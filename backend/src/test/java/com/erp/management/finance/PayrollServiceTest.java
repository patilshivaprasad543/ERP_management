package com.erp.management.finance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PayrollServiceTest {
    @Mock
    private PayrollRepository payroll;

    @InjectMocks
    private PayrollService service;

    @Test
    void createCalculatesNetSalaryAndStartsAsDraft() {
        when(payroll.findByEmployeeIdAndPayrollMonth(1L, "2026-09")).thenReturn(Optional.empty());
        PayrollRecord saved = PayrollRecord.builder().id(10L).build();
        when(payroll.save(any(PayrollRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PayrollRecord result = service.create(1L, "2026-09", new BigDecimal("50000.00"), new BigDecimal("7500.00"));

        assertEquals(new BigDecimal("42500.00"), result.getNetSalary());
        assertEquals(PayrollRecord.PayrollStatus.DRAFT, result.getStatus());
        assertNotNull(result.getGeneratedAt());
    }

    @Test
    void rejectsDeductionsAboveGross() {
        assertThrows(IllegalArgumentException.class,
                () -> service.create(1L, "2026-09", new BigDecimal("50000.00"), new BigDecimal("50000.01")));
        verify(payroll, never()).save(any());
    }

    @Test
    void allowsForwardStatusTransitions() {
        PayrollRecord record = PayrollRecord.builder().id(1L).status(PayrollRecord.PayrollStatus.DRAFT).build();
        when(payroll.findById(1L)).thenReturn(Optional.of(record));
        when(payroll.save(record)).thenReturn(record);

        service.changeStatus(1L, PayrollRecord.PayrollStatus.PROCESSED);
        assertEquals(PayrollRecord.PayrollStatus.PROCESSED, record.getStatus());
    }

    @Test
    void rejectsBackwardStatusTransitions() {
        PayrollRecord record = PayrollRecord.builder().id(1L).status(PayrollRecord.PayrollStatus.PROCESSED).build();
        when(payroll.findById(1L)).thenReturn(Optional.of(record));

        assertThrows(IllegalArgumentException.class,
                () -> service.changeStatus(1L, PayrollRecord.PayrollStatus.DRAFT));
        verify(payroll, never()).save(any());
    }
}
