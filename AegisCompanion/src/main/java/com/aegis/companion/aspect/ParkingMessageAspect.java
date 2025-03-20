package com.aegis.companion.aspect;

import com.aegis.companion.model.dto.notification.ParkingNotification;
import com.aegis.companion.model.entity.ParkingReservation;
import com.aegis.companion.model.enums.ParkingEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ParkingMessageAspect {
    private final RabbitTemplate rabbitTemplate;

    // 切入点：ParkingReservationCoreServiceImpl中所有public方法
    @Pointcut("execution(public * com.aegis.companion.service.impl.ParkingReservationCoreServiceImpl.*(..))")
    public void parkingServiceMethods() {
    }

    // 后置通知
    @AfterReturning(pointcut = "parkingServiceMethods()", returning = "result")
    public void sendParkingEvent(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        ParkingEventType eventType = parseEventType(methodName);


        if (result instanceof ParkingReservation &&
                eventType != null
        ) {

            String routingKey = "parking.message." + eventType.getCode().toLowerCase().replace("_", ".");
            rabbitTemplate.convertAndSend(
                    "parking.exchange",
                    routingKey,
                    ParkingNotification.build(eventType, (ParkingReservation) result)
            );
            log.info("发送消息到路由键: {}", routingKey); // 添加日志

        }
    }

    private ParkingEventType parseEventType(String methodName) {
        return switch (methodName) {
            case "createReservation" -> ParkingEventType.RESERVE;
            case "cancelReservation" -> ParkingEventType.RESERVE_CANCELED;
            case "confirmOccupancy" -> ParkingEventType.OCCUPY;
            case "autoReleaseExpiredReservations" -> ParkingEventType.ADMIN_RELEASE;
            default -> null;
        };
    }
}
