package com.erp.management.finance;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/finance/payables")
@RequiredArgsConstructor
public class VendorPayableController {
    private final VendorPayableService service;

    @GetMapping
    public List<VendorPayable> all() { return service.all(); }

    @GetMapping("/status/{status}")
    public List<VendorPayable> byStatus(@PathVariable VendorPayable.PayableStatus status) {
        return service.byStatus(status);
    }

    @PostMapping("/from-receipt")
    @ResponseStatus(HttpStatus.CREATED)
    public VendorPayable createFromReceipt(@Valid @RequestBody ReceiptRequest request) {
        return service.createForReceivedOrder(request.purchaseOrderId());
    }

    @PatchMapping("/{id}/approve")
    public VendorPayable approve(@PathVariable Long id) { return service.approve(id); }

    @PatchMapping("/{id}/pay")
    public VendorPayable pay(@PathVariable Long id) { return service.pay(id); }

    @PatchMapping("/{id}/cancel")
    public VendorPayable cancel(@PathVariable Long id) { return service.cancel(id); }

    public record ReceiptRequest(@NotNull Long purchaseOrderId) {}
}
