package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.*;
import com.example.resilient_api.domain.spi.ReportPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportUseCaseTest {

    @Mock
    private ReportPersistencePort reportPersistencePort;

    @InjectMocks
    private ReportUseCase reportUseCase;

    private final String messageId = "report-trace-999";
    private Report report;

    @BeforeEach
    void setUp() {

        // 1. Definimos las Tecnologías
        Technology tech1 = new Technology(1L, "Java", "Backend Language");
        Technology tech2 = new Technology(2L, "WebFlux", "Reactive Framework");
        Technology tech3 = new Technology(3L, "PostgreSQL", "Relational Database");

        // 2. Definimos las Capacidades y les asignamos sus tecnologías
        Capacity capBackend = new Capacity(
                10L,
                "Microservices Architect",
                "Design and implement scalable services",
                List.of(tech1, tech2)
        );

        Capacity capDatabase = new Capacity(
                11L,
                "Database Management",
                "Manage and optimize data persistence",
                List.of(tech3)
        );

        // 3. Definimos el Reporte Final
         report = new Report(
                100L,
                "Fullstack Cloud Bootcamp",
                "Comprehensive 12-week intensive program",
                LocalDate.of(2026, 3, 15),
                12,
                List.of(capBackend, capDatabase)
        );
    }

    @Test
    @DisplayName("Should save a new report successfully")
    void registerReportSuccess() {
        when(reportPersistencePort.save(any(Report.class))).thenReturn(Mono.empty());

        Mono<Void> result = reportUseCase.registerReport(report, messageId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(reportPersistencePort).save(report);
    }

    @Test
    @DisplayName("Should update report with person details")
    void updateReportSuccess() {
        Person person = new Person(1L, "Test User", "test@mail.com", 36);
        UpdatePersonReportRequest request = new UpdatePersonReportRequest(100L, person);

        when(reportPersistencePort.update(anyLong(), any(Person.class), anyString()))
                .thenReturn(Mono.empty());

        Mono<Void> result = reportUseCase.updateReport(request, messageId);

        StepVerifier.create(result)
                .verifyComplete();

        verify(reportPersistencePort).update(eq(100L), eq(person), eq(messageId));
    }

    @Test
    @DisplayName("Should list top bootcamps ordered by popularity")
    void listTopBootcampsSuccess() {

        // 1. Preparamos las Tecnologías y Capacidades
        Technology java = new Technology(1L, "Java", "Language");
        Technology spring = new Technology(2L, "Spring Boot", "Framework");

        Capacity capBackend = new Capacity(
                10L,
                "Backend Essentials",
                "Microservices and APIs",
                List.of(java, spring)
        );

        // 2. Preparamos las Personas
        Person p1 = new Person(1L, "Ada Lovelace", "ada@compute.com", 36);
        Person p2 = new Person(2L, "Alan Turing", "alan@enigma.com", 40);

        // 3. Construimos el BootcampReport
        BootcampReport reportMock = new BootcampReport(
                100L,                // bootcampId
                "Java Reactive Bootcamp",       // name
                "Advanced training",            // description
                12,                             // durationWeeks
                LocalDate.of(2026, 3, 1),       // launchDate
                List.of(capBackend),            // capacity (lista)
                List.of(p1, p2),                // personas (lista)
                1,                              // totalCapacities (conteo)
                2,                              // totalTechnologies (conteo: java + spring)
                2                               // totalPeople (conteo: ada + alan)
        );

        // 3. Construimos el BootcampReport
        BootcampReport reportMock2 = new BootcampReport(
                101L,                           // bootcampId
                "Java Reactive Bootcamp",       // name
                "Advanced training",            // description
                12,                             // durationWeeks
                LocalDate.of(2026, 3, 1),       // launchDate
                List.of(capBackend),            // capacity (lista)
                List.of(p1, p2),                // personas (lista)
                1,                              // totalCapacities (conteo)
                2,                              // totalTechnologies (conteo: java + spring)
                2                               // totalPeople (conteo: ada + alan)
        );

        when(reportPersistencePort.listTopBootcamps(anyString()))
                .thenReturn(Flux.just(reportMock, reportMock2));

        Flux<BootcampReport> result = reportUseCase.listTopBootcamps(messageId);

        StepVerifier.create(result)
                .expectNext(reportMock)
                .expectNext(reportMock2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle error and log when saving report fails")
    void registerReportError() {
        when(reportPersistencePort.save(any())).thenReturn(Mono.error(new RuntimeException("DB Error")));

        Mono<Void> result = reportUseCase.registerReport(report, messageId);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    @DisplayName("Should log and propagate error when persistence fails")
    void updateReportFailure() {
        // 1. GIVEN (Preparación)
        String messageId = "error-trace-555";
        Person person = new Person(1L, "Error User", "error@mail.com", 36);
        UpdatePersonReportRequest request = new UpdatePersonReportRequest(100L, person);

        // Simulamos que el puerto de persistencia lanza una excepción
        RuntimeException persistenceException = new RuntimeException("Database connection timeout");

        when(reportPersistencePort.update(eq(100L), any(Person.class), eq(messageId)))
                .thenReturn(Mono.error(persistenceException));

        // 2. WHEN (Ejecución)
        Mono<Void> result = reportUseCase.updateReport(request, messageId);

        // 3. THEN (Verificación)
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Database connection timeout"))
                .verify();

        // Verificamos que se intentó llamar al puerto
        verify(reportPersistencePort).update(eq(100L), eq(person), eq(messageId));
    }
}