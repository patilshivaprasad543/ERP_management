package com.erp.management.reports;

import com.erp.management.finance.ExpenseRepository;
import com.erp.management.finance.VendorPayableRepository;
import com.erp.management.inventory.InventoryRepository;
import com.erp.management.procurement.PurchaseOrderRepository;
import com.erp.management.procurement.PurchaseRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErpReportServiceTest {
    @Mock ExpenseRepository expenses;
    @Mock VendorPayableRepository payables;
    @Mock PurchaseRequestRepository requests;
    @Mock PurchaseOrderRepository orders;
    @Mock InventoryRepository inventory;

    @Test
    void buildsDashboardFromOperationalData() {
        when(expenses.findAll()).thenReturn(List.of());
        when(payables.findAll()).thenReturn(List.of());
        when(requests.findAll()).thenReturn(List.of());
        when(orders.findAll()).thenReturn(List.of());
        when(inventory.findAll()).thenReturn(List.of());
        ErpReportService.DashboardReport report = new ErpReportService(expenses, payables, requests, orders, inventory).dashboard();
        assertEquals(0, report.purchaseRequestCount());
        assertEquals(0, report.purchaseOrderCount());
        assertEquals(0, report.inventoryItemCount());
        assertEquals(0, report.expenseTotal().signum());
        assertEquals(0, report.openPayableTotal().signum());
        assertEquals(0, report.inventoryValue().signum());
    }
}
