package com.empmanagement.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.empmanagement.dto.EmployeeRequest;
import com.empmanagement.dto.EmployeeResponse;
import com.empmanagement.service.EmployeeService;

// @RestController
// @RequestMapping("/api/v1/employees")
// @RequiredArgsConstructor
// public class EmployeeController {

//     private final EmployeeService employeeService;

//     // GET /api/v1/employees
//     @GetMapping
//     @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
//     public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
//         return ResponseEntity.ok(employeeService.getAllEmployees());
//     }

//     // GET /api/v1/employees/{id}
//     @GetMapping("/{id}")
//     @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
//     public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
//         return ResponseEntity.ok(employeeService.getEmployeeById(id));
//     }

//     // POST /api/v1/employees
//     @PostMapping
//     @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
//     public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
//         return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(request));
//     }

//     // PUT /api/v1/employees/{id}
//     @PutMapping("/{id}")
//     @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
//     public ResponseEntity<EmployeeResponse> updateEmployee(
//             @PathVariable Long id,
//             @Valid @RequestBody EmployeeRequest request) {
//         return ResponseEntity.ok(employeeService.updateEmployee(id, request));
//     }

//     // DELETE /api/v1/employees/{id}
//     @DeleteMapping("/{id}")
//     @PreAuthorize("hasRole('ADMIN')")
//     public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
//         employeeService.deleteEmployee(id);
//         return ResponseEntity.noContent().build();
//     }
// }



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "Employee management endpoints")
public class EmployeeController {

    private final EmployeeService employeeService;

    // GET /api/v1/employees?page=0&size=10&sort=firstName,asc
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    @Operation(summary = "Get all employees (paginated)")
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(employeeService.getAllEmployees(pageable));
    }

    // GET /api/v1/employees/search?keyword=john&page=0&size=10
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    @Operation(summary = "Search employees by name or email")
    public ResponseEntity<Page<EmployeeResponse>> searchEmployees(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(employeeService.searchEmployees(keyword,
                PageRequest.of(page, size)));
    }

    // GET /api/v1/employees/filter?department=Engineering&role=Backend Developer
    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    @Operation(summary = "Filter employees by department or role")
    public ResponseEntity<Page<EmployeeResponse>> filterEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (department != null)
            return ResponseEntity.ok(employeeService.getByDepartment(department, pageable));
        if (role != null)
            return ResponseEntity.ok(employeeService.getByRole(role, pageable));
        return ResponseEntity.ok(employeeService.getAllEmployees(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR','EMPLOYEE')")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @Operation(summary = "Create a new employee")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.createEmployee(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @Operation(summary = "Update an employee")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an employee")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}