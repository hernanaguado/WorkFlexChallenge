package com.hernanaguado.backend.workation.importer;

import com.hernanaguado.backend.workation.model.Workation;
import com.hernanaguado.backend.workation.repository.WorkationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Imports the workations CSV into the database on startup, only if the table is empty.
 */
@Component
public class WorkationCsvImporter implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(WorkationCsvImporter.class);

    private final WorkationRepository workationRepository;
    private final WorkationCsvParser workationCsvParser;
    private final Resource csvResource;

    public WorkationCsvImporter(WorkationRepository workationRepository,
                                WorkationCsvParser workationCsvParser,
                                @Value("${workflex.import.csv-location:classpath:workations.csv}") Resource csvResource) {
        this.workationRepository = workationRepository;
        this.workationCsvParser = workationCsvParser;
        this.csvResource = csvResource;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (workationRepository.count() > 0) {
            log.info("Workations already present, skipping CSV import");
            return;
        }
        List<Workation> workations = readCsv();
        workationRepository.saveAll(workations);
        log.info("Imported {} workations from {}", workations.size(), csvResource.getDescription());
    }

    private List<Workation> readCsv() {
        try (Reader reader = new InputStreamReader(csvResource.getInputStream(), StandardCharsets.UTF_8)) {
            return workationCsvParser.parse(reader);
        } catch (IOException ex) {
            throw new UncheckedIOException("Could not open " + csvResource.getDescription(), ex);
        }
    }
}