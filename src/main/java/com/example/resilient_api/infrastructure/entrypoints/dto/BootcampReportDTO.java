package com.example.resilient_api.infrastructure.entrypoints.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class BootcampReportDTO {
    private Long bootcampId;
    private String name;
    private String description;
    private Integer durationWeeks;
    private LocalDate launchDate;

    private List<CapacityDTO> bootcampCapacityList;
    private List<PersonDTO> personas;

    private Integer totalCapacities;
    private Integer totalTechnologies;
    private Integer totalPeople;

}
