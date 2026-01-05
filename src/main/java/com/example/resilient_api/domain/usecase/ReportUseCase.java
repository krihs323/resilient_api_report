package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.model.*;
import com.example.resilient_api.domain.spi.BootcampGateway;
import com.example.resilient_api.domain.spi.ReportPersistencePort;
import com.example.resilient_api.domain.api.ReportServicePort;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import java.util.*;

@Slf4j
public class ReportUseCase implements ReportServicePort {

    private final ReportPersistencePort reportPersistencePort;
    private final BootcampGateway bootcampGateway;

    public ReportUseCase(ReportPersistencePort reportPersistencePort, BootcampGateway bootcampGateway) {
        this.reportPersistencePort = reportPersistencePort;
        this.bootcampGateway = bootcampGateway;
    }

    @Override
    public Mono<Void> registerReport(Report report, String messageId) {
        return reportPersistencePort.save(report)
                .doOnError(e -> log.error("Error en registro para messageId {}: {}", messageId, e.getMessage()));
    }

}
