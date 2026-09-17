package com.erp.management.inventory;

import com.erp.management.finance.VendorPayableService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {
    @Mock InventoryRepository inventory;
    @Mock PurchaseOrderRepository orders;
    @Mock InventoryReceiptRepository receipts;
    @Mock VendorPayableService payables;
    private InventoryService service;

    @BeforeEach
    void setUp() { service = new InventoryService(inventory, orders, receipts, payables); }

    @Test
    void receivesSentOrderUpdatesStockAndCreatesReceipt() {
        PurchaseOrder order = PurchaseOrder.builder().id(1L).status(PurchaseOrder.PurchaseOrderStatus.SENT).build();
        InventoryItem item = InventoryItem.builder().id(2L).sku("LAP-01").name("Laptop")
                .quantityOnHand(new BigDecimal("10")).unitCost(new BigDecimal("100.00"))
                .status(InventoryItem.InventoryStatus.ACTIVE).build();
        when(orders.findById(1L)).thenReturn(Optional.of(order));
        when(receipts.findByPurchaseOrderId(1L)).thenReturn(Optional.empty());
        when(inventory.findBySkuIgnoreCase("LAP-01")).thenReturn(Optional.of(item));
        when(orders.save(any(PurchaseOrder.class))).thenAnswer(inv -> inv.getArgument(0));
        when(inventory.save(any(InventoryItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(receipts.save(any(InventoryReceipt.class))).thenAnswer(inv -> inv.getArgument(0));

        InventoryItem result = service.receivePurchaseOrder(1L, "LAP-01", "Laptop", "pcs",
                new BigDecimal("5"), new BigDecimal("120.00"));

        assertEquals(new BigDecimal("15"), result.getQuantityOnHand());
        assertEquals(new BigDecimal("106.67"), result.getUnitCost());
        assertEquals(PurchaseOrder.PurchaseOrderStatus.RECEIVED, order.getStatus());
        verify(receipts).save(any(InventoryReceipt.class));
        verify(payables).createForReceivedOrder(1L);
    }

    @Test
    void rejectsDuplicateReceipt() {
        when(orders.findById(1L)).thenReturn(Optional.of(PurchaseOrder.builder().id(1L)
                .status(PurchaseOrder.PurchaseOrderStatus.SENT).build()));
        when(receipts.findByPurchaseOrderId(1L)).thenReturn(Optional.of(InventoryReceipt.builder().id(7L).purchaseOrderId(1L).build()));
        assertThrows(IllegalArgumentException.class, () -> service.receivePurchaseOrder(1L, "X", "Item", "pcs",
                new BigDecimal("1"), new BigDecimal("10")));
        verify(inventory, never()).save(any());
        verify(payables, never()).createForReceivedOrder(any());
    }

    @Test
    void rejectsNonSentOrder() {
        when(orders.findById(1L)).thenReturn(Optional.of(PurchaseOrder.builder().id(1L)
                .status(PurchaseOrder.PurchaseOrderStatus.APPROVED).build()));
        assertThrows(IllegalArgumentException.class, () -> service.receivePurchaseOrder(1L, "X", "Item", "pcs",
                new BigDecimal("1"), new BigDecimal("10")));
        verify(inventory, never()).save(any());
    }
}
