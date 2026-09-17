package com.erp.management.ai;

import com.erp.management.finance.Expense;
import com.erp.management.finance.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiAnomalyServiceTest {
    @Mock ExpenseRepository expenses;
    @InjectMocks AiAnomalyService service;

    @Test
    void detectsExpenseAtLeastTwiceHistoricalAverage() {
        Expense normal = expense(1L, "Travel", "500.00");
        Expense anomaly = expense(2L, "Travel", "1200.00");
        when(expenses.findAll()).thenReturn(List.of(normal, anomaly));

        List<AiAnomalyService.ExpenseAnomaly> result = service.detect();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).expenseId());
        assertTrue(result.get(0).amount().compareTo(result.get(0).historicalAverage()) > 0);
    }

    @Test
    void ignoresSmallExpenseWithoutPeerHistory() {
        when(expenses.findAll()).thenReturn(List.of(expense(1L, "Travel", "900.00")));

        assertTrue(service.detect().isEmpty());
    }

    private Expense expense(Long id, String category, String amount) {
        return Expense.builder().id(id).employeeId(10L).category(category).amount(new BigDecimal(amount))
                .expenseDate(LocalDate.now()).status(Expense.ExpenseStatus.SUBMITTED).build();
    }
}
