package com.erp.management.procurement;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vendors", uniqueConstraints = @UniqueConstraint(columnNames = "vendorCode"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vendor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 30) private String vendorCode;
    @Column(nullable = false, length = 160) private String name;
    @Column(length = 160) private String email;
    @Column(length = 25) private String phone;
    @Column(length = 500) private String address;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private VendorStatus status;

    public enum VendorStatus { PENDING, ACTIVE, SUSPENDED, INACTIVE }
}
