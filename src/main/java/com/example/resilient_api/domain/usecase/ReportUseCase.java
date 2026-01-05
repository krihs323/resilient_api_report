package com.example.resilient_api.domain.usecase;

import com.example.resilient_api.domain.enums.TechnicalMessage;
import com.example.resilient_api.domain.exceptions.BusinessException;
import com.example.resilient_api.domain.model.*;
import com.example.resilient_api.domain.spi.BootcampGateway;
import com.example.resilient_api.domain.spi.ReportPersistencePort;
import com.example.resilient_api.domain.api.ReportServicePort;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
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
//                .flatMap(savedPerson ->
//                        saveBootcamps(savedPerson.id(), report, messageId)
//                                .then(Mono.just(savedPerson))
//                )
                // Log de error global para identificar exactamente qué parte falla
                .doOnError(e -> log.error("Error en registro para messageId {}: {}", messageId, e.getMessage()));
    }

    /*private Mono<Boolean> saveBootcamps(Long idPerson, Report report, String messageId) {
        return reportPersistencePort.saveBootcamps(idPerson, report, messageId)
                .filter(isSaved -> isSaved)
                .switchIfEmpty(Mono.error(new BusinessException(TechnicalMessage.DATABASE_ERROR)));
    }*/





}
