package com.example.resilient_api.infrastructure.adapters.persistenceadapter;

import com.example.resilient_api.domain.model.BootcampReport;
import com.example.resilient_api.domain.model.Person;
import com.example.resilient_api.domain.model.Report;
import com.example.resilient_api.domain.spi.ReportPersistencePort;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.mapper.ReportEntityMapper;
import com.example.resilient_api.infrastructure.adapters.persistenceadapter.repository.ReportRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import reactor.core.publisher.Flux;
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
        int totalCapacities = (report.bootcampCapacityList() != null) ? report.bootcampCapacityList().size() : 0;
        long totalTechnologies = (report.bootcampCapacityList() != null)
                ? report.bootcampCapacityList().stream()
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
                .set("capacity", report.bootcampCapacityList())
                .set("totalCapacities", totalCapacities)
                .set("totalTechnologies", (int) totalTechnologies)
                .setOnInsert("createdAt", LocalDateTime.now())
                .set("updatedAt", LocalDateTime.now());

        // upsert: si no existe lo crea, si existe lo actualiza
        return mongoTemplate.upsert(query, update, "bootcamp_metrics").then();

    }

    @Override
    public Mono<Void> update(Long idBootcamp, Person person, String messageId) {
        // 1. Criterio de búsqueda por el ID del bootcamp
        Query query = new Query(Criteria.where("bootcampId").is(idBootcamp));

        // 2. Operación: Agregar persona a la lista y actualizar fecha
        Update update = new Update()
                .push("personas", person)
                .inc("totalPeople", 1)
                .set("updatedAt", LocalDateTime.now());

        return mongoTemplate.updateFirst(query, update, "bootcamp_metrics").then();
    }

    @Override
    public Flux<BootcampReport> listTopBootcamps(String messageId) {
        Query query = new Query()
                .with(Sort.by(Sort.Direction.DESC, "totalPeople"));
        // Retornamos un Flux para manejar múltiples registros de forma reactiva
        return mongoTemplate.find(query, BootcampReport.class, "bootcamp_metrics")
                .doOnComplete(() -> log.info("Listado de bootcamps más exitoso generado con éxito"));
    }

}
