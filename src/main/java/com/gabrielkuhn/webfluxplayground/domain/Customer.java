package com.gabrielkuhn.webfluxplayground.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotEmpty;

@Table
@Data
@AllArgsConstructor
@With
@Builder
public class Customer {

    @Id
    private Integer id;
    @NotEmpty
    private String name;
}
