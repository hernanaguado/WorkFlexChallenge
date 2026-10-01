package com.hernanaguado.backend.workation;

import com.hernanaguado.backend.workation.dto.WorkationDto;
import com.hernanaguado.backend.workation.model.RiskLevel;
import com.hernanaguado.backend.workation.model.Workation;

import java.time.LocalDate;

/**
 * Shared test data. The three workations mirror rows w1, w4 and w5 of {@code workations.csv}.
 */
public final class WorkationFixtures {

    public static final String WORKATION_ENDPOINT = "/workflex/workation";
    public static final String WARN_LEVEL = "WARN";
    public static final String SKIPPED_ROW_LOG = "Skipping invalid workation at CSV line ";
    public static final String UNKNOWN_RISK = "MEDIUM";

    public static final String GERMANY = "Germany";
    public static final String GERMANY_CODE = "de";
    public static final String UNITED_STATES = "United States";
    public static final String UNITED_STATES_CODE = "us";
    public static final String GREECE = "Greece";
    public static final String GREECE_CODE = "gr";
    public static final String INDIA = "India";
    public static final String INDIA_CODE = "in";
    public static final String UNKNOWN_COUNTRY = "Atlantis";

    public static final String HIGH_RISK_ID = "w1";
    public static final String HIGH_RISK_EMPLOYEE = "Steffen Jacobs";
    public static final LocalDate HIGH_RISK_START = LocalDate.of(2024, 1, 2);
    public static final LocalDate HIGH_RISK_END = LocalDate.of(2024, 12, 31);
    public static final int HIGH_RISK_WORKING_DAYS = 65;

    public static final String LOW_RISK_ID = "w4";
    public static final String LOW_RISK_EMPLOYEE = "Andre Fischer";
    public static final LocalDate LOW_RISK_START = LocalDate.of(2023, 5, 22);
    public static final LocalDate LOW_RISK_END = LocalDate.of(2023, 6, 30);
    public static final int LOW_RISK_WORKING_DAYS = 50;

    public static final String NO_RISK_ID = "w5";
    public static final String NO_RISK_EMPLOYEE = "Ayushi Singh";
    public static final LocalDate NO_RISK_START = LocalDate.of(2023, 3, 13);
    public static final LocalDate NO_RISK_END = LocalDate.of(2023, 4, 30);
    public static final int NO_RISK_WORKING_DAYS = 35;

    private WorkationFixtures() {
    }

    /** w1: Germany → United States, HIGH. */
    public static Workation highRiskWorkation() {
        return new Workation(HIGH_RISK_ID, HIGH_RISK_EMPLOYEE, GERMANY, UNITED_STATES,
                HIGH_RISK_START, HIGH_RISK_END, HIGH_RISK_WORKING_DAYS, RiskLevel.HIGH);
    }

    public static WorkationDto highRiskWorkationDto() {
        return new WorkationDto(HIGH_RISK_ID, HIGH_RISK_EMPLOYEE, GERMANY, GERMANY_CODE, UNITED_STATES,
                UNITED_STATES_CODE, HIGH_RISK_START, HIGH_RISK_END, HIGH_RISK_WORKING_DAYS, RiskLevel.HIGH);
    }

    /** w4: Germany → Greece, LOW. */
    public static Workation lowRiskWorkation() {
        return new Workation(LOW_RISK_ID, LOW_RISK_EMPLOYEE, GERMANY, GREECE,
                LOW_RISK_START, LOW_RISK_END, LOW_RISK_WORKING_DAYS, RiskLevel.LOW);
    }

    public static WorkationDto lowRiskWorkationDto() {
        return new WorkationDto(LOW_RISK_ID, LOW_RISK_EMPLOYEE, GERMANY, GERMANY_CODE, GREECE,
                GREECE_CODE, LOW_RISK_START, LOW_RISK_END, LOW_RISK_WORKING_DAYS, RiskLevel.LOW);
    }

    /** w5: Germany → India, NO. */
    public static Workation noRiskWorkation() {
        return new Workation(NO_RISK_ID, NO_RISK_EMPLOYEE, GERMANY, INDIA,
                NO_RISK_START, NO_RISK_END, NO_RISK_WORKING_DAYS, RiskLevel.NO);
    }
}
