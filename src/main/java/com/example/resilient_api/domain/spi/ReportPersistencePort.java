package com.example.resilient_api.domain.spi;

import com.example.resilient_api.domain.model.Person;
import com.example.resilient_api.domain.model.Report;
import reactor.core.publisher.Mono;

public interface ReportPersistencePort {
    Mono<Void> save(Report report);
    Mono<Void> update(Long idBootcamp, Person person, String messageId);
}
