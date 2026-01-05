package com.example.resilient_api.domain.api;

import com.example.resilient_api.domain.model.Person;
import com.example.resilient_api.domain.model.Report;
import com.example.resilient_api.domain.model.UpdatePersonReportRequest;
import reactor.core.publisher.Mono;

public interface ReportServicePort {
    Mono<Void> registerReport(Report report, String messageId);
    Mono<Void> updateReport(UpdatePersonReportRequest updatePersonReportRequest, String messageId);
}
