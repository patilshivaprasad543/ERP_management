package com.erp.management.finance;

import com.erp.management.procurement.PurchaseOrder;
import com.erp.management.procurement.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorPayableService {
    private final VendorPayableRepository payables;
    private final PurchaseOrderRepository orders;

    public List<VendorPayable> all() { return payables.findAllByOrderByCreatedAtDesc(); }

    public List<VendorPayable> byStatus(VendorPayable.PayableStatus status) {
        return payables.findByStatusOrderByDueDateAsc(status);
    }

    @Transactional
    public VendorPayable createForReceivedOrder(Long purchaseOrderId) {
        VendorPayable existing = payables.findByPurchaseOrderId(purchaseOrderId).orElse(null);
        if (existing != null) return existing;

        PurchaseOrder order = orders.findById(purchaseOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found"));
        if (order.getStatus() != PurchaseOrder.PurchaseOrderStatus.RECEIVED) {
            throw new IllegalArgumentException("Payable can only be created for a received purchase order");
        }

        return payables.save(VendorPayable.builder()
                .purchaseOrderId(order.getId())
                .vendorId(order.getVendorId())
                .amount(order.getTotalAmount())
                .dueDate(order.getExpectedDeliveryDate() == null ? LocalDate.now() : order.getExpectedDeliveryDate())
                .status(VendorPayable.PayableStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Transactional
    public VendorPayable approve(Long id) {
        VendorPayable payable = get(id);
        if (payable.getStatus() != VendorPayable.PayableStatus.OPEN) {
            throw new IllegalArgumentException("Only open payables can be approved");
        }
        payable.setStatus(VendorPayable.PayableStatus.APPROVED);
        return payables.save(payable);
    }

    @Transactional
    public VendorPayable pay(Long id) {
        VendorPayable payable = get(id);
        if (payable.getStatus() != VendorPayable.PayableStatus.APPROVED) {
            throw new IllegalArgumentException("Only approved payables can be paid");
        }
        payable.setStatus(VendorPayable.PayableStatus.PAID);
        payable.setPaidAt(LocalDateTime.now());
        return payables.save(payable);
    }

    @Transactional
    public VendorPayable cancel(Long id) {
        VendorPayable payable = get(id);
        if (payable.getStatus() == VendorPayable.PayableStatus.PAID || payable.getStatus() == VendorPayable.PayableStatus.CANCELLED) {
            throw new IllegalArgumentException("This payable cannot be cancelled");
        }
        payable.setStatus(VendorPayable.PayableStatus.CANCELLED);
        return payables.save(payable);
    }

    private VendorPayable get(Long id) {
        return payables.findById(id).orElseThrow(() -> new IllegalArgumentException("Payable not found"));
    }
}
