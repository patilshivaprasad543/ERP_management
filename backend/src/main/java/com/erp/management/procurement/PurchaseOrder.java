package com.erp.management.procurement;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_orders", uniqueConstraints = @UniqueConstraint(columnNames = "order_number"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_request_id", nullable = false, unique = true)
    private Long purchaseRequestId;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(name = "order_number", nullable = false, length = 40)
    private String orderNumber;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    @Column(name = "expected_delivery_date")
    private LocalDate expectedDeliveryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PurchaseOrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum PurchaseOrderStatus { DRAFT, APPROVED, SENT, RECEIVED, CANCELLED }
}
