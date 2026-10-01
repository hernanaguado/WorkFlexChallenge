package com.hernanaguado.backend.country;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class CountryCodeResolverTest {

    private final CountryCodeResolver resolver = new CountryCodeResolver();

    @ParameterizedTest
    @CsvSource({
            "Germany, de",
            "United States, us",
            "Ukraine, ua",
            "Belgium, be",
            "Spain, es",
            "Greece, gr",
            "India, in",
            "'  germany ', de"
    })
    void shouldResolveEnglishCountryNameToLowercaseIsoCode(String name, String expectedCode) {
        assertThat(resolver.resolve(name)).isEqualTo(expectedCode);
    }

    @Test
    void shouldReturnNullWhenCountryIsUnknown() {
        assertThat(resolver.resolve("Atlantis")).isNull();
    }

    @Test
    void shouldReturnNullWhenCountryIsNull() {
        assertThat(resolver.resolve(null)).isNull();
    }
}