package com.smartcommunity.smart_community_platform.service;

import com.smartcommunity.smart_community_platform.model.enums.TicketState;
import org.springframework.scheduling.annotation.Async;

public interface TicketLogService {

    @Async
    void logStateChange(Long ticketId, TicketState fromState, TicketState toState,
                        Long operatorId, String remark);
}
