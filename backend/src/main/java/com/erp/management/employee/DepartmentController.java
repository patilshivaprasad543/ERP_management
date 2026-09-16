package com.erp.management.employee;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentRepository repository;

    @GetMapping
    public List<Department> all() { return repository.findAll(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Department create(@Valid @RequestBody CreateDepartment body) {
        repository.findByCodeIgnoreCase(body.code()).ifPresent(d -> { throw new IllegalArgumentException("Department code already exists"); });
        return repository.save(Department.builder().name(body.name()).code(body.code()).description(body.description()).active(true).build());
    }

    public record CreateDepartment(@NotBlank String name, @NotBlank String code, String description) {}
}
