package com.hernanaguado.backend.workation;

import com.hernanaguado.backend.workation.importer.WorkationCsvImporter;
import com.hernanaguado.backend.workation.model.RiskLevel;
import com.hernanaguado.backend.workation.repository.WorkationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static com.hernanaguado.backend.workation.WorkationFixtures.GERMANY_CODE;
import static com.hernanaguado.backend.workation.WorkationFixtures.HIGH_RISK_EMPLOYEE;
import static com.hernanaguado.backend.workation.WorkationFixtures.HIGH_RISK_ID;
import static com.hernanaguado.backend.workation.WorkationFixtures.HIGH_RISK_START;
import static com.hernanaguado.backend.workation.WorkationFixtures.UNITED_STATES_CODE;
import static com.hernanaguado.backend.workation.WorkationFixtures.WORKATION_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Boots the full application: the CSV is imported into H2 on startup and served by the endpoint.
 */
@SpringBootTest
@AutoConfigureMockMvc
class WorkationIntegrationTest {

    private static final int CSV_ROW_COUNT = 5;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WorkationRepository workationRepository;

    @Autowired
    private WorkationCsvImporter workationCsvImporter;

    @Test
    void shouldReturnTheFiveWorkationsImportedFromCsv() throws Exception {
        mockMvc.perform(get(WORKATION_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(CSV_ROW_COUNT))
                .andExpect(jsonPath("$[0].workationId").value(HIGH_RISK_ID))
                .andExpect(jsonPath("$[0].employee").value(HIGH_RISK_EMPLOYEE))
                .andExpect(jsonPath("$[0].originCode").value(GERMANY_CODE))
                .andExpect(jsonPath("$[0].destinationCode").value(UNITED_STATES_CODE))
                .andExpect(jsonPath("$[0].start").value(HIGH_RISK_START.toString()))
                .andExpect(jsonPath("$[0].risk").value(RiskLevel.HIGH.name()))
                .andExpect(jsonPath("$[3].risk").value(RiskLevel.LOW.name()))
                .andExpect(jsonPath("$[4].risk").value(RiskLevel.NO.name()));
    }

    @Test
    void shouldNotDuplicateWorkationsWhenImporterRunsAgain() {
        // when
        workationCsvImporter.run(new DefaultApplicationArguments());

        // then
        assertThat(workationRepository.count()).isEqualTo(CSV_ROW_COUNT);
    }
}
