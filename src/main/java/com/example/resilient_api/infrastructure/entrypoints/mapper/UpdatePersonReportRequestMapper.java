package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.Person;
import com.example.resilient_api.domain.model.UpdatePersonReportRequest;
import com.example.resilient_api.infrastructure.entrypoints.dto.UpdatePersonReportRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {PersonMapper.class})
public interface UpdatePersonReportRequestMapper {

    @Mapping(source = "idBootcamp", target = "idBootcamp")
    @Mapping(source = "person", target = "person")
    UpdatePersonReportRequest personDTOToPerson(UpdatePersonReportRequestDTO updatePersonReportRequestDTO);
}
