package com.erp.management.procurement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByPurchaseRequestId(Long purchaseRequestId);
    List<PurchaseOrder> findByVendorIdOrderByOrderDateDesc(Long vendorId);
    List<PurchaseOrder> findByStatusOrderByOrderDateDesc(PurchaseOrder.PurchaseOrderStatus status);
}
