package com.hernanaguado.backend.workation.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.hernanaguado.backend.workation.WorkationFixtures.UNKNOWN_RISK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class RiskLevelTest {

    @ParameterizedTest
    @CsvSource({
            "HIGH, HIGH",
            "high, HIGH",
            "LOW, LOW",
            "low, LOW",
            "LOW_RISK, LOW",
            "NO, NO",
            "no_risk, NO",
            "'  NO  ', NO"
    })
    void shouldParseCsvValuesAndSpecNamingCaseInsensitively(String input, RiskLevel expected) {
        assertThat(RiskLevel.fromCsv(input)).isEqualTo(expected);
    }

    @Test
    void shouldRejectUnknownRiskLevel() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> RiskLevel.fromCsv(UNKNOWN_RISK))
                .withMessageContaining(UNKNOWN_RISK);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "   ")
    void shouldRejectBlankRiskLevel(String input) {
        assertThatIllegalArgumentException().isThrownBy(() -> RiskLevel.fromCsv(input));
    }
}