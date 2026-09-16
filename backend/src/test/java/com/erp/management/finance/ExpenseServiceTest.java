package com.erp.management.finance;

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
class ExpenseServiceTest {
    @Mock ExpenseRepository repository;
    private ExpenseService service;

    @BeforeEach
    void setUp() { service = new ExpenseService(repository); }

    @Test
    void createsDraftExpense() {
        Expense saved = Expense.builder().id(1L).status(Expense.ExpenseStatus.DRAFT).build();
        when(repository.save(any(Expense.class))).thenReturn(saved);
        Expense result = service.create(7L, "Travel", new BigDecimal("1250.00"), LocalDate.now(), "Client visit");
        assertEquals(Expense.ExpenseStatus.DRAFT, result.getStatus());
        verify(repository).save(any(Expense.class));
    }

    @Test
    void rejectsFutureExpenseDate() {
        assertThrows(IllegalArgumentException.class, () -> service.create(7L, "Travel", new BigDecimal("100"), LocalDate.now().plusDays(1), "Future"));
        verify(repository, never()).save(any());
    }

    @Test
    void enforcesWorkflow() {
        Expense expense = Expense.builder().id(1L).status(Expense.ExpenseStatus.SUBMITTED).build();
        when(repository.findById(1L)).thenReturn(Optional.of(expense));
        when(repository.save(any(Expense.class))).thenAnswer(inv -> inv.getArgument(0));
        assertEquals(Expense.ExpenseStatus.APPROVED, service.decide(1L, Expense.ExpenseStatus.APPROVED).getStatus());
        assertThrows(IllegalArgumentException.class, () -> service.decide(1L, Expense.ExpenseStatus.PAID));
    }
}
