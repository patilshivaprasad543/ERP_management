package com.erp.management.inventory;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService service;

    @GetMapping
    public List<InventoryItem> all() { return service.all(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryItem create(@Valid @RequestBody InventoryRequest request) {
        return service.create(request.sku(), request.name(), request.unit(), request.quantity(), request.unitCost());
    }

    @PostMapping("/receive")
    public InventoryItem receive(@Valid @RequestBody ReceiveRequest request) {
        return service.receivePurchaseOrder(request.purchaseOrderId(), request.sku(), request.name(), request.unit(), request.quantity(), request.unitCost());
    }

    public record InventoryRequest(@NotBlank @Size(max = 40) String sku,
                                   @NotBlank @Size(max = 160) String name,
                                   @Size(max = 80) String unit,
                                   @NotNull @DecimalMin("0.00") BigDecimal quantity,
                                   @NotNull @DecimalMin("0.00") BigDecimal unitCost) {}

    public record ReceiveRequest(@NotNull @Positive Long purchaseOrderId,
                                 @NotBlank @Size(max = 40) String sku,
                                 @NotBlank @Size(max = 160) String name,
                                 @Size(max = 80) String unit,
                                 @NotNull @DecimalMin("0.001") BigDecimal quantity,
                                 @NotNull @DecimalMin("0.00") BigDecimal unitCost) {}
}
