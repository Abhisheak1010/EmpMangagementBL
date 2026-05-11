package com.empmanagement.repo;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.empmanagement.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);


     // Search by name or email (case-insensitive)
    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.lastName)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.email)     LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Employee> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Filter by department name
    Page<Employee> findByDepartment_Name(String departmentName, Pageable pageable);

    // Filter by role
    Page<Employee> findByRoleIgnoreCase(String role, Pageable pageable);
}