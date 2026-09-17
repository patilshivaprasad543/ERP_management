package com.erp.management.finance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VendorPayableRepository extends JpaRepository<VendorPayable, Long> {
    Optional<VendorPayable> findByPurchaseOrderId(Long purchaseOrderId);
    List<VendorPayable> findByStatusOrderByDueDateAsc(VendorPayable.PayableStatus status);
    List<VendorPayable> findAllByOrderByCreatedAtDesc();
}
