package com.example.resilient_api.domain.api;

import com.example.resilient_api.domain.model.Report;
import reactor.core.publisher.Mono;

public interface ReportServicePort {
    Mono<Void> registerReport(Report report, String messageId);
}
