package com.example.resilient_api.infrastructure.entrypoints.mapper;

import com.example.resilient_api.domain.model.Capacity;
import com.example.resilient_api.infrastructure.entrypoints.dto.CapacityDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        uses = {TechnologyMapper.class})
public interface CapacityMapper {

    @Mapping( source = "id", target = "idCapacity")
    @Mapping( source = "name", target = "name")
    @Mapping( source = "description", target = "description")
    @Mapping( source = "capacityTechnologyList", target = "capacityTechnologyList")
    CapacityDTO capacityTechnologiesToCapacityDTO(Capacity capacity);

    @Mapping( source = "idCapacity", target = "id")
    @Mapping( source = "name", target = "name")
    @Mapping( source = "description", target = "description")
    @Mapping( source = "capacityTechnologyList", target = "capacityTechnologyList")
    Capacity capacityTechnologiesDTOToCapacity(CapacityDTO capacityDTO);
}
