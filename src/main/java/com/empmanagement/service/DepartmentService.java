package com.empmanagement.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.empmanagement.dto.DepartmentRequest;
import com.empmanagement.dto.DepartmentResponse;
import com.empmanagement.entity.Department;
import com.empmanagement.exception.ResourceNotFoundException;
import com.empmanagement.repo.DepartmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DepartmentResponse getDepartmentById(Long id) {
        return toResponse(findById(id));
    }

    public DepartmentResponse createDepartment(DepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName()))
            throw new IllegalArgumentException("Department already exists: " + request.getName());
        Department dept = Department.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return toResponse(departmentRepository.save(dept));
    }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        Department dept = findById(id);
        dept.setName(request.getName());
        dept.setDescription(request.getDescription());
        return toResponse(departmentRepository.save(dept));
    }

    public void deleteDepartment(Long id) {
        if (!departmentRepository.existsById(id))
            throw new ResourceNotFoundException("Department not found: " + id);
        departmentRepository.deleteById(id);
    }

    public Department findById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }

    private DepartmentResponse toResponse(Department d) {
        int count = d.getEmployees() == null ? 0 : d.getEmployees().size();
        return DepartmentResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .employeeCount(count)
                .build();
    }
}