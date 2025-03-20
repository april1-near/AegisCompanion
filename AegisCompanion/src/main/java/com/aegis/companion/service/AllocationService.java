package com.aegis.companion.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface AllocationService {


    @Async
    void autoAssignWorker(Long ticketId);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void adjustWorkerLoad(Long workerId, int delta, Integer version);
}
