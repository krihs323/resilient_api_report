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
public class ReportDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate launchDate;
    private Integer durationWeeks;
    private List<CapacityDTO> capacityList;

}
