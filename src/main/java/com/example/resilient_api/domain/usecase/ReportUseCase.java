package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.*;
import com.example.resilient_api.domain.spi.ReportPersistencePort;
import com.example.resilient_api.domain.api.ReportServicePort;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.*;

@Slf4j
public class ReportUseCase implements ReportServicePort {

    private final ReportPersistencePort reportPersistencePort;

    public ReportUseCase(ReportPersistencePort reportPersistencePort) {
        this.reportPersistencePort = reportPersistencePort;
    }

    @Override
    public Mono<Void> registerReport(Report report, String messageId) {
        return reportPersistencePort.save(report)
                .doOnError(e -> log.error("Error en registro para messageId {}: {}", messageId, e.getMessage()));
    }

    @Override
    public Mono<Void> updateReport(UpdatePersonReportRequest updatePersonReportRequest, String messageId) {

      return reportPersistencePort.update(updatePersonReportRequest.idBootcamp(), updatePersonReportRequest.person(), messageId)
                .doOnError(e -> log.error("Error en registro para messageId {}: {}", messageId, e.getMessage()));
    }

    @Override
    public Flux<BootcampReport> listTopBootcamps(String messageId) {
        return reportPersistencePort.listTopBootcamps(messageId)
                .switchIfEmpty(Flux.empty());
    }

}
