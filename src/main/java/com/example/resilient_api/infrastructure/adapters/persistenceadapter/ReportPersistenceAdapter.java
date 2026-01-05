package com.example.resilient_api.infrastructure.adapters.persistenceadapter;

import com.example.resilient_api.domain.model.Report;
import com.example.resilient_api.domain.spi.ReportPersistencePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.ReportEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.ReportRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@AllArgsConstructor
@Slf4j
public class ReportPersistenceAdapter implements ReportPersistencePort {
    private final ReportRepository reportRepository;
    private final ReportEntityMapper reportEntityMapper;
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Void> save(Report report) {

        // Calculamos totales para métricas rápidas
        int totalCapacities = (report.capacityList() != null) ? report.capacityList().size() : 0;
        long totalTechnologies = (report.capacityList() != null)
                ? report.capacityList().stream()
                .flatMap(c -> c.capacityTechnologyList().stream())
                .distinct() // Por si una tecnología se repite en varias capacidades
                .count()
                : 0;

        Query query = new Query(Criteria.where("bootcampId").is(report.id()));
        Update update = new Update()
                .set("name", report.name())
                .set("description", report.description())
                .set("launchDate", report.launchDate())
                .set("durationWeeks", report.durationWeeks())
                .set("capacity", report.capacityList())
                .set("totalCapacities", totalCapacities)
                .set("totalTechnologies", (int) totalTechnologies)
                //.inc("enrolledPersonsCount", report.increment()) // Incrementa en 1
                .setOnInsert("createdAt", LocalDateTime.now())
                .set("updatedAt", LocalDateTime.now());

        // upsert: si no existe lo crea, si existe lo actualiza
        return mongoTemplate.upsert(query, update, "bootcamp_metrics").then();

    }

}
