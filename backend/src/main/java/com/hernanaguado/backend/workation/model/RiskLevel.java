package com.hernanaguado.backend.workation.model;

import java.util.Locale;

public enum RiskLevel {
    HIGH,
    LOW,
    NO;

    private static final String RISK_SUFFIX = "_RISK";

    /**
     * Parses a risk value from the CSV. Accepts the CSV values (HIGH, LOW, NO)
     * as well as the *_RISK naming used in the specification, case-insensitively.
     */
    public static RiskLevel fromCsv(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Risk level must not be blank");
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.endsWith(RISK_SUFFIX)) {
            normalized = normalized.substring(0, normalized.length() - RISK_SUFFIX.length());
        }
        try {
            return valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown risk level: " + value, ex);
        }
    }
}