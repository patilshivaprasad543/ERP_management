package com.erp.management.ai;

import com.erp.management.finance.ExpenseRepository;
import com.erp.management.finance.VendorPayableRepository;
import com.erp.management.inventory.InventoryRepository;
import com.erp.management.procurement.PurchaseOrderRepository;
import com.erp.management.procurement.PurchaseRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiInsightServiceOrderingTest {
    @Mock ExpenseRepository expenses;
    @Mock VendorPayableRepository payables;
    @Mock PurchaseRequestRepository requests;
    @Mock PurchaseOrderRepository orders;
    @Mock InventoryRepository inventory;

    @InjectMocks AiInsightService service;

    @Test
    void emptyDataUsesInformationalFallback() {
        when(expenses.findAll()).thenReturn(List.of());
        when(payables.findAll()).thenReturn(List.of());
        when(requests.findAll()).thenReturn(List.of());
        when(orders.findAll()).thenReturn(List.of());
        when(inventory.findAll()).thenReturn(List.of());

        AiInsightService.AiAnalysis result = service.analyze();

        assertEquals("INFO", result.insights().get(0).severity());
        assertEquals(0, result.highPriorityCount());
    }
}
