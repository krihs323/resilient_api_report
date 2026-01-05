package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.BootcampReport;
import com.example.resilient_api.infrastructure.entrypoints.dto.BootcampReportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {CapacityMapper.class, PersonMapper.class})
public interface BootcampReportMapper {

    @Mapping( source = "bootcampId", target = "bootcampId")
    @Mapping( source = "name", target = "name")
    @Mapping( source = "description", target = "description")
    @Mapping( source = "durationWeeks", target = "durationWeeks")
    @Mapping( source = "launchDate", target = "launchDate")
    @Mapping( source = "totalCapacities", target = "totalCapacities")
    @Mapping( source = "totalTechnologies", target = "totalTechnologies")
    @Mapping( source = "totalPeople", target = "totalPeople")
    @Mapping( source = "capacity", target = "bootcampCapacityList")
    @Mapping( source = "personas", target = "personas")
    BootcampReportDTO bootcampReportToBootcampReportDTO(BootcampReport bootcampReport);

}
