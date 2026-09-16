package com.erp.management.finance;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/finance/payroll")
@RequiredArgsConstructor
public class PayrollController {
    private final PayrollRepository payroll;

    @GetMapping
    public List<PayrollRecord> byMonth(@RequestParam @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])") String month) {
        return payroll.findByPayrollMonthOrderByEmployeeIdAsc(month);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PayrollRecord create(@Valid @RequestBody PayrollRequest request) {
        if (payroll.findByEmployeeIdAndPayrollMonth(request.employeeId(), request.payrollMonth()).isPresent()) {
            throw new IllegalArgumentException("Payroll already exists for this employee and month");
        }
        if (request.deductions().compareTo(request.grossSalary()) > 0) {
            throw new IllegalArgumentException("Deductions cannot exceed gross salary");
        }
        return payroll.save(PayrollRecord.builder()
                .employeeId(request.employeeId())
                .payrollMonth(request.payrollMonth())
                .grossSalary(request.grossSalary())
                .deductions(request.deductions())
                .netSalary(request.grossSalary().subtract(request.deductions()))
                .status(PayrollRecord.PayrollStatus.DRAFT)
                .generatedAt(LocalDateTime.now())
                .build());
    }

    @PatchMapping("/{id}/status")
    public PayrollRecord status(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        PayrollRecord record = payroll.findById(id).orElseThrow(() -> new IllegalArgumentException("Payroll record not found"));
        record.setStatus(request.status());
        return payroll.save(record);
    }

    public record PayrollRequest(@NotNull Long employeeId,
                                 @NotBlank @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])") String payrollMonth,
                                 @NotNull @DecimalMin("0.00") BigDecimal grossSalary,
                                 @NotNull @DecimalMin("0.00") BigDecimal deductions) {}
    public record StatusRequest(@NotNull PayrollRecord.PayrollStatus status) {}
}
