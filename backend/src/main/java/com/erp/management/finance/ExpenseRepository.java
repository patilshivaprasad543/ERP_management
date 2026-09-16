package com.erp.management.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByEmployeeIdOrderByExpenseDateDesc(Long employeeId);
    List<Expense> findByStatusOrderByExpenseDateAsc(Expense.ExpenseStatus status);
}
