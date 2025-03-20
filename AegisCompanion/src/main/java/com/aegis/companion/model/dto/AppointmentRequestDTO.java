package com.aegis.companion.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AppointmentRequestDTO {
    @NotNull(message = "医生ID不能为空/L'ID du médecin est requis")
    private Long doctorId;

    @FutureOrPresent
    private LocalDate appointDate;

    @Pattern(regexp = "\\d{2}:\\d{2}-\\d{2}:\\d{2}")
    private String timeSlot;
}