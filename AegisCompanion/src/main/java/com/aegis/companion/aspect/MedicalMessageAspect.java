// file: aspect/MedicalMessageAspect.java
package com.aegis.companion.aspect;

import com.aegis.companion.model.dto.notification.MedicalNotification;
import com.aegis.companion.model.enums.MedicalEventType;
import com.aegis.companion.model.vo.AppointmentVO;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Aspect
@Component
@RequiredArgsConstructor
public class MedicalMessageAspect {
    private final RabbitTemplate rabbitTemplate;

    @AfterReturning(
            pointcut = "execution(public * com.aegis.companion.service.impl.AppointmentServiceImpl.*(..))",
            returning = "result"
    )
    public void sendMedicalMessage(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        MedicalEventType eventType = parseEventType(methodName);

        if (eventType != null &&
                result instanceof AppointmentVO
        ) {
            String routingKey = "medical.message." + eventType.getCode().toLowerCase().replace("_", ".");

            rabbitTemplate.convertAndSend(
                    "medical.exchange",
                    routingKey,
                    MedicalNotification.build(eventType, (AppointmentVO) result)
            );
        }
    }

    private MedicalEventType parseEventType(String methodName) {
        return switch (methodName) {
            case "createAppointment" -> MedicalEventType.APPOINTMENT_CREATED;
            case "cancelAppointment" -> MedicalEventType.APPOINTMENT_CANCELED;
            case "confirmCompletion" -> MedicalEventType.APPOINTMENT_CONFIRMED;
            default -> null;
        };
    }

}
