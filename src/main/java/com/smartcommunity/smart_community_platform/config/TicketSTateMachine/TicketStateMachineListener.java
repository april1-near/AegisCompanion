package com.smartcommunity.smart_community_platform.config.TicketSTateMachine;

import com.smartcommunity.smart_community_platform.dao.TicketMapper;
import com.smartcommunity.smart_community_platform.model.dto.notification.TicketNotification;
import com.smartcommunity.smart_community_platform.model.entity.Ticket;
import com.smartcommunity.smart_community_platform.model.enums.TicketEvent;
import com.smartcommunity.smart_community_platform.model.enums.TicketState;
import com.smartcommunity.smart_community_platform.model.vo.TicketVO;
import com.smartcommunity.smart_community_platform.service.AllocationService;
import com.smartcommunity.smart_community_platform.service.TicketLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class TicketStateMachineListener
        extends StateMachineListenerAdapter<TicketState, TicketEvent> {

    private final TicketLogService logService;
    private final AllocationService allocationService;
    private final TicketMapper ticketMapper;
    //    private final TicketEventNotifier eventNotifier;
    private final RabbitTemplate rabbitTemplate;


    @Override
    public void stateContext(StateContext<TicketState, TicketEvent> stateContext) {
        // 仅处理状态变更事件
        if (stateContext.getStage() == StateContext.Stage.TRANSITION_END) {

            // 获取状态机实例
            StateMachine<TicketState, TicketEvent> stateMachine = stateContext.getStateMachine();


            // 获取工单ID（状态机ID即为工单ID）
            Long ticketId = Long.parseLong(stateMachine.getId());
            Ticket ticket = ticketMapper.selectById(ticketId);


            // 操作人ID从消息头获取
            Long operatorId = stateContext.getMessageHeaders().get("operatorId", Long.class);
            String remark = stateContext.getMessageHeaders().get("logRemark", String.class);
            // 获取状态变化信息

            State<TicketState, TicketEvent> sourceState = stateContext.getSource();
            TicketState from = (sourceState != null) ? sourceState.getId() : TicketState.CREATED;
            TicketState to = stateContext.getTarget().getId();

            if (remark == null || remark.isEmpty()) {
                remark = "状态变更：" + from.getDesc() + "→" + to.getDesc();
            }
            // 记录日志（过滤空转换）
            if (from != to) {
                logService.logStateChange(
                        ticketId,
                        from,
                        to,
                        operatorId,
                        remark
                );


                TicketVO ticketVO = TicketVO.fromEntity(ticket);

                if (from == TicketState.CREATED && to == TicketState.AUTO_ASSIGNED) {
                    ticketVO.setAssigneeId(ticket.getAssigneeId());
                }
                if (from == TicketState.REVIEW_PENDING && to == TicketState.REVIEW_FAILED) {
                    ticketVO.setReviewerId(operatorId);
                    ticketVO.setRemake(remark);
                }
                String routingKey = "ticket.message." + to.getCode().toLowerCase().replace("_", ".");

                rabbitTemplate.convertAndSend(
                        "ticket.exchange",
                        routingKey,
                        TicketNotification.build(to, ticketVO)
                );

                if (to!= TicketState.AUTO_ASSIGNED)handTicketUpdateStatus(ticket, to);

                // 新增工单完成时负载处理
                handleWorkloadAdjustment(stateContext);
            }
        }
    }

    private void handTicketUpdateStatus(Ticket ticket, TicketState to) {
        ticket.setState(to);
        ticket.setUpdateTime(LocalDateTime.now());
        ticketMapper.updateAssignmentWithLock(ticket);
    }

    private void handleWorkloadAdjustment(StateContext<TicketState, TicketEvent> context) {
        TicketState targetState = context.getTarget().getId();
        if (targetState == TicketState.COMPLETED || targetState == TicketState.CLOSED) {
            Long ticketId = Long.parseLong(context.getStateMachine().getId());
            Ticket ticket = ticketMapper.selectById(ticketId);

            if (ticket != null && ticket.getAssigneeId() != null) {
                try {
                    allocationService.adjustWorkerLoad(ticket.getAssigneeId(), -1, ticket.getVersion());
                } catch (Exception e) {
                    log.error("工人负载调整失败 workerId={}", ticket.getAssigneeId(), e);
                }


            }
        }
    }


}
