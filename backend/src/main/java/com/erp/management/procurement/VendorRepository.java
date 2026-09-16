package com.erp.management.procurement;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByVendorCodeIgnoreCase(String vendorCode);
    List<Vendor> findByStatusOrderByNameAsc(Vendor.VendorStatus status);
}
