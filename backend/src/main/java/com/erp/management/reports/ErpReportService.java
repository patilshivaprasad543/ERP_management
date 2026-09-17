package com.erp.management.reports;

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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ErpReportService {
    private final ExpenseRepository expenses;
    private final VendorPayableRepository payables;
    private final PurchaseRequestRepository requests;
    private final PurchaseOrderRepository orders;
    private final InventoryRepository inventory;

    public DashboardReport dashboard() {
        List<Expense> expenseList = expenses.findAll();
        List<VendorPayable> payableList = payables.findAll();
        List<PurchaseRequest> requestList = requests.findAll();
        List<PurchaseOrder> orderList = orders.findAll();
        List<InventoryItem> inventoryList = inventory.findAll();

        BigDecimal expenseTotal = expenseList.stream().map(Expense::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal payableTotal = payableList.stream().filter(p -> p.getStatus() == VendorPayable.PayableStatus.OPEN || p.getStatus() == VendorPayable.PayableStatus.APPROVED)
                .map(VendorPayable::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal inventoryValue = inventoryList.stream().map(i -> i.getQuantityOnHand().multiply(i.getUnitCost())).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<PurchaseRequest.PurchaseRequestStatus, Long> requestStatus = new EnumMap<>(PurchaseRequest.PurchaseRequestStatus.class);
        for (PurchaseRequest.PurchaseRequestStatus status : PurchaseRequest.PurchaseRequestStatus.values()) {
            requestStatus.put(status, requestList.stream().filter(r -> r.getStatus() == status).count());
        }
        Map<PurchaseOrder.PurchaseOrderStatus, Long> orderStatus = new EnumMap<>(PurchaseOrder.PurchaseOrderStatus.class);
        for (PurchaseOrder.PurchaseOrderStatus status : PurchaseOrder.PurchaseOrderStatus.values()) {
            orderStatus.put(status, orderList.stream().filter(o -> o.getStatus() == status).count());
        }
        return new DashboardReport(expenseTotal, payableTotal, inventoryValue, requestList.size(), orderList.size(), inventoryList.size(), requestStatus, orderStatus);
    }

    public record DashboardReport(BigDecimal expenseTotal, BigDecimal openPayableTotal, BigDecimal inventoryValue,
                                   int purchaseRequestCount, int purchaseOrderCount, int inventoryItemCount,
                                   Map<PurchaseRequest.PurchaseRequestStatus, Long> purchaseRequestsByStatus,
                                   Map<PurchaseOrder.PurchaseOrderStatus, Long> purchaseOrdersByStatus) {}
}
