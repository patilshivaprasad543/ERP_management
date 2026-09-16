package com.erp.management.employee;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employees;
    private final DepartmentRepository departments;

    public List<Employee> findAll() { return employees.findAll(); }

    public Employee findById(Long id) {
        return employees.findById(id).orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
    }

    @Transactional
    public Employee create(EmployeeRequest request) {
        if (employees.findByEmployeeCodeIgnoreCase(request.employeeCode()).isPresent())
            throw new IllegalArgumentException("Employee code already exists");
        if (employees.findById(request.departmentId()).isEmpty())
            throw new IllegalArgumentException("Department not found");
        Department department = departments.findById(request.departmentId()).orElseThrow();
        Employee employee = Employee.builder()
                .employeeCode(request.employeeCode())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .joiningDate(request.joiningDate())
                .department(department)
                .status(Employee.EmploymentStatus.ACTIVE)
                .build();
        return employees.save(employee);
    }

    public record EmployeeRequest(String employeeCode, String firstName, String lastName,
                                  String email, String phone, java.time.LocalDate joiningDate,
                                  Long departmentId) {}
}
