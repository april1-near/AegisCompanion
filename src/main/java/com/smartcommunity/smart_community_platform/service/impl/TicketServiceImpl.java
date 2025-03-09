package com.smartcommunity.smart_community_platform.service.impl;

import com.smartcommunity.smart_community_platform.dao.TicketLogMapper;
import com.smartcommunity.smart_community_platform.dao.TicketMapper;
import com.smartcommunity.smart_community_platform.model.dto.TicketCreateDTO;
import com.smartcommunity.smart_community_platform.model.entity.Ticket;
import com.smartcommunity.smart_community_platform.model.entity.TicketLog;
import com.smartcommunity.smart_community_platform.model.enums.TicketEvent;
import com.smartcommunity.smart_community_platform.model.enums.TicketState;
import com.smartcommunity.smart_community_platform.model.vo.TicketLogVO;
import com.smartcommunity.smart_community_platform.model.vo.TicketVO;
import com.smartcommunity.smart_community_platform.service.TicketService;
import com.smartcommunity.smart_community_platform.service.TicketStateMachineService;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 工单服务（状态机驱动版本）
 */
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketMapper ticketMapper;
    private final TicketLogMapper ticketLogMapper;
    private final TicketStateMachineService stateMachineService;

    /**
     * 创建工单并初始化状态机
     */
    @Transactional
    @Override
    public void createTicket(TicketCreateDTO dto, Long userId) {
        // 创建工单实体
        Ticket ticket = new Ticket();
        ticket.setTitle(dto.getTitle());
        ticket.setDescription(dto.getDescription());
        ticket.setType(dto.getType());
        ticket.setUserId(userId);
        ticket.setState(TicketState.CREATED);
        ticketMapper.insert(ticket);

        //
        stateMachineService.initStateMachine(ticket);

        // 触发自动分配（通过状态机事件驱动）
        stateMachineService.sendEvent(ticket.getId(),
                TicketEvent.AUTO_ASSIGN,
                userId); // 系统自动操作
    }

    /**
     * 工人开始处理
     * @param ticketId 工单id
     * @param operatorId 操作人
     */

    @Override
    public void startProcessing(Long ticketId, Long operatorId) {
        Ticket ticket = getTicketWithLock(ticketId);

        // 验证操作权限
        if (!Objects.equals(ticket.getAssigneeId(), operatorId)) {
            throw new SecurityException("仅分配人员可操作此工单");
        }
        validateState(ticket,TicketState.AUTO_ASSIGNED);

        // 发送状态机事件
        stateMachineService.sendEvent(
                ticketId,
                TicketEvent.MANUAL_PROCESS,
                operatorId
        );
    }

    /**
     * 提交审核（Worker触发）
     */
    @Override
    public void submitForReview(Long ticketId, Long operatorId) {
        Ticket ticket = getTicketWithLock(ticketId);
        validateState(ticket, TicketState.PROCESSING);

        // 携带审核结果参数
        stateMachineService.sendEvent(ticketId,
                TicketEvent.SUBMIT_REVIEW,
                operatorId);
    }

    /**
     * 处理审核结果（Admin触发）
     */
    @Override
    public void handleReview(Long ticketId, boolean isApproved, String remark, Long operatorId) {
        Ticket ticket = getTicketWithLock(ticketId);
//        validateReviewState(ticket);

        TicketEvent event = isApproved ?
                TicketEvent.REVIEW_PASS :
                TicketEvent.REVIEW_REJECT;

        // 携带审核备注
        stateMachineService.getStateMachine(ticketId)
                .sendEvent(MessageBuilder.withPayload(event)
                        .setHeader("reviewResult", isApproved)
                        .setHeader("logRemark", remark)
                        .build());
    }

    /**
     * 用户确认完成
     */
    @Override
    public void confirmCompletion(Long ticketId, Long userId) {
        Ticket ticket = getTicketWithLock(ticketId);
        if (!ticket.getUserId().equals(userId)) {
            throw new SecurityException("无权操作他人工单");
        }
        validateState(ticket, TicketState.REVIEW_PASSED);

        stateMachineService.sendEvent(ticketId,
                TicketEvent.USER_CONFIRM,
                userId);
    }

    private Ticket getTicketWithLock(Long ticketId) {
        Ticket ticket = ticketMapper.selectByIdWithLock(ticketId);
        if (ticket == null) try {
            throw new NotFoundException("工单不存在");
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        return ticket;
    }

    private void validateState(Ticket ticket, TicketState expected) {
        if (ticket.getState() != expected) {
            throw new IllegalStateException("工单当前状态不允许此操作");
        }
    }


    /**
     * 获取用户所有工单
     */
    @Override
    public List<TicketVO> getUserTickets(Long userId) {
        List<Ticket> tickets = ticketMapper.selectByUserIdWithNames(userId);
        return convertToVOWithLogs(tickets);
    }

    /**
     * 获取工人处理的所有工单
     */
    @Override
    public List<TicketVO> getWorkerTickets(Long assigneeId) {
        List<Ticket> tickets = ticketMapper.selectByAssigneeIdWithNames(assigneeId);
        return convertToVOWithLogs(tickets);
    }

    /**
     * 转换工单实体为VO并填充日志
     */
    private List<TicketVO> convertToVOWithLogs(List<Ticket> tickets) {
        if (CollectionUtils.isEmpty(tickets)) return Collections.emptyList();

        // 批量获取相关日志
        List<Long> ticketIds = tickets.stream().map(Ticket::getId).toList();
        List<TicketLog> logs = ticketLogMapper.selectByTicketIds(ticketIds);
        Map<Long, List<TicketLog>> logMap = logs.stream()
                .collect(Collectors.groupingBy(TicketLog::getTicketId));

        // 批量获取操作人信息
        Set<Long> operatorIds = logs.stream().map(TicketLog::getOperatorId).collect(Collectors.toSet());


        return tickets.stream().map(ticket -> {
            TicketVO vo = TicketVO.fromEntity(ticket);
            vo.setCreatorName(ticket.getCreatorName());
            vo.setAssigneeName(ticket.getAssigneeName());
            vo.setStateDesc(ticket.getState().getDesc()); // 状态转换

            // 填充日志
            List<TicketLogVO> logVOs = Optional.ofNullable(logMap.get(ticket.getId()))
                    .orElse(Collections.emptyList()).stream()
                    .map(log -> {
                        TicketLogVO logVO = TicketLogVO.fromEntity(log);
                        return logVO;
                    }).toList();
            vo.setLogs(logVOs);

            return vo;
        }).toList();
    }

}
