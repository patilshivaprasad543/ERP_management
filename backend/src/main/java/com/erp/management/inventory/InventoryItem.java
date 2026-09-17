package com.erp.management.inventory;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "inventory_items", uniqueConstraints = @UniqueConstraint(columnNames = "sku"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 40) private String sku;
    @Column(nullable = false, length = 160) private String name;
    @Column(length = 80) private String unit;
    @Column(nullable = false, precision = 14, scale = 3) private BigDecimal quantityOnHand;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal unitCost;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private InventoryStatus status;

    public enum InventoryStatus { ACTIVE, INACTIVE }
}
