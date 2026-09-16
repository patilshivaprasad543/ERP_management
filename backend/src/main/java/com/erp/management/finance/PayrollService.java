package com.erp.management.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollService {
    private final PayrollRepository payroll;

    public List<PayrollRecord> byMonth(String month) {
        return payroll.findByPayrollMonthOrderByEmployeeIdAsc(month);
    }

    @Transactional
    public PayrollRecord create(Long employeeId, String payrollMonth, BigDecimal grossSalary, BigDecimal deductions) {
        if (payroll.findByEmployeeIdAndPayrollMonth(employeeId, payrollMonth).isPresent()) {
            throw new IllegalArgumentException("Payroll already exists for this employee and month");
        }
        validateMoney(grossSalary, deductions);
        return payroll.save(PayrollRecord.builder()
                .employeeId(employeeId)
                .payrollMonth(payrollMonth)
                .grossSalary(grossSalary)
                .deductions(deductions)
                .netSalary(grossSalary.subtract(deductions))
                .status(PayrollRecord.PayrollStatus.DRAFT)
                .generatedAt(LocalDateTime.now())
                .build());
    }

    @Transactional
    public PayrollRecord changeStatus(Long id, PayrollRecord.PayrollStatus target) {
        PayrollRecord record = payroll.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payroll record not found"));
        PayrollRecord.PayrollStatus current = record.getStatus();
        if (!isAllowedTransition(current, target)) {
            throw new IllegalArgumentException("Invalid payroll status transition: " + current + " -> " + target);
        }
        record.setStatus(target);
        return payroll.save(record);
    }

    private void validateMoney(BigDecimal grossSalary, BigDecimal deductions) {
        if (grossSalary.signum() < 0 || deductions.signum() < 0) {
            throw new IllegalArgumentException("Salary values cannot be negative");
        }
        if (deductions.compareTo(grossSalary) > 0) {
            throw new IllegalArgumentException("Deductions cannot exceed gross salary");
        }
    }

    private boolean isAllowedTransition(PayrollRecord.PayrollStatus current, PayrollRecord.PayrollStatus target) {
        return switch (current) {
            case DRAFT -> target == PayrollRecord.PayrollStatus.PROCESSED || target == PayrollRecord.PayrollStatus.CANCELLED;
            case PROCESSED -> target == PayrollRecord.PayrollStatus.PAID || target == PayrollRecord.PayrollStatus.CANCELLED;
            case PAID, CANCELLED -> false;
        };
    }
}
