package com.erp.management.procurement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/procurement/purchase-requests")
@RequiredArgsConstructor
public class PurchaseRequestController {
    private final PurchaseRequestService service;

    @GetMapping
    public List<PurchaseRequest> all() { return service.all(); }
    @GetMapping("/employee/{employeeId}")
    public List<PurchaseRequest> byEmployee(@PathVariable @Positive Long employeeId) { return service.byEmployee(employeeId); }
    @GetMapping("/status/{status}")
    public List<PurchaseRequest> byStatus(@PathVariable PurchaseRequest.PurchaseRequestStatus status) { return service.byStatus(status); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PurchaseRequest create(@Valid @RequestBody PurchaseRequestPayload request) {
        return service.create(request.requesterEmployeeId(), request.vendorId(), request.itemDescription(), request.quantity(), request.estimatedUnitPrice(), request.requestedDate());
    }
    @PatchMapping("/{id}/submit") public PurchaseRequest submit(@PathVariable @Positive Long id) { return service.submit(id); }
    @PatchMapping("/{id}/decision") public PurchaseRequest decide(@PathVariable @Positive Long id, @Valid @RequestBody DecisionRequest request) { return service.decide(id, request.status()); }
    @PatchMapping("/{id}/cancel") public PurchaseRequest cancel(@PathVariable @Positive Long id) { return service.cancel(id); }

    public record PurchaseRequestPayload(@NotNull @Positive Long requesterEmployeeId, @NotNull @Positive Long vendorId,
                                         @NotBlank @Size(max = 300) String itemDescription, @NotNull @DecimalMin("0.001") BigDecimal quantity,
                                         @NotNull @DecimalMin("0.00") BigDecimal estimatedUnitPrice, @NotNull @PastOrPresent LocalDate requestedDate) {}
    public record DecisionRequest(@NotNull PurchaseRequest.PurchaseRequestStatus status) {}
}
