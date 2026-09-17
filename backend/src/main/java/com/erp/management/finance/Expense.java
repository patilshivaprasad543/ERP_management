package com.erp.management.finance;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Expense {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employee_id", nullable = false)
    private Long employeeId;
    @Column(nullable = false, length = 100)
    private String category;
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false)
    private LocalDate expenseDate;
    @Column(length = 500)
    private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private ExpenseStatus status;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum ExpenseStatus { DRAFT, SUBMITTED, APPROVED, REJECTED, PAID, CANCELLED }
}
