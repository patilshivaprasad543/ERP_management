package com.erp.management.finance;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_payables", uniqueConstraints = @UniqueConstraint(columnNames = "purchase_order_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VendorPayable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_order_id", nullable = false, unique = true)
    private Long purchaseOrderId;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(name = "invoice_number", length = 80)
    private String invoiceNumber;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayableStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    public enum PayableStatus { OPEN, APPROVED, PAID, CANCELLED }
}
