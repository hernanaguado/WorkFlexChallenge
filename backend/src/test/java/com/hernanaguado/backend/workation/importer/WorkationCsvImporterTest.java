package com.hernanaguado.backend.workation.importer;

import com.hernanaguado.backend.workation.model.Workation;
import com.hernanaguado.backend.workation.repository.WorkationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.core.io.ByteArrayResource;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.hernanaguado.backend.workation.WorkationFixtures.highRiskWorkation;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkationCsvImporterTest {

    private static final DefaultApplicationArguments NO_ARGS = new DefaultApplicationArguments();
    private static final long EXISTING_WORKATIONS = 5L;

    @Mock
    private WorkationRepository workationRepository;

    @Mock
    private WorkationCsvParser workationCsvParser;

    private WorkationCsvImporter importer;

    @BeforeEach
    void setUp() {
        ByteArrayResource csv = new ByteArrayResource("csv content".getBytes(StandardCharsets.UTF_8));
        importer = new WorkationCsvImporter(workationRepository, workationCsvParser, csv);
    }

    @Test
    void shouldSaveParsedWorkationsWhenTableIsEmpty() {
        // given
        List<Workation> parsed = List.of(highRiskWorkation());
        when(workationRepository.count()).thenReturn(0L);
        when(workationCsvParser.parse(any(Reader.class))).thenReturn(parsed);

        // when
        importer.run(NO_ARGS);

        // then
        verify(workationRepository).saveAll(parsed);
    }

    @Test
    void shouldSkipImportWhenWorkationsAlreadyExist() {
        // given
        when(workationRepository.count()).thenReturn(EXISTING_WORKATIONS);

        // when
        importer.run(NO_ARGS);

        // then
        verify(workationCsvParser, never()).parse(any(Reader.class));
        verify(workationRepository, never()).saveAll(anyList());
    }
}