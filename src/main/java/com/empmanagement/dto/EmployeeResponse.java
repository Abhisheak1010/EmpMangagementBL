package com.empmanagement.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    // private String department;
    private String departmentName;
    private String role;
    private Double salary;
    private LocalDate joinedAt;
}
