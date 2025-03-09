// file: aspect/ActivityMessageAspect.java
package com.smartcommunity.smart_community_platform.aspect;

import com.smartcommunity.smart_community_platform.model.dto.BookingApproveDTO;
import com.smartcommunity.smart_community_platform.model.dto.notification.RoomBookingNotification;
import com.smartcommunity.smart_community_platform.model.enums.BookingStatusEnum;
import com.smartcommunity.smart_community_platform.model.vo.BookingRecordVO;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ActivityMessageAspect {
    private final RabbitTemplate rabbitTemplate;

    @AfterReturning(
            pointcut = "execution(public * com.smartcommunity.smart_community_platform.service.impl.RoomBookingServiceImpl.*(..))",
            returning = "result"
    )
    public void sendActivityMessage(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        BookingStatusEnum eventType = parseEventType(methodName, joinPoint.getArgs());

        if (eventType != null &&
                result instanceof BookingRecordVO
        ) {
            String routingKey = "activity.message." + eventType.getCode().toLowerCase().replace("_", ".");

            rabbitTemplate.convertAndSend(
                    "activity.exchange",
                    routingKey,
                    RoomBookingNotification.build(eventType, (BookingRecordVO) result)
            );
        }
    }

    private BookingStatusEnum parseEventType(String methodName, Object[] args) {
        return switch (methodName) {
            case "createBooking" -> BookingStatusEnum.PENDING;
            case "approveBooking" -> {
                BookingApproveDTO dto = (BookingApproveDTO) args[1];
                yield dto.getApproved() ?
                        BookingStatusEnum.APPROVED :
                        BookingStatusEnum.REJECTED;
            }
            case "cancelBooking" -> BookingStatusEnum.CANCELED;
            default -> null;
        };
    }

}
