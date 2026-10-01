package com.hernanaguado.backend.workation.controller;

import com.hernanaguado.backend.workation.dto.WorkationDto;
import com.hernanaguado.backend.workation.service.WorkationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workflex/workation")
@Tag(name = "Workations")
public class WorkationController {

    private final WorkationService workationService;

    public WorkationController(WorkationService workationService) {
        this.workationService = workationService;
    }

    @GetMapping
    @Operation(summary = "List all workations")
    public List<WorkationDto> findAll() {
        return workationService.findAll();
    }
}