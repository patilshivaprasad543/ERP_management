package com.erp.management.procurement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {
    @Mock PurchaseOrderRepository orders;
    @Mock PurchaseRequestRepository requests;
    @Mock VendorRepository vendors;
    private PurchaseOrderService service;

    @BeforeEach
    void setUp() { service = new PurchaseOrderService(orders, requests, vendors); }

    @Test
    void createsOrderFromApprovedRequestAndCalculatesTotal() {
        PurchaseRequest request = PurchaseRequest.builder().id(10L).vendorId(2L)
                .quantity(new BigDecimal("3")).estimatedUnitPrice(new BigDecimal("1250.00"))
                .status(PurchaseRequest.PurchaseRequestStatus.APPROVED).build();
        when(orders.findByPurchaseRequestId(10L)).thenReturn(Optional.empty());
        when(requests.findById(10L)).thenReturn(Optional.of(request));
        when(vendors.findById(2L)).thenReturn(Optional.of(Vendor.builder().id(2L).status(Vendor.VendorStatus.ACTIVE).build()));
        when(orders.save(any(PurchaseOrder.class))).thenAnswer(inv -> inv.getArgument(0));
        PurchaseOrder result = service.create(10L, LocalDate.now().plusDays(5));
        assertEquals(new BigDecimal("3750.00"), result.getTotalAmount());
        assertEquals(PurchaseOrder.PurchaseOrderStatus.DRAFT, result.getStatus());
    }

    @Test
    void rejectsUnapprovedRequest() {
        PurchaseRequest request = PurchaseRequest.builder().id(10L).vendorId(2L)
                .status(PurchaseRequest.PurchaseRequestStatus.SUBMITTED).build();
        when(orders.findByPurchaseRequestId(10L)).thenReturn(Optional.empty());
        when(requests.findById(10L)).thenReturn(Optional.of(request));
        assertThrows(IllegalArgumentException.class, () -> service.create(10L, null));
        verify(orders, never()).save(any());
    }

    @Test
    void enforcesOrderWorkflow() {
        PurchaseOrder order = PurchaseOrder.builder().id(1L).status(PurchaseOrder.PurchaseOrderStatus.DRAFT).build();
        when(orders.findById(1L)).thenReturn(Optional.of(order));
        when(orders.save(any(PurchaseOrder.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(PurchaseOrder.PurchaseOrderStatus.APPROVED, service.approve(1L).getStatus());
        assertThrows(IllegalArgumentException.class, () -> service.approve(1L));
    }
}
