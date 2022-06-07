package com.snow.models;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Table("employee")
public class Employee {
    @Id
    private long id;
    private String name;
    private long age;
    private long salary;
}
