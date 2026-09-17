package com.erp.management.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryReceiptRepository extends JpaRepository<InventoryReceipt, Long> {
    Optional<InventoryReceipt> findByPurchaseOrderId(Long purchaseOrderId);
    List<InventoryReceipt> findAllByOrderByReceivedAtDesc();
}
