package com.aegis.companion.service;

import com.aegis.companion.model.entity.Ticket;
import com.aegis.companion.model.enums.TicketEvent;
import com.aegis.companion.model.enums.TicketState;
import com.aegis.companion.model.vo.TicketVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachineException;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 状态机核心服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketStateMachineService {
    @Qualifier("ticketStateMachineFactory")
    private final StateMachineFactory<TicketState, TicketEvent> stateMachineFactory;
    private final StateMachinePersister<TicketState, TicketEvent, String> persist;


    /**
     * 初始化状态机（工单创建时调用）
     */
    public void initStateMachine(Ticket ticket) {
        StateMachine<TicketState, TicketEvent> sm = stateMachineFactory.getStateMachine(ticket.getId().toString());
        sm.start();

        TicketVO ticketVO = TicketVO.fromEntity(ticket);
        sm.getExtendedState().getVariables().put("operatorId", ticket.getUserId());
        sm.getExtendedState().getVariables().put("ticketVO", ticketVO);
        log.info("init：初始化上下文：{}", sm.getExtendedState().getVariables());
        try {
            persist.persist(sm, ticket.getId().toString());
        } catch (Exception e) {
            throw new StateMachineException("状态机初始化失败", e);
        }
    }

    public void sendEvent(Long ticketId, TicketEvent event, Long operatorId) {
        StateMachine<TicketState, TicketEvent> sm = stateMachineFactory.getStateMachine(ticketId.toString());
        try {
            persist.restore(sm, ticketId.toString());

            // 防御性初始化
            if (sm.getExtendedState().getVariables() == null) {
                sm.getExtendedState().getVariables().putAll(new ConcurrentHashMap<>());
            }

            // 更新操作人信息
            sm.getExtendedState().getVariables().put("operatorId", operatorId);

            // 发送携带完整上下文的事件
            Message<TicketEvent> message = MessageBuilder.createMessage(event,
                    new MessageHeaders(Collections.singletonMap("operatorId", operatorId))
            );
            sm.sendEvent(message);

            persist.persist(sm, ticketId.toString());
        } catch (Exception e) {
            throw new StateMachineException("状态机操作失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取当前状态（带持久化恢复）
     */
    public TicketState getCurrentState(Long ticketId) {
        StateMachine<TicketState, TicketEvent> sm = stateMachineFactory.getStateMachine(ticketId.toString());
        try {
            persist.restore(sm, ticketId.toString());
            return sm.getState().getId();
        } catch (Exception e) {
            return TicketState.CREATED;
        }
    }

    public StateMachine<TicketState, TicketEvent> getStateMachine(Long ticketId) {
        StateMachine<TicketState, TicketEvent> sm = stateMachineFactory.getStateMachine(ticketId.toString());
        try {
            persist.restore(sm, ticketId.toString());
        } catch (Exception e) {
            throw new StateMachineException("工单状态不存在");
        }
        return sm;
    }
}
