package com.example.resilient_api.domain.model;


import java.time.LocalDate;
import java.util.List;

public record BootcampReport(Long bootcampId, String name, String description, Integer durationWeeks, LocalDate launchDate, List<Capacity> capacity, List<Person> personas, Integer totalCapacities, Integer totalTechnologies, Integer totalPeople) {
}