package com.smartcommunity.smart_community_platform.service.impl;


import com.smartcommunity.smart_community_platform.dao.TicketLogMapper;
import com.smartcommunity.smart_community_platform.model.entity.TicketLog;
import com.smartcommunity.smart_community_platform.model.enums.TicketState;
import com.smartcommunity.smart_community_platform.service.TicketLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 工单日志服务
 */
@Service
@RequiredArgsConstructor
public class TicketLogServiceImpl implements TicketLogService {
    private final TicketLogMapper logMapper;

    /**
     * 记录状态变更日志
     *
     * @param ticketId   工单ID
     * @param fromState  原状态
     * @param toState    新状态
     * @param operatorId 操作人ID
     * @param remark     备注
     */

    @Async
    @Override
    public void logStateChange(Long ticketId, TicketState fromState, TicketState toState,
                               Long operatorId, String remark) {
        TicketLog log = new TicketLog();
        log.setTicketId(ticketId);
        log.setFromState(fromState);
        log.setToState(toState);
        log.setOperatorId(operatorId);
        log.setRemark(remark);
        logMapper.insert(log);
    }
}