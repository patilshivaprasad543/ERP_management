package com.erp.management.employee;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService service;

    @GetMapping
    public List<Employee> all() { return service.findAll(); }

    @GetMapping("/{id}")
    public Employee byId(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Employee create(@Valid @RequestBody CreateEmployee body) {
        return service.create(new EmployeeService.EmployeeRequest(body.employeeCode(), body.firstName(),
                body.lastName(), body.email(), body.phone(), body.joiningDate(), body.departmentId()));
    }

    public record CreateEmployee(
            @NotBlank @Size(max = 30) String employeeCode,
            @NotBlank @Size(max = 80) String firstName,
            @NotBlank @Size(max = 80) String lastName,
            @NotBlank @Email String email,
            @Size(max = 25) String phone,
            @NotNull LocalDate joiningDate,
            @NotNull Long departmentId) {}
}
