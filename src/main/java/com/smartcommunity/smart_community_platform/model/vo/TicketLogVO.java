package com.smartcommunity.smart_community_platform.model.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.smartcommunity.smart_community_platform.model.entity.TicketLog;
import com.smartcommunity.smart_community_platform.utils.BeanCopyUtils;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单日志VO
 */
@Data
public class TicketLogVO {
    private String fromState;
    private String toState;
    @JsonFormat(pattern = "MM-dd HH:mm")

    private LocalDateTime createTime;
    private String remark;

    public static TicketLogVO fromEntity(TicketLog log) {
        TicketLogVO logVo = new TicketLogVO();
        logVo.setFromState(log.getFromState().getDesc());
        logVo.setToState(log.getToState().getDesc());
        logVo.setCreateTime(log.getCreateTime());
        logVo.setRemark(log.getRemark());
        return logVo;
    }

}