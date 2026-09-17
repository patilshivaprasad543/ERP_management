package com.erp.management.finance;

import com.erp.management.procurement.PurchaseOrder;
import com.erp.management.procurement.PurchaseOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorPayableServiceTest {
    @Mock VendorPayableRepository payables;
    @Mock PurchaseOrderRepository orders;
    private VendorPayableService service;

    @BeforeEach
    void setUp() { service = new VendorPayableService(payables, orders); }

    @Test
    void createsPayableForReceivedOrderAndIsIdempotent() {
        PurchaseOrder order = PurchaseOrder.builder().id(7L).vendorId(3L)
                .totalAmount(new BigDecimal("4500.00"))
                .status(PurchaseOrder.PurchaseOrderStatus.RECEIVED).build();
        when(payables.findByPurchaseOrderId(7L)).thenReturn(Optional.empty());
        when(orders.findById(7L)).thenReturn(Optional.of(order));
        when(payables.save(any(VendorPayable.class))).thenAnswer(inv -> inv.getArgument(0));

        VendorPayable result = service.createForReceivedOrder(7L);

        assertEquals(7L, result.getPurchaseOrderId());
        assertEquals(3L, result.getVendorId());
        assertEquals(new BigDecimal("4500.00"), result.getAmount());
        assertEquals(VendorPayable.PayableStatus.OPEN, result.getStatus());

        VendorPayable existing = VendorPayable.builder().id(20L).purchaseOrderId(7L)
                .status(VendorPayable.PayableStatus.OPEN).build();
        when(payables.findByPurchaseOrderId(7L)).thenReturn(Optional.of(existing));
        assertSame(existing, service.createForReceivedOrder(7L));
        verify(payables, times(1)).save(any(VendorPayable.class));
    }

    @Test
    void rejectsUnreceivedOrder() {
        PurchaseOrder order = PurchaseOrder.builder().id(7L).status(PurchaseOrder.PurchaseOrderStatus.SENT).build();
        when(payables.findByPurchaseOrderId(7L)).thenReturn(Optional.empty());
        when(orders.findById(7L)).thenReturn(Optional.of(order));
        assertThrows(IllegalArgumentException.class, () -> service.createForReceivedOrder(7L));
        verify(payables, never()).save(any());
    }

    @Test
    void enforcesApproveThenPayWorkflow() {
        VendorPayable payable = VendorPayable.builder().id(20L)
                .status(VendorPayable.PayableStatus.OPEN).build();
        when(payables.findById(20L)).thenReturn(Optional.of(payable));
        when(payables.save(any(VendorPayable.class))).thenAnswer(inv -> inv.getArgument(0));

        assertEquals(VendorPayable.PayableStatus.APPROVED, service.approve(20L).getStatus());
        assertEquals(VendorPayable.PayableStatus.PAID, service.pay(20L).getStatus());
        assertNotNull(payable.getPaidAt());
        assertThrows(IllegalArgumentException.class, () -> service.pay(20L));
    }
}
