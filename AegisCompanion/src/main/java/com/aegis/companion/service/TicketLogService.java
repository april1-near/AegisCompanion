package com.aegis.companion.service;

import com.aegis.companion.model.enums.TicketState;
import org.springframework.scheduling.annotation.Async;

public interface TicketLogService {

    @Async
    void logStateChange(Long ticketId, TicketState fromState, TicketState toState,
                        Long operatorId, String remark);
}
