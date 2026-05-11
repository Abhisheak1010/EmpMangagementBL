package com.empmanagement.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Department ID is required")
    private Long departmentId; 

    // @NotBlank(message = "Department is required")
    // private String department;

    @NotBlank(message = "Role is required")
    private String role;

    @Positive(message = "Salary must be positive")
    private Double salary;

    private LocalDate joinedAt;
}
