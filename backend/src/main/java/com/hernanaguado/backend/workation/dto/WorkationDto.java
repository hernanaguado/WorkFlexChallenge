package com.hernanaguado.backend.workation.dto;

import com.hernanaguado.backend.workation.model.RiskLevel;

import java.time.LocalDate;

public record WorkationDto(
        String workationId,
        String employee,
        String origin,
        String originCode,
        String destination,
        String destinationCode,
        LocalDate start,
        LocalDate end,
        int workingDays,
        RiskLevel risk) {
}