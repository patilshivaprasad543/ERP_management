package com.erp.management.finance;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/finance/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService service;

    @GetMapping("/employee/{employeeId}")
    public List<Expense> byEmployee(@PathVariable @Positive Long employeeId) {
        return service.byEmployee(employeeId);
    }

    @GetMapping("/status/{status}")
    public List<Expense> byStatus(@PathVariable Expense.ExpenseStatus status) {
        return service.byStatus(status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Expense create(@Valid @RequestBody ExpenseRequest request) {
        return service.create(request.employeeId(), request.category(), request.amount(), request.expenseDate(), request.description());
    }

    @PostMapping("/{id}/submit")
    public Expense submit(@PathVariable Long id) { return service.submit(id); }

    @PatchMapping("/{id}/decision")
    public Expense decide(@PathVariable Long id, @Valid @RequestBody DecisionRequest request) {
        return service.decide(id, request.status());
    }

    @PostMapping("/{id}/pay")
    public Expense pay(@PathVariable Long id) { return service.markPaid(id); }

    @PostMapping("/{id}/cancel")
    public Expense cancel(@PathVariable Long id) { return service.cancel(id); }

    public record ExpenseRequest(@NotNull @Positive Long employeeId,
                                 @NotBlank @Size(max = 100) String category,
                                 @NotNull @DecimalMin("0.01") BigDecimal amount,
                                 @NotNull LocalDate expenseDate,
                                 @Size(max = 500) String description) {}

    public record DecisionRequest(@NotNull Expense.ExpenseStatus status) {}
}
