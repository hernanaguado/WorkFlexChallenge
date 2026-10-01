package com.hernanaguado.backend.workation.mapper;

import com.hernanaguado.backend.country.CountryCodeResolver;
import com.hernanaguado.backend.workation.dto.WorkationDto;
import com.hernanaguado.backend.workation.model.Workation;
import org.junit.jupiter.api.Test;

import static com.hernanaguado.backend.workation.WorkationFixtures.INDIA_CODE;
import static com.hernanaguado.backend.workation.WorkationFixtures.UNKNOWN_COUNTRY;
import static com.hernanaguado.backend.workation.WorkationFixtures.lowRiskWorkation;
import static com.hernanaguado.backend.workation.WorkationFixtures.lowRiskWorkationDto;
import static com.hernanaguado.backend.workation.WorkationFixtures.noRiskWorkation;
import static org.assertj.core.api.Assertions.assertThat;

class WorkationMapperTest {

    private final WorkationMapper mapper = new WorkationMapper(new CountryCodeResolver());

    @Test
    void shouldMapEntityToDtoWithCountryCodes() {
        // given
        Workation workation = lowRiskWorkation();

        // when
        WorkationDto dto = mapper.toDto(workation);

        // then
        assertThat(dto).isEqualTo(lowRiskWorkationDto());
    }

    @Test
    void shouldLeaveCountryCodeNullWhenCountryIsUnknown() {
        // given
        Workation workation = noRiskWorkation();
        workation.setOrigin(UNKNOWN_COUNTRY);

        // when
        WorkationDto dto = mapper.toDto(workation);

        // then
        assertThat(dto.originCode()).isNull();
        assertThat(dto.destinationCode()).isEqualTo(INDIA_CODE);
    }
}
