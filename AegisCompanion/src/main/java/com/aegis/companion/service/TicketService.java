package com.aegis.companion.service;

import com.aegis.companion.model.dto.ReviewHandleDTO;
import com.aegis.companion.model.dto.TicketCreateDTO;
import com.aegis.companion.model.vo.TicketVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TicketService {
    @Transactional
    void createTicket(TicketCreateDTO dto, Long userId);

    void startProcessing(Long ticketId, Long operatorId);

    void submitForReview(Long ticketId, Long operatorId);

    List<TicketVO> getAllTickets();


    void handleReview(Long ticketId, ReviewHandleDTO dto, Long operatorId);

    void confirmCompletion(Long ticketId, Long userId);

    List<TicketVO> getUserTickets(Long userId);

    List<TicketVO> getWorkerTickets(Long assigneeId);
}
