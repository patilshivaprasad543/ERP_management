package com.erp.management.procurement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {
    private final PurchaseOrderRepository orders;
    private final PurchaseRequestRepository requests;
    private final VendorRepository vendors;

    public List<PurchaseOrder> all() { return orders.findAll(); }
    public List<PurchaseOrder> byVendor(Long vendorId) { return orders.findByVendorIdOrderByOrderDateDesc(vendorId); }
    public List<PurchaseOrder> byStatus(PurchaseOrder.PurchaseOrderStatus status) { return orders.findByStatusOrderByOrderDateDesc(status); }

    @Transactional
    public PurchaseOrder create(Long purchaseRequestId, LocalDate expectedDeliveryDate) {
        if (orders.findByPurchaseRequestId(purchaseRequestId).isPresent()) {
            throw new IllegalArgumentException("A purchase order already exists for this request");
        }
        PurchaseRequest request = requests.findById(purchaseRequestId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase request not found"));
        if (request.getStatus() != PurchaseRequest.PurchaseRequestStatus.APPROVED) {
            throw new IllegalArgumentException("Only approved purchase requests can create purchase orders");
        }
        Vendor vendor = vendors.findById(request.getVendorId())
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
        if (vendor.getStatus() != Vendor.VendorStatus.ACTIVE) {
            throw new IllegalArgumentException("Purchase order requires an active vendor");
        }
        if (expectedDeliveryDate != null && expectedDeliveryDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Expected delivery date cannot be in the past");
        }
        BigDecimal total = request.getQuantity().multiply(request.getEstimatedUnitPrice());
        String orderNumber = "PO-" + LocalDate.now().getYear() + "-" + System.currentTimeMillis();
        return orders.save(PurchaseOrder.builder()
                .purchaseRequestId(request.getId())
                .vendorId(request.getVendorId())
                .orderNumber(orderNumber)
                .totalAmount(total)
                .orderDate(LocalDate.now())
                .expectedDeliveryDate(expectedDeliveryDate)
                .status(PurchaseOrder.PurchaseOrderStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Transactional public PurchaseOrder approve(Long id) { return transition(id, PurchaseOrder.PurchaseOrderStatus.APPROVED); }
    @Transactional public PurchaseOrder send(Long id) { return transition(id, PurchaseOrder.PurchaseOrderStatus.SENT); }
    @Transactional public PurchaseOrder receive(Long id) { return transition(id, PurchaseOrder.PurchaseOrderStatus.RECEIVED); }
    @Transactional public PurchaseOrder cancel(Long id) { return transition(id, PurchaseOrder.PurchaseOrderStatus.CANCELLED); }

    private PurchaseOrder transition(Long id, PurchaseOrder.PurchaseOrderStatus target) {
        PurchaseOrder order = orders.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found"));
        PurchaseOrder.PurchaseOrderStatus current = order.getStatus();
        boolean allowed = switch (current) {
            case DRAFT -> target == PurchaseOrder.PurchaseOrderStatus.APPROVED || target == PurchaseOrder.PurchaseOrderStatus.CANCELLED;
            case APPROVED -> target == PurchaseOrder.PurchaseOrderStatus.SENT || target == PurchaseOrder.PurchaseOrderStatus.CANCELLED;
            case SENT -> target == PurchaseOrder.PurchaseOrderStatus.RECEIVED || target == PurchaseOrder.PurchaseOrderStatus.CANCELLED;
            case RECEIVED, CANCELLED -> false;
        };
        if (!allowed) throw new IllegalArgumentException("Invalid purchase order status transition: " + current + " -> " + target);
        order.setStatus(target);
        return orders.save(order);
    }
}
