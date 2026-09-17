package com.erp.management.finance;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/finance/payroll")
@RequiredArgsConstructor
public class PayrollController {
    private final PayrollService service;

    @GetMapping
    public List<PayrollRecord> byMonth(@RequestParam @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])") String month) {
        return service.byMonth(month);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PayrollRecord create(@Valid @RequestBody PayrollRequest request) {
        return service.create(request.employeeId(), request.payrollMonth(), request.grossSalary(), request.deductions());
    }

    @PatchMapping("/{id}/status")
    public PayrollRecord status(@PathVariable @Positive Long id, @Valid @RequestBody StatusRequest request) {
        return service.changeStatus(id, request.status());
    }

    public record PayrollRequest(@NotNull @Positive Long employeeId,
                                 @NotBlank @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])") String payrollMonth,
                                 @NotNull @DecimalMin("0.00") BigDecimal grossSalary,
                                 @NotNull @DecimalMin("0.00") BigDecimal deductions) {}
    public record StatusRequest(@NotNull PayrollRecord.PayrollStatus status) {}
}
