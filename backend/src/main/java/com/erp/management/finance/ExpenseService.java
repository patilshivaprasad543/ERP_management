package com.erp.management.finance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpenseRepository expenses;

    public List<Expense> byEmployee(Long employeeId) {
        return expenses.findByEmployeeIdOrderByExpenseDateDesc(employeeId);
    }

    public List<Expense> byStatus(Expense.ExpenseStatus status) {
        return expenses.findByStatusOrderByExpenseDateAsc(status);
    }

    @Transactional
    public Expense create(Long employeeId, String category, BigDecimal amount,
                          LocalDate expenseDate, String description) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Expense amount must be greater than zero");
        }
        if (expenseDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Expense date cannot be in the future");
        }
        return expenses.save(Expense.builder()
                .employeeId(employeeId)
                .category(category.trim())
                .amount(amount)
                .expenseDate(expenseDate)
                .description(description)
                .status(Expense.ExpenseStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Transactional
    public Expense submit(Long id) {
        return transition(id, Expense.ExpenseStatus.SUBMITTED);
    }

    @Transactional
    public Expense decide(Long id, Expense.ExpenseStatus target) {
        if (target != Expense.ExpenseStatus.APPROVED && target != Expense.ExpenseStatus.REJECTED) {
            throw new IllegalArgumentException("Decision must be APPROVED or REJECTED");
        }
        return transition(id, target);
    }

    @Transactional
    public Expense markPaid(Long id) {
        return transition(id, Expense.ExpenseStatus.PAID);
    }

    @Transactional
    public Expense cancel(Long id) {
        return transition(id, Expense.ExpenseStatus.CANCELLED);
    }

    private Expense transition(Long id, Expense.ExpenseStatus target) {
        Expense expense = expenses.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found"));
        Expense.ExpenseStatus current = expense.getStatus();
        boolean allowed = switch (current) {
            case DRAFT -> target == Expense.ExpenseStatus.SUBMITTED || target == Expense.ExpenseStatus.CANCELLED;
            case SUBMITTED -> target == Expense.ExpenseStatus.APPROVED || target == Expense.ExpenseStatus.REJECTED || target == Expense.ExpenseStatus.CANCELLED;
            case APPROVED -> target == Expense.ExpenseStatus.PAID || target == Expense.ExpenseStatus.CANCELLED;
            case REJECTED, PAID, CANCELLED -> false;
        };
        if (!allowed) {
            throw new IllegalArgumentException("Invalid expense status transition: " + current + " -> " + target);
        }
        expense.setStatus(target);
        return expenses.save(expense);
    }
}
