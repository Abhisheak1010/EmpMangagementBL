package com.empmanagement.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.empmanagement.dto.EmployeeRequest;
import com.empmanagement.dto.EmployeeResponse;
import com.empmanagement.entity.Department;
import com.empmanagement.entity.Employee;
import com.empmanagement.exception.EmailAlreadyExistsException;
import com.empmanagement.exception.ResourceNotFoundException;
import com.empmanagement.repo.EmployeeRepository;




import com.empmanagement.repo.DepartmentRepository;

// import java.util.List;
// import java.util.stream.Collectors;

// import org.springframework.stereotype.Service;

// import com.empmanagement.dto.EmployeeRequest;
// import com.empmanagement.dto.EmployeeResponse;
// import com.empmanagement.entity.Employee;
// import com.empmanagement.exception.EmailAlreadyExistsException;
// import com.empmanagement.exception.ResourceNotFoundException;
// import com.empmanagement.repo.EmployeeRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class EmployeeService {

//     private final EmployeeRepository employeeRepository;

//     public List<EmployeeResponse> getAllEmployees() {
//         return employeeRepository.findAll()
//                 .stream()
//                 .map(this::toResponse)
//                 .collect(Collectors.toList());
//     }

//     public EmployeeResponse getEmployeeById(Long id) {
//         Employee employee = employeeRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
//         return toResponse(employee);
//     }

//     public EmployeeResponse createEmployee(EmployeeRequest request) {
//         if (employeeRepository.existsByEmail(request.getEmail())) {
//             throw new EmailAlreadyExistsException("Email already in use: " + request.getEmail());
//         }
//         Employee employee = toEntity(request);
//         return toResponse(employeeRepository.save(employee));
//     }

//     public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
//         Employee existing = employeeRepository.findById(id)
//                 .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));

//         // Allow email update only if it belongs to the same employee
//         if (!existing.getEmail().equals(request.getEmail())
//                 && employeeRepository.existsByEmail(request.getEmail())) {
//             throw new EmailAlreadyExistsException("Email already in use: " + request.getEmail());
//         }

//         existing.setFirstName(request.getFirstName());
//         existing.setLastName(request.getLastName());
//         existing.setEmail(request.getEmail());
//         existing.setDepartment(request.getDepartment());
//         existing.setRole(request.getRole());
//         existing.setSalary(request.getSalary());
//         existing.setJoinedAt(request.getJoinedAt());

//         return toResponse(employeeRepository.save(existing));
//     }

//     public void deleteEmployee(Long id) {
//         if (!employeeRepository.existsById(id)) {
//             throw new ResourceNotFoundException("Employee not found with id: " + id);
//         }
//         employeeRepository.deleteById(id);
//     }

//     // ── Mappers ──────────────────────────────────────────────

//     private EmployeeResponse toResponse(Employee e) {
//         return EmployeeResponse.builder()
//                 .id(e.getId())
//                 .firstName(e.getFirstName())
//                 .lastName(e.getLastName())
//                 .email(e.getEmail())
//                 .department(e.getDepartment())
//                 .role(e.getRole())
//                 .salary(e.getSalary())
//                 .joinedAt(e.getJoinedAt())
//                 .build();
//     }

//     private Employee toEntity(EmployeeRequest r) {
//         return Employee.builder()
//                 .firstName(r.getFirstName())
//                 .lastName(r.getLastName())
//                 .email(r.getEmail())
//                 .department(r.getDepartment())
//                 .role(r.getRole())
//                 .salary(r.getSalary())
//                 .joinedAt(r.getJoinedAt())
//                 .build();
//     }
// }



import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final DepartmentRepository departmentRepository;

    // Paginated list of all employees
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable).map(this::toResponse);
    }

    // Search by keyword across name + email
    public Page<EmployeeResponse> searchEmployees(String keyword, Pageable pageable) {
        return employeeRepository.searchByKeyword(keyword, pageable).map(this::toResponse);
    }

    // Filter by department name
    public Page<EmployeeResponse> getByDepartment(String deptName, Pageable pageable) {
        return employeeRepository.findByDepartment_Name(deptName, pageable).map(this::toResponse);
    }

    // Filter by role
    public Page<EmployeeResponse> getByRole(String role, Pageable pageable) {
        return employeeRepository.findByRoleIgnoreCase(role, pageable).map(this::toResponse);
    }

    public EmployeeResponse getEmployeeById(Long id) {
        return toResponse(findById(id));
    }

    public EmployeeResponse createEmployee(EmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail()))
            throw new EmailAlreadyExistsException("Email in use: " + request.getEmail());
        Department dept = departmentService.findById(request.getDepartmentId());
        Employee employee = Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .department(dept)
                .role(request.getRole())
                .salary(request.getSalary())
                .joinedAt(request.getJoinedAt())
                .build();
        return toResponse(employeeRepository.save(employee));
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee existing = findById(id);
        if (!existing.getEmail().equals(request.getEmail()) &&
                employeeRepository.existsByEmail(request.getEmail()))
            throw new EmailAlreadyExistsException("Email in use: " + request.getEmail());
        Department dept = departmentService.findById(request.getDepartmentId());
        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setEmail(request.getEmail());
        existing.setDepartment(dept);
        existing.setRole(request.getRole());
        existing.setSalary(request.getSalary());
        existing.setJoinedAt(request.getJoinedAt());
        return toResponse(employeeRepository.save(existing));
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id))
            throw new ResourceNotFoundException("Employee not found: " + id);
        employeeRepository.deleteById(id);
    }

    private Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
    }

    private EmployeeResponse toResponse(Employee e) {
        return EmployeeResponse.builder()
                .id(e.getId())
                .firstName(e.getFirstName())
                .lastName(e.getLastName())
                .email(e.getEmail())
                .departmentName(e.getDepartment() != null ? e.getDepartment().getName() : null)
                .role(e.getRole())
                .salary(e.getSalary())
                .joinedAt(e.getJoinedAt())
                .build();
    }
}