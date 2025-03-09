package com.smartcommunity.smart_community_platform.service;

import com.smartcommunity.smart_community_platform.model.dto.TicketCreateDTO;
import com.smartcommunity.smart_community_platform.model.vo.TicketVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TicketService {
    @Transactional
    void createTicket(TicketCreateDTO dto, Long userId);

    void startProcessing(Long ticketId, Long operatorId);

    void submitForReview(Long ticketId, Long operatorId);

    void handleReview(Long ticketId, boolean isApproved, String remark, Long operatorId);

    void confirmCompletion(Long ticketId, Long userId);

    List<TicketVO> getUserTickets(Long userId);

    List<TicketVO> getWorkerTickets(Long assigneeId);
}
