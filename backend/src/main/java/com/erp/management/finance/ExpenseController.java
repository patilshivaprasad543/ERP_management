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
    @GetMapping("/employee/{employeeId}") public List<Expense> byEmployee(@PathVariable @Positive Long employeeId){return service.byEmployee(employeeId);}
    @GetMapping("/status/{status}") public List<Expense> byStatus(@PathVariable Expense.ExpenseStatus status){return service.byStatus(status);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Expense create(@Valid @RequestBody ExpenseRequest r){return service.create(r.employeeId(),r.category(),r.amount(),r.expenseDate(),r.description());}
    @PostMapping("/{id}/submit") public Expense submit(@PathVariable @Positive Long id){return service.submit(id);}
    @PatchMapping("/{id}/decision") public Expense decide(@PathVariable @Positive Long id,@Valid @RequestBody DecisionRequest r){return service.decide(id,r.status());}
    @PostMapping("/{id}/pay") public Expense pay(@PathVariable @Positive Long id){return service.markPaid(id);}
    @PostMapping("/{id}/cancel") public Expense cancel(@PathVariable @Positive Long id){return service.cancel(id);}
    public record ExpenseRequest(@NotNull @Positive Long employeeId,@NotBlank @Size(max=100) String category,@NotNull @DecimalMin("0.01") BigDecimal amount,@NotNull @PastOrPresent LocalDate expenseDate,@Size(max=500) String description){}
    public record DecisionRequest(@NotNull Expense.ExpenseStatus status){}
}
