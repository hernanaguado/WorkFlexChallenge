package com.hernanaguado.backend.workation.service;

import com.hernanaguado.backend.workation.dto.WorkationDto;
import com.hernanaguado.backend.workation.mapper.WorkationMapper;
import com.hernanaguado.backend.workation.model.Workation;
import com.hernanaguado.backend.workation.repository.WorkationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.hernanaguado.backend.workation.WorkationFixtures.highRiskWorkation;
import static com.hernanaguado.backend.workation.WorkationFixtures.highRiskWorkationDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkationServiceTest {

    private static final Sort BY_ID = Sort.by("workationId");

    @Mock
    private WorkationRepository workationRepository;

    @Mock
    private WorkationMapper workationMapper;

    @InjectMocks
    private WorkationService workationService;

    @Test
    void shouldReturnAllWorkationsMappedToDtosOrderedById() {
        // given
        Workation workation = highRiskWorkation();
        WorkationDto dto = highRiskWorkationDto();
        when(workationRepository.findAll(BY_ID)).thenReturn(List.of(workation));
        when(workationMapper.toDto(workation)).thenReturn(dto);

        // when
        List<WorkationDto> result = workationService.findAll();

        // then
        assertThat(result).containsExactly(dto);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoWorkations() {
        // given
        when(workationRepository.findAll(BY_ID)).thenReturn(List.of());

        // when & then
        assertThat(workationService.findAll()).isEmpty();
    }
}
