package com.aegis.companion.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// file: service/MessageTemplateService.java
@Service
public class MessageTemplateService {
    // 使用线程安全的ConcurrentHashMap
    private static final Map<String, String> TEMPLATES = new ConcurrentHashMap<>() {{
        put("PARK_RESERVE", "您已成功预约【%d】号车位，有效期至%s");
        put("PARK_RESERVE_CANCELED", "已取消【%d】号车位的预约，原有效期至%s");
        put("PARK_OCCUPY", "已确认到达【%d】号车位");
        put("PARK_ADMIN_RELEASE", "管理员已强制释放【%d】号车位");
        put("PARK_TIMEOUT_RELEASE", "您预约的【%d】号车位已因超时自动释放");
        /*==================================================================*/
        put("MEDICAL_CREATED", "您已成功预约【%s】医生的就诊，预约日期为%s，时间段为%s");
        put("MEDICAL_CANCELED", "您已取消【%s】医生的就诊预约，原预约日期为%s，时间段为%s");
        put("MEDICAL_CONFIRMED", "您已确认【%s】医生的就诊，预约日期为%s，时间段为%s");
        put("MEDICAL_REMINDER", "温馨提示：您预约的【%s】医生的就诊即将开始，预约日期为%s，时间段为%s");
        /*==================================================================*/
        put("BOOKING_PENDING", "您的【%s】场地预约申请已提交（时间段：%s），等待审批");
        put("BOOKING_APPROVED", "您的【%s】场地预约已通过（时间段：%s），请按时使用");
        put("BOOKING_REJECTED", "您的【%s】场地预约申请未通过（时间段：%s）。拒绝原因：%s");
        put("BOOKING_COMPLETED", "您已完成【%s】场地的使用（时间段：%s），感谢您的配合");
        put("BOOKING_CANCELED", "您已取消【%s】场地的预约（原时间段：%s）");
        /*==================================================================*/
        put("WORK_ORDER_CREATED", "【工单创建】%s 已提交新工单（ID：%d）\n描述：%s\n优先级：%s");
        put("WORK_ORDER_ASSIGNED", "【工单分配】%s 已将工单（ID：%d）分配给您\n问题描述：%s");
        put("WORK_ORDER_PROCESSING", "【处理进展】%s 正在处理您的工单（ID：%d）\n当前状态：%s");
        put("WORK_ORDER_NEED_REVIEW", "【待审核】工单（ID：%d）已完成处理，等待 %s 审核");
        put("WORK_ORDER_CONFIRMED", "【用户确认】%s 已确认工单（ID：%d）完成");
        put("WORK_ORDER_CLOSED", "【工单关闭】您的工单（ID：%d）已关闭\n处理人：%s");
        put("WORK_ORDER_CANCELLED", "【工单取消】%s 取消了工单（ID：%d）\n原因：%s");
        /*==================================================================*/
        put("TICKET_AUTO_ASSIGNED","【新工单】您有新的工单待处理！工单号：{%d}，类型{%s}，提交时间{%s}");
        put("TICKET_REVIEW_PENDING","【待审核】工单 {#%d} 已处理完毕，请及时审核。提交人：{%s}");
        put("TICKET_REVIEW_FAILED","【需重新处理】工单 {#%d} 审核未通过，原因{%s}");
        put("TICKET_COMPLETED","【已完成】您的工单 {%d} 已确认完成，感谢使用！");
    }};

    public String getTemplate(String code) {

        return TEMPLATES.getOrDefault(code, "【系统通知】暂时无法获取详细信息");

    }

    // 添加动态更新能力
    public void updateTemplate(String code, String newTemplate) {
        TEMPLATES.put(code, newTemplate);
    }
}
