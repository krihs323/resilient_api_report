package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.Report;
import com.example.resilient_api.infrastructure.entrypoints.dto.ReportDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {CapacityMapper.class})
public interface ReportMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "launchDate", target = "launchDate")
    @Mapping(source = "durationWeeks", target = "durationWeeks")
    @Mapping(source = "capacityList", target = "capacityList")
    Report reportDTOToReport(ReportDTO reportDTO);

}

