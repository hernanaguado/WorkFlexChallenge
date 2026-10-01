package com.hernanaguado.backend.workation.service;

import com.hernanaguado.backend.workation.dto.WorkationDto;
import com.hernanaguado.backend.workation.mapper.WorkationMapper;
import com.hernanaguado.backend.workation.repository.WorkationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WorkationService {

    private static final Sort DEFAULT_SORT = Sort.by("workationId");

    private final WorkationRepository workationRepository;
    private final WorkationMapper workationMapper;

    public WorkationService(WorkationRepository workationRepository, WorkationMapper workationMapper) {
        this.workationRepository = workationRepository;
        this.workationMapper = workationMapper;
    }

    @Transactional(readOnly = true)
    public List<WorkationDto> findAll() {
        return workationRepository.findAll(DEFAULT_SORT).stream()
                .map(workationMapper::toDto)
                .toList();
    }
}