package com.erp.management.procurement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/procurement/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {
    private final PurchaseOrderService service;

    @GetMapping
    public List<PurchaseOrder> all() { return service.all(); }

    @GetMapping("/vendor/{vendorId}")
    public List<PurchaseOrder> byVendor(@PathVariable @Positive Long vendorId) { return service.byVendor(vendorId); }

    @GetMapping("/status/{status}")
    public List<PurchaseOrder> byStatus(@PathVariable PurchaseOrder.PurchaseOrderStatus status) { return service.byStatus(status); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseOrder create(@Valid @RequestBody CreateOrderRequest request) {
        return service.create(request.purchaseRequestId(), request.expectedDeliveryDate());
    }

    @PatchMapping("/{id}/approve")
    public PurchaseOrder approve(@PathVariable @Positive Long id) { return service.approve(id); }

    @PatchMapping("/{id}/send")
    public PurchaseOrder send(@PathVariable @Positive Long id) { return service.send(id); }

    @PatchMapping("/{id}/receive")
    public PurchaseOrder receive(@PathVariable @Positive Long id) { return service.receive(id); }

    @PatchMapping("/{id}/cancel")
    public PurchaseOrder cancel(@PathVariable @Positive Long id) { return service.cancel(id); }

    public record CreateOrderRequest(@NotNull @Positive Long purchaseRequestId, LocalDate expectedDeliveryDate) {}
}
