package com.hernanaguado.backend.workation.mapper;

import com.hernanaguado.backend.country.CountryCodeResolver;
import com.hernanaguado.backend.workation.dto.WorkationDto;
import com.hernanaguado.backend.workation.model.Workation;
import org.springframework.stereotype.Component;

@Component
public class WorkationMapper {

    private final CountryCodeResolver countryCodeResolver;

    public WorkationMapper(CountryCodeResolver countryCodeResolver) {
        this.countryCodeResolver = countryCodeResolver;
    }

    public WorkationDto toDto(Workation workation) {
        return new WorkationDto(
                workation.getWorkationId(),
                workation.getEmployee(),
                workation.getOrigin(),
                countryCodeResolver.resolve(workation.getOrigin()),
                workation.getDestination(),
                countryCodeResolver.resolve(workation.getDestination()),
                workation.getStart(),
                workation.getEnd(),
                workation.getWorkingDays(),
                workation.getRisk());
    }
}