package com.hernanaguado.backend.country;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Resolves English country names (e.g. "United States") to lowercase
 * ISO 3166-1 alpha-2 codes (e.g. "us"), as used by flag CDNs.
 */
@Component
public class CountryCodeResolver {

    private final Map<String, String> codesByName = Arrays.stream(Locale.getISOCountries())
            .collect(Collectors.toUnmodifiableMap(
                    CountryCodeResolver::englishName,
                    Function.identity(),
                    (first, second) -> first));

    /**
     * @return the lowercase alpha-2 code, or {@code null} if the name is unknown
     */
    public String resolve(String countryName) {
        if (countryName == null) {
            return null;
        }
        String code = codesByName.get(normalize(countryName));
        return code == null ? null : code.toLowerCase(Locale.ROOT);
    }

    private static String englishName(String isoCode) {
        return normalize(Locale.of("", isoCode).getDisplayCountry(Locale.ENGLISH));
    }

    private static String normalize(String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }
}