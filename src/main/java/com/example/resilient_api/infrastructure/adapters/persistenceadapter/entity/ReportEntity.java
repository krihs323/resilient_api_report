package com.example.resilient_api.infrastructure.adapters.persistenceadapter.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

//@Table(name = "person")
@Getter
@Setter
@RequiredArgsConstructor
public class ReportEntity {
    @Id
    private Long id;
    private String name;
    private String description;
}
