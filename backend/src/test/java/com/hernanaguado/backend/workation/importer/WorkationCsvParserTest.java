package com.hernanaguado.backend.workation.importer;

import com.hernanaguado.backend.workation.model.RiskLevel;
import com.hernanaguado.backend.workation.model.Workation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.io.StringReader;
import java.util.List;

import static com.hernanaguado.backend.workation.WorkationFixtures.GERMANY;
import static com.hernanaguado.backend.workation.WorkationFixtures.HIGH_RISK_EMPLOYEE;
import static com.hernanaguado.backend.workation.WorkationFixtures.HIGH_RISK_END;
import static com.hernanaguado.backend.workation.WorkationFixtures.HIGH_RISK_ID;
import static com.hernanaguado.backend.workation.WorkationFixtures.HIGH_RISK_START;
import static com.hernanaguado.backend.workation.WorkationFixtures.HIGH_RISK_WORKING_DAYS;
import static com.hernanaguado.backend.workation.WorkationFixtures.NO_RISK_ID;
import static com.hernanaguado.backend.workation.WorkationFixtures.SKIPPED_ROW_LOG;
import static com.hernanaguado.backend.workation.WorkationFixtures.UNITED_STATES;
import static com.hernanaguado.backend.workation.WorkationFixtures.UNKNOWN_RISK;
import static com.hernanaguado.backend.workation.WorkationFixtures.WARN_LEVEL;
import static com.hernanaguado.backend.workation.WorkationFixtures.highRiskWorkation;
import static com.hernanaguado.backend.workation.WorkationFixtures.noRiskWorkation;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class WorkationCsvParserTest {

    private static final String HEADER = "workationId,employee,origin,destination,start,end,workingDays,risk\n";
    private static final String INVALID_DATE = "02/01/2024";

    private final WorkationCsvParser parser = new WorkationCsvParser();

    @Test
    void shouldMapEveryCsvColumnToTheEntity() {
        // given
        String csv = HEADER + csvRow(highRiskWorkation()) + csvRow(noRiskWorkation());

        // when
        List<Workation> workations = parser.parse(new StringReader(csv));

        // then
        assertThat(workations).hasSize(2);
        Workation first = workations.getFirst();
        assertThat(first.getWorkationId()).isEqualTo(HIGH_RISK_ID);
        assertThat(first.getEmployee()).isEqualTo(HIGH_RISK_EMPLOYEE);
        assertThat(first.getOrigin()).isEqualTo(GERMANY);
        assertThat(first.getDestination()).isEqualTo(UNITED_STATES);
        assertThat(first.getStart()).isEqualTo(HIGH_RISK_START);
        assertThat(first.getEnd()).isEqualTo(HIGH_RISK_END);
        assertThat(first.getWorkingDays()).isEqualTo(HIGH_RISK_WORKING_DAYS);
        assertThat(first.getRisk()).isEqualTo(RiskLevel.HIGH);
        assertThat(workations.get(1).getRisk()).isEqualTo(RiskLevel.NO);
    }

    @Test
    void shouldReturnEmptyListWhenCsvHasOnlyHeader() {
        assertThat(parser.parse(new StringReader(HEADER))).isEmpty();
    }

    @Test
    void shouldSkipRowWithInvalidDateAndLogItsLineNumber(CapturedOutput output) {
        // given
        String csv = HEADER
                + csvRow(highRiskWorkation(), INVALID_DATE, RiskLevel.HIGH.name())
                + csvRow(noRiskWorkation());

        // when
        List<Workation> workations = parser.parse(new StringReader(csv));

        // then
        assertThat(workations).extracting(Workation::getWorkationId).containsExactly(NO_RISK_ID);
        assertThat(output).contains(WARN_LEVEL).contains(SKIPPED_ROW_LOG + 2);
    }

    @Test
    void shouldLogPhysicalLineNumberWhenCsvContainsBlankLines(CapturedOutput output) {
        // given
        String csv = HEADER
                + "\n"
                + csvRow(highRiskWorkation(), HIGH_RISK_START.toString(), UNKNOWN_RISK);

        // when & then
        assertThat(parser.parse(new StringReader(csv))).isEmpty();
        assertThat(output).contains(SKIPPED_ROW_LOG + 3);
    }

    @Test
    void shouldSkipRowWithUnknownRiskAndLogItsLineNumber(CapturedOutput output) {
        // given
        String csv = HEADER
                + csvRow(highRiskWorkation())
                + csvRow(noRiskWorkation(), noRiskWorkation().getStart().toString(), UNKNOWN_RISK);

        // when
        List<Workation> workations = parser.parse(new StringReader(csv));

        // then
        assertThat(workations).extracting(Workation::getWorkationId).containsExactly(HIGH_RISK_ID);
        assertThat(output).contains(SKIPPED_ROW_LOG + 3).contains(UNKNOWN_RISK);
    }

    private static String csvRow(Workation workation) {
        return csvRow(workation, workation.getStart().toString(), workation.getRisk().name());
    }

    private static String csvRow(Workation workation, String start, String risk) {
        return String.join(",",
                workation.getWorkationId(),
                workation.getEmployee(),
                workation.getOrigin(),
                workation.getDestination(),
                start,
                workation.getEnd().toString(),
                String.valueOf(workation.getWorkingDays()),
                risk) + "\n";
    }
}
