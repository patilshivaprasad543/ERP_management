package com.erp.management.inventory;

import com.erp.management.finance.VendorPayableService;
import com.erp.management.procurement.PurchaseOrder;
import com.erp.management.procurement.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventory;
    private final PurchaseOrderRepository orders;
    private final VendorPayableService payables;

    public List<InventoryItem> all() { return inventory.findAll(); }

    @Transactional
    public InventoryItem create(String sku, String name, String unit, BigDecimal quantity, BigDecimal unitCost) {
        if (inventory.findBySkuIgnoreCase(sku).isPresent()) throw new IllegalArgumentException("SKU already exists");
        if (quantity.signum() < 0 || unitCost.signum() < 0) throw new IllegalArgumentException("Inventory values cannot be negative");
        return inventory.save(InventoryItem.builder().sku(sku.trim()).name(name.trim()).unit(unit)
                .quantityOnHand(quantity).unitCost(unitCost).status(InventoryItem.InventoryStatus.ACTIVE).build());
    }

    @Transactional
    public InventoryItem receivePurchaseOrder(Long purchaseOrderId, String sku, String name, String unit,
                                               BigDecimal quantity, BigDecimal unitCost) {
        if (quantity.signum() <= 0) throw new IllegalArgumentException("Received quantity must be greater than zero");
        if (unitCost.signum() < 0) throw new IllegalArgumentException("Unit cost cannot be negative");
        PurchaseOrder order = orders.findById(purchaseOrderId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase order not found"));
        if (order.getStatus() != PurchaseOrder.PurchaseOrderStatus.SENT) {
            throw new IllegalArgumentException("Only sent purchase orders can be received");
        }
        InventoryItem item = inventory.findBySkuIgnoreCase(sku).orElse(null);
        if (item == null) {
            item = InventoryItem.builder().sku(sku.trim()).name(name.trim()).unit(unit)
                    .quantityOnHand(quantity).unitCost(unitCost).status(InventoryItem.InventoryStatus.ACTIVE).build();
        } else {
            if (item.getStatus() != InventoryItem.InventoryStatus.ACTIVE) throw new IllegalArgumentException("Inventory item is inactive");
            BigDecimal oldQty = item.getQuantityOnHand();
            BigDecimal newQty = oldQty.add(quantity);
            BigDecimal weightedCost = oldQty.signum() == 0 ? unitCost
                    : oldQty.multiply(item.getUnitCost()).add(quantity.multiply(unitCost)).divide(newQty, 2, java.math.RoundingMode.HALF_UP);
            item.setQuantityOnHand(newQty);
            item.setUnitCost(weightedCost);
        }
        order.setStatus(PurchaseOrder.PurchaseOrderStatus.RECEIVED);
        orders.save(order);
        InventoryItem saved = inventory.save(item);
        payables.createForReceivedOrder(order.getId());
        return saved;
    }
}
