package com.erp.management.procurement;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PurchaseRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requester_employee_id", nullable = false)
    private Long requesterEmployeeId;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(name = "item_description", nullable = false, length = 300)
    private String itemDescription;

    @Column(nullable = false, precision = 14, scale = 3)
    private BigDecimal quantity;

    @Column(name = "estimated_unit_price", nullable = false, precision = 14, scale = 2)
    private BigDecimal estimatedUnitPrice;

    @Column(name = "requested_date", nullable = false)
    private LocalDate requestedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PurchaseRequestStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum PurchaseRequestStatus { DRAFT, SUBMITTED, APPROVED, REJECTED, CANCELLED }
}
