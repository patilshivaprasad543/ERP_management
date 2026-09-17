package com.erp.management.procurement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorService {
    private final VendorRepository vendors;

    public List<Vendor> all() { return vendors.findAll(); }
    public List<Vendor> byStatus(Vendor.VendorStatus status) { return vendors.findByStatusOrderByNameAsc(status); }

    @Transactional
    public Vendor create(String code, String name, String email, String phone, String address) {
        if (vendors.findByVendorCodeIgnoreCase(code).isPresent()) {
            throw new IllegalArgumentException("Vendor code already exists");
        }
        return vendors.save(Vendor.builder()
                .vendorCode(code.trim())
                .name(name.trim())
                .email(email)
                .phone(phone)
                .address(address)
                .status(Vendor.VendorStatus.PENDING)
                .build());
    }

    @Transactional
    public Vendor activate(Long id) { return transition(id, Vendor.VendorStatus.ACTIVE); }
    @Transactional
    public Vendor suspend(Long id) { return transition(id, Vendor.VendorStatus.SUSPENDED); }
    @Transactional
    public Vendor deactivate(Long id) { return transition(id, Vendor.VendorStatus.INACTIVE); }

    private Vendor transition(Long id, Vendor.VendorStatus target) {
        Vendor vendor = vendors.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
        Vendor.VendorStatus current = vendor.getStatus();
        boolean allowed = switch (current) {
            case PENDING -> target == Vendor.VendorStatus.ACTIVE || target == Vendor.VendorStatus.INACTIVE;
            case ACTIVE -> target == Vendor.VendorStatus.SUSPENDED || target == Vendor.VendorStatus.INACTIVE;
            case SUSPENDED -> target == Vendor.VendorStatus.ACTIVE || target == Vendor.VendorStatus.INACTIVE;
            case INACTIVE -> false;
        };
        if (!allowed) {
            throw new IllegalArgumentException("Invalid vendor status transition: " + current + " -> " + target);
        }
        vendor.setStatus(target);
        return vendors.save(vendor);
    }
}
