package com.hernanaguado.backend.workation.importer;

import com.hernanaguado.backend.workation.exception.CsvImportException;
import com.hernanaguado.backend.workation.model.RiskLevel;
import com.hernanaguado.backend.workation.model.Workation;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses workations from CSV with the header
 * {@code workationId,employee,origin,destination,start,end,workingDays,risk}.
 * Invalid rows are skipped and logged; valid rows are always returned.
 */
@Component
public class WorkationCsvParser {

    private static final Logger log = LoggerFactory.getLogger(WorkationCsvParser.class);

    private static final CSVFormat FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .setIgnoreEmptyLines(true)
            .get();

    public List<Workation> parse(Reader reader) {
        try (CSVParser parser = FORMAT.parse(reader)) {
            List<Workation> workations = new ArrayList<>();
            for (CSVRecord csvRecord : parser) {
                try {
                    workations.add(toWorkation(csvRecord));
                } catch (CsvImportException ex) {
                    log.warn("Skipping invalid workation at CSV line {}: {}",
                            parser.getCurrentLineNumber(), ex.getMessage());
                }
            }
            return workations;
        } catch (IOException ex) {
            throw new UncheckedIOException("Could not read workations CSV", ex);
        }
    }

    private Workation toWorkation(CSVRecord csvRecord) {
        try {
            return new Workation(
                    required(csvRecord, "workationId"),
                    required(csvRecord, "employee"),
                    required(csvRecord, "origin"),
                    required(csvRecord, "destination"),
                    LocalDate.parse(required(csvRecord, "start")),
                    LocalDate.parse(required(csvRecord, "end")),
                    Integer.parseInt(required(csvRecord, "workingDays")),
                    RiskLevel.fromCsv(required(csvRecord, "risk")));
        } catch (RuntimeException ex) {
            throw new CsvImportException(ex.getMessage(), ex);
        }
    }

    private static String required(CSVRecord csvRecord, String column) {
        if (!csvRecord.isSet(column)) {
            throw new IllegalArgumentException("Missing column '" + column + "'");
        }
        String value = csvRecord.get(column);
        if (value.isBlank()) {
            throw new IllegalArgumentException("Empty value for '" + column + "'");
        }
        return value;
    }
}