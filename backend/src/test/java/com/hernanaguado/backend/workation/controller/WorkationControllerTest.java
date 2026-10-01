package com.hernanaguado.backend.workation.controller;

import com.hernanaguado.backend.workation.model.RiskLevel;
import com.hernanaguado.backend.workation.service.WorkationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.hernanaguado.backend.workation.WorkationFixtures.GERMANY;
import static com.hernanaguado.backend.workation.WorkationFixtures.GERMANY_CODE;
import static com.hernanaguado.backend.workation.WorkationFixtures.GREECE;
import static com.hernanaguado.backend.workation.WorkationFixtures.GREECE_CODE;
import static com.hernanaguado.backend.workation.WorkationFixtures.LOW_RISK_EMPLOYEE;
import static com.hernanaguado.backend.workation.WorkationFixtures.LOW_RISK_END;
import static com.hernanaguado.backend.workation.WorkationFixtures.LOW_RISK_ID;
import static com.hernanaguado.backend.workation.WorkationFixtures.LOW_RISK_START;
import static com.hernanaguado.backend.workation.WorkationFixtures.LOW_RISK_WORKING_DAYS;
import static com.hernanaguado.backend.workation.WorkationFixtures.WORKATION_ENDPOINT;
import static com.hernanaguado.backend.workation.WorkationFixtures.lowRiskWorkationDto;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkationController.class)
class WorkationControllerTest {

    private static final String FRONTEND_ORIGIN = "http://localhost:4200";
    private static final String PROBLEM_TITLE = "Internal Server Error";
    private static final String PROBLEM_DETAIL = "An unexpected error occurred";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkationService workationService;

    @Test
    void shouldReturnWorkationsAsJson() throws Exception {
        // given
        when(workationService.findAll()).thenReturn(List.of(lowRiskWorkationDto()));

        // when & then
        mockMvc.perform(get(WORKATION_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].workationId").value(LOW_RISK_ID))
                .andExpect(jsonPath("$[0].employee").value(LOW_RISK_EMPLOYEE))
                .andExpect(jsonPath("$[0].origin").value(GERMANY))
                .andExpect(jsonPath("$[0].originCode").value(GERMANY_CODE))
                .andExpect(jsonPath("$[0].destination").value(GREECE))
                .andExpect(jsonPath("$[0].destinationCode").value(GREECE_CODE))
                .andExpect(jsonPath("$[0].start").value(LOW_RISK_START.toString()))
                .andExpect(jsonPath("$[0].end").value(LOW_RISK_END.toString()))
                .andExpect(jsonPath("$[0].workingDays").value(LOW_RISK_WORKING_DAYS))
                .andExpect(jsonPath("$[0].risk").value(RiskLevel.LOW.name()));
    }

    @Test
    void shouldReturnEmptyArrayWhenThereAreNoWorkations() throws Exception {
        // given
        when(workationService.findAll()).thenReturn(List.of());

        // when & then
        mockMvc.perform(get(WORKATION_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturnProblemDetailWhenAnUnexpectedErrorOccurs() throws Exception {
        // given
        when(workationService.findAll()).thenThrow(new IllegalStateException("database is down"));

        // when & then
        mockMvc.perform(get(WORKATION_ENDPOINT))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.title").value(PROBLEM_TITLE))
                .andExpect(jsonPath("$.detail").value(PROBLEM_DETAIL));
    }

    @Test
    void shouldAllowCorsRequestsFromTheFrontendOrigin() throws Exception {
        // given
        when(workationService.findAll()).thenReturn(List.of());

        // when & then
        mockMvc.perform(get(WORKATION_ENDPOINT).header(HttpHeaders.ORIGIN, FRONTEND_ORIGIN))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, FRONTEND_ORIGIN));
    }
}
