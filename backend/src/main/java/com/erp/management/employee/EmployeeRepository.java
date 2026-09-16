package com.erp.management.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeCodeIgnoreCase(String employeeCode);
    List<Employee> findByDepartmentIdOrderByFirstNameAsc(Long departmentId);
    List<Employee> findByStatusOrderByFirstNameAsc(Employee.EmploymentStatus status);
}
