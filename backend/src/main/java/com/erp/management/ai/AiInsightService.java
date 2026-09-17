package com.erp.management.ai;

import com.erp.management.finance.Expense;
import com.erp.management.finance.ExpenseRepository;
import com.erp.management.finance.VendorPayable;
import com.erp.management.finance.VendorPayableRepository;
import com.erp.management.inventory.InventoryItem;
import com.erp.management.inventory.InventoryRepository;
import com.erp.management.procurement.PurchaseOrder;
import com.erp.management.procurement.PurchaseOrderRepository;
import com.erp.management.procurement.PurchaseRequest;
import com.erp.management.procurement.PurchaseRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiInsightService {
    private final ExpenseRepository expenses;
    private final VendorPayableRepository payables;
    private final PurchaseRequestRepository requests;
    private final PurchaseOrderRepository orders;
    private final InventoryRepository inventory;

    public AiAnalysis analyze() {
        List<AiInsight> insights = new ArrayList<>();
        List<InventoryItem> items = inventory.findAll();
        List<VendorPayable> payableList = payables.findAll();
        List<PurchaseRequest> requestList = requests.findAll();
        List<PurchaseOrder> orderList = orders.findAll();
        List<Expense> expenseList = expenses.findAll();

        long lowStock = items.stream().filter(i -> i.getStatus() == InventoryItem.InventoryStatus.ACTIVE && i.getQuantityOnHand().compareTo(BigDecimal.ZERO) <= 0).count();
        if (lowStock > 0) {
            insights.add(new AiInsight("HIGH", "INVENTORY", "Stockout risk detected", lowStock + " active inventory item(s) have zero or negative stock. Review replenishment immediately."));
        }

        long overdue = payableList.stream().filter(p -> p.getStatus() == VendorPayable.PayableStatus.OPEN && p.getDueDate() != null && p.getDueDate().isBefore(java.time.LocalDate.now())).count();
        if (overdue > 0) {
            insights.add(new AiInsight("HIGH", "FINANCE", "Overdue payables", overdue + " open payable(s) are past their due date."));
        }

        long submittedRequests = requestList.stream().filter(r -> r.getStatus() == PurchaseRequest.PurchaseRequestStatus.SUBMITTED).count();
        if (submittedRequests > 0) {
            insights.add(new AiInsight("MEDIUM", "PROCUREMENT", "Approval backlog", submittedRequests + " purchase request(s) are waiting for a decision."));
        }

        long approvedOrders = orderList.stream().filter(o -> o.getStatus() == PurchaseOrder.PurchaseOrderStatus.APPROVED).count();
        if (approvedOrders > 0) {
            insights.add(new AiInsight("MEDIUM", "PROCUREMENT", "Purchase orders ready to send", approvedOrders + " approved purchase order(s) have not been sent to vendors."));
        }

        BigDecimal totalExpenses = expenseList.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
            insights.add(new AiInsight("INFO", "FINANCE", "Expense activity", "Recorded expense value is " + totalExpenses + ". Use the finance dashboard to review spending and approvals."));
        }

        if (insights.isEmpty()) {
            insights.add(new AiInsight("INFO", "SYSTEM", "No immediate risks detected", "Current ERP operational data does not trigger any configured high-priority intelligence rules."));
        }
        insights.sort(Comparator.comparingInt(i -> severityRank(i.severity())));
        return new AiAnalysis(insights.size(), insights.stream().filter(i -> "HIGH".equals(i.severity())).count(), insights);
    }

    private int severityRank(String severity) {
        return switch (severity) { case "HIGH" -> 0; case "MEDIUM" -> 1; default -> 2; };
    }

    public record AiAnalysis(int insightCount, long highPriorityCount, List<AiInsight> insights) {}
    public record AiInsight(String severity, String area, String title, String message) {}
}
