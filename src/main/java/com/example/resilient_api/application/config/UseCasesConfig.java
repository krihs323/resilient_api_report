package com.example.resilient_api.application.config;

import com.example.resilient_api.domain.spi.BootcampGateway;
import com.example.resilient_api.domain.spi.ReportPersistencePort;
import com.example.resilient_api.domain.usecase.ReportUseCase;
import com.example.resilient_api.domain.api.ReportServicePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.ReportPersistenceAdapter;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.ReportEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
        private final ReportRepository reportRepository;
        private final ReportEntityMapper reportEntityMapper;
        private final ReactiveMongoTemplate mongoTemplate;

        @Bean
        public ReportPersistencePort personPersistencePort() {
                return new ReportPersistenceAdapter(reportRepository, reportEntityMapper, mongoTemplate);
        }

        @Bean
        public ReportServicePort personServicePort(ReportPersistencePort reportPersistencePort, BootcampGateway bootcampGateway){
                return new ReportUseCase(reportPersistencePort, bootcampGateway);
        }
}
