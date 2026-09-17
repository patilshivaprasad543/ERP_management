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
class PurchaseRequestServiceTest {
    @Mock PurchaseRequestRepository requests;
    @Mock VendorRepository vendors;
    private PurchaseRequestService service;

    @BeforeEach
    void setUp() { service = new PurchaseRequestService(requests, vendors); }

    @Test
    void createsDraftForActiveVendor() {
        Vendor vendor = Vendor.builder().id(2L).status(Vendor.VendorStatus.ACTIVE).build();
        when(vendors.findById(2L)).thenReturn(Optional.of(vendor));
        when(requests.save(any(PurchaseRequest.class))).thenAnswer(inv -> inv.getArgument(0));
        PurchaseRequest result = service.create(7L, 2L, "Laptop", new BigDecimal("2"), new BigDecimal("50000"), LocalDate.now());
        assertEquals(PurchaseRequest.PurchaseRequestStatus.DRAFT, result.getStatus());
        verify(requests).save(any(PurchaseRequest.class));
    }

    @Test
    void rejectsInactiveVendor() {
        when(vendors.findById(2L)).thenReturn(Optional.of(Vendor.builder().id(2L).status(Vendor.VendorStatus.SUSPENDED).build()));
        assertThrows(IllegalArgumentException.class, () -> service.create(7L, 2L, "Laptop", new BigDecimal("1"), new BigDecimal("50000"), LocalDate.now()));
        verify(requests, never()).save(any());
    }

    @Test
    void enforcesWorkflow() {
        PurchaseRequest request = PurchaseRequest.builder().id(1L).status(PurchaseRequest.PurchaseRequestStatus.DRAFT).build();
        when(requests.findById(1L)).thenReturn(Optional.of(request));
        when(requests.save(any(PurchaseRequest.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(PurchaseRequest.PurchaseRequestStatus.SUBMITTED, service.submit(1L).getStatus());
        assertThrows(IllegalArgumentException.class, () -> service.submit(1L));
    }
}
