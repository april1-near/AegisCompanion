package com.smartcommunity.smart_community_platform.service.impl;

import com.smartcommunity.smart_community_platform.model.dto.TicketCreateDTO;
import com.smartcommunity.smart_community_platform.model.enums.TicketEvent;
import com.smartcommunity.smart_community_platform.service.TicketService;
import com.smartcommunity.smart_community_platform.service.TicketStateMachineService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TicketServiceImplTest {
    @Autowired
    private TicketService service;
    @Autowired
    private TicketStateMachineService ticketStateMachineService;

    @Test
    public void createTest() {

        TicketCreateDTO ticketCreateDTO = new TicketCreateDTO();

        ticketCreateDTO.setDescription("路灯不亮2");
        ticketCreateDTO.setTitle("楼道灯不亮");
        ticketCreateDTO.setType("电路维修");
        service.createTicket(ticketCreateDTO, 2004L);
    }

    @Test
    public void EventTest(){
        ticketStateMachineService.sendEvent(25L, TicketEvent.SUBMIT_REVIEW,2004L);
    }


}