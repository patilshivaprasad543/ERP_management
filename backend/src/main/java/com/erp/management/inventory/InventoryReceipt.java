package com.erp.management.inventory;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_receipts", uniqueConstraints = @UniqueConstraint(columnNames = "purchase_order_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryReceipt {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "purchase_order_id", nullable = false, unique = true)
    private Long purchaseOrderId;

    @Column(nullable = false, length = 40)
    private String sku;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit_cost", nullable = false, precision = 14, scale = 2)
    private BigDecimal unitCost;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;
}
