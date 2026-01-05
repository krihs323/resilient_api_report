package com.example.resilient_api.domain.model;


import java.time.LocalDate;
import java.util.List;

public record Report(Long id, String name, String description, LocalDate launchDate, Integer durationWeeks, List<Capacity> bootcampCapacityList) {
}