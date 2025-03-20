package com.aegis.companion.model.vo;

import com.aegis.companion.model.entity.Ticket;
import com.aegis.companion.utils.BeanCopyUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工单详情VO
 */
@Data
public class TicketVO {
    private Long id;
    private String title;
    private String description;
    private String type;

    private Long userId;
    private Long assigneeId;
    private Long reviewerId;
    private String remake;

    private String stateDesc;
    //创建人姓名
    private String creatorName;
    //处理人姓名
    private String assigneeName;

    //2025-03-05 01:17:30
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;

    private List<TicketLogVO> logs;

    public static TicketVO fromEntity(Ticket ticket) {
        TicketVO vo = new TicketVO();
        BeanCopyUtils.copyNonNullProperties(ticket, vo);

        return vo;
    }
}
