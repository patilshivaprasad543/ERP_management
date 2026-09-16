package com.erp.management.procurement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendorServiceTest {
    @Mock VendorRepository repository;
    private VendorService service;

    @BeforeEach
    void setUp() { service = new VendorService(repository); }

    @Test
    void createsPendingVendor() {
        when(repository.findByVendorCodeIgnoreCase("V-001")).thenReturn(Optional.empty());
        when(repository.save(any(Vendor.class))).thenAnswer(inv -> inv.getArgument(0));
        Vendor result = service.create("V-001", "Acme Supplies", "a@acme.test", null, null);
        assertEquals(Vendor.VendorStatus.PENDING, result.getStatus());
        verify(repository).save(any(Vendor.class));
    }

    @Test
    void rejectsDuplicateVendorCode() {
        when(repository.findByVendorCodeIgnoreCase("V-001")).thenReturn(Optional.of(new Vendor()));
        assertThrows(IllegalArgumentException.class, () -> service.create("V-001", "Acme", null, null, null));
        verify(repository, never()).save(any());
    }

    @Test
    void enforcesVendorWorkflow() {
        Vendor vendor = Vendor.builder().id(1L).status(Vendor.VendorStatus.PENDING).build();
        when(repository.findById(1L)).thenReturn(Optional.of(vendor));
        when(repository.save(any(Vendor.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(Vendor.VendorStatus.ACTIVE, service.activate(1L).getStatus());
        assertThrows(IllegalArgumentException.class, () -> service.activate(1L));
    }
}
