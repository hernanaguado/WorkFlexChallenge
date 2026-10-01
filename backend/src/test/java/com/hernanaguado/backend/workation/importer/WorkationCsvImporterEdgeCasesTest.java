package com.hernanaguado.backend.workation.importer;

import com.hernanaguado.backend.country.CountryCodeResolver;
import com.hernanaguado.backend.workation.dto.WorkationDto;
import com.hernanaguado.backend.workation.mapper.WorkationMapper;
import com.hernanaguado.backend.workation.model.RiskLevel;
import com.hernanaguado.backend.workation.model.Workation;
import com.hernanaguado.backend.workation.repository.WorkationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.io.ClassPathResource;

import java.time.LocalDate;
import java.util.List;

import static com.hernanaguado.backend.workation.WorkationFixtures.GERMANY;
import static com.hernanaguado.backend.workation.WorkationFixtures.GERMANY_CODE;
import static com.hernanaguado.backend.workation.WorkationFixtures.SKIPPED_ROW_LOG;
import static com.hernanaguado.backend.workation.WorkationFixtures.UNKNOWN_COUNTRY;
import static com.hernanaguado.backend.workation.WorkationFixtures.UNKNOWN_RISK;
import static com.hernanaguado.backend.workation.WorkationFixtures.WARN_LEVEL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Runs the importer with the real parser against {@code workations-edge-cases.csv}:
 * tricky but valid rows are imported, invalid rows are skipped with a warning.
 */
@ExtendWith({MockitoExtension.class, OutputCaptureExtension.class})
class WorkationCsvImporterEdgeCasesTest {

    private static final String EDGE_CASES_CSV = "workations-edge-cases.csv";
    private static final String QUOTED_NAME_ID = "e1";
    private static final String SPEC_RISK_NAMING_ID = "e2";
    private static final String UNKNOWN_COUNTRY_ID = "e3";
    private static final String QUOTED_NAME_WITH_COMMA = "Smith, John";
    private static final String MISSING_COLUMN_REASON = "Missing column";
    private static final String EMPTY_EMPLOYEE_REASON = "Empty value for 'employee'";

    @Mock
    private WorkationRepository workationRepository;

    @Captor
    private ArgumentCaptor<List<Workation>> savedWorkations;

    private final WorkationMapper mapper = new WorkationMapper(new CountryCodeResolver());

    private List<Workation> imported;

    @BeforeEach
    void importEdgeCasesCsv() {
        WorkationCsvImporter importer = new WorkationCsvImporter(
                workationRepository, new WorkationCsvParser(), new ClassPathResource(EDGE_CASES_CSV));
        when(workationRepository.count()).thenReturn(0L);

        importer.run(new DefaultApplicationArguments());

        verify(workationRepository).saveAll(savedWorkations.capture());
        imported = savedWorkations.getValue();
    }

    @Test
    void shouldImportOnlyTheValidRows() {
        assertThat(imported).extracting(Workation::getWorkationId).containsExactly(QUOTED_NAME_ID, SPEC_RISK_NAMING_ID, UNKNOWN_COUNTRY_ID);
    }

    @Test
    void shouldKeepQuotedNameContainingAComma() {
        Workation smith = imported.getFirst();

        assertThat(smith.getEmployee()).isEqualTo(QUOTED_NAME_WITH_COMMA);
        assertThat(smith.getStart()).isEqualTo(LocalDate.of(2024, 2, 1));
        assertThat(smith.getEnd()).isEqualTo(LocalDate.of(2024, 2, 15));
        assertThat(smith.getWorkingDays()).isEqualTo(10);
        assertThat(smith.getRisk()).isEqualTo(RiskLevel.LOW);
    }

    @Test
    void shouldAcceptSpecRiskNamingAndLowercaseRisk() {
        assertThat(imported).extracting(Workation::getRisk)
                .containsExactly(RiskLevel.LOW, RiskLevel.NO, RiskLevel.HIGH);
    }

    @Test
    void shouldResolveCountryCodesAndLeaveUnknownCountryNull() {
        // when
        List<WorkationDto> dtos = imported.stream().map(mapper::toDto).toList();

        // then
        assertThat(dtos)
                .extracting(WorkationDto::origin, WorkationDto::originCode,
                        WorkationDto::destination, WorkationDto::destinationCode)
                .containsExactly(
                        tuple("United Kingdom", "gb", "Czechia", "cz"),
                        tuple("South Korea", "kr", "Netherlands", "nl"),
                        tuple(UNKNOWN_COUNTRY, null, GERMANY, GERMANY_CODE));
    }

    @Test
    void shouldLogAWarningWithTheLineNumberOfEachSkippedRow(CapturedOutput output) {
        assertThat(output)
                .contains(WARN_LEVEL)
                .contains(SKIPPED_ROW_LOG + 5)
                .contains(UNKNOWN_RISK)
                .contains(SKIPPED_ROW_LOG + 6)
                .contains(SKIPPED_ROW_LOG + 7)
                .contains(MISSING_COLUMN_REASON)
                .contains(SKIPPED_ROW_LOG + 8)
                .contains(EMPTY_EMPLOYEE_REASON);
    }
}