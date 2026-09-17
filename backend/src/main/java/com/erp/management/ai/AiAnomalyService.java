package com.erp.management.ai;

import com.erp.management.finance.Expense;
import com.erp.management.finance.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiAnomalyService {
    private static final BigDecimal MIN_ANOMALY_AMOUNT = new BigDecimal("1000.00");
    private static final BigDecimal RATIO = new BigDecimal("2.00");
    private final ExpenseRepository expenses;

    public List<ExpenseAnomaly> detect() {
        List<Expense> records = expenses.findAll();
        List<ExpenseAnomaly> result = new ArrayList<>();
        for (Expense expense : records) {
            if (expense.getAmount() == null || expense.getAmount().compareTo(MIN_ANOMALY_AMOUNT) < 0) continue;
            BigDecimal peerTotal = records.stream()
                    .filter(e -> e.getId() != null && !e.getId().equals(expense.getId()))
                    .filter(e -> expense.getEmployeeId().equals(e.getEmployeeId()))
                    .filter(e -> expense.getCategory().equalsIgnoreCase(e.getCategory()))
                    .map(Expense::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            long peerCount = records.stream()
                    .filter(e -> e.getId() != null && !e.getId().equals(expense.getId()))
                    .filter(e -> expense.getEmployeeId().equals(e.getEmployeeId()))
                    .filter(e -> expense.getCategory().equalsIgnoreCase(e.getCategory()))
                    .count();
            if (peerCount == 0) continue;
            BigDecimal average = peerTotal.divide(BigDecimal.valueOf(peerCount), 2, RoundingMode.HALF_UP);
            if (average.signum() > 0 && expense.getAmount().compareTo(average.multiply(RATIO)) >= 0) {
                result.add(new ExpenseAnomaly(expense.getId(), expense.getEmployeeId(), expense.getCategory(), expense.getAmount(), average, "MEDIUM", "Expense is at least 2x the employee/category historical average."));
            }
        }
        return result;
    }

    public record ExpenseAnomaly(Long expenseId, Long employeeId, String category, BigDecimal amount, BigDecimal historicalAverage, String severity, String reason) {}
}
