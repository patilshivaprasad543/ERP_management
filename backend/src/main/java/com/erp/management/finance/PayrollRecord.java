package com.erp.management.finance;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_records", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "payroll_month"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayrollRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "employee_id", nullable = false) private Long employeeId;
    @Column(name = "payroll_month", nullable = false, length = 7) private String payrollMonth;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal grossSalary;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal deductions;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal netSalary;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private PayrollStatus status;
    @Column(nullable = false) private LocalDateTime generatedAt;

    public enum PayrollStatus { DRAFT, PROCESSED, PAID, CANCELLED }
}
