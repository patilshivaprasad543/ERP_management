package com.erp.management.ai;

import com.erp.management.finance.ExpenseRepository;
import com.erp.management.finance.VendorPayableRepository;
import com.erp.management.inventory.InventoryItem;
import com.erp.management.inventory.InventoryRepository;
import com.erp.management.procurement.PurchaseOrderRepository;
import com.erp.management.procurement.PurchaseRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiInsightServiceSignalsTest {
    @Mock ExpenseRepository expenses;
    @Mock VendorPayableRepository payables;
    @Mock PurchaseRequestRepository requests;
    @Mock PurchaseOrderRepository orders;
    @Mock InventoryRepository inventory;

    @InjectMocks AiInsightService service;

    @Test
    void stockoutCreatesHighPriorityInventoryInsight() {
        InventoryItem item = InventoryItem.builder()
                .sku("SKU-001")
                .name("Test item")
                .unit("EA")
                .quantityOnHand(BigDecimal.ZERO)
                .unitCost(new BigDecimal("10.00"))
                .status(InventoryItem.InventoryStatus.ACTIVE)
                .build();
        when(inventory.findAll()).thenReturn(List.of(item));
        when(payables.findAll()).thenReturn(List.of());
        when(requests.findAll()).thenReturn(List.of());
        when(orders.findAll()).thenReturn(List.of());
        when(expenses.findAll()).thenReturn(List.of());

        AiInsightService.AiAnalysis result = service.analyze();

        assertTrue(result.highPriorityCount() >= 1);
        assertTrue(result.insights().stream().anyMatch(i -> "INVENTORY".equals(i.area())));
    }
}
