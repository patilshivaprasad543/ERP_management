package com.erp.management.procurement;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procurement/vendors")
@RequiredArgsConstructor
public class VendorController {
    private final VendorService service;

    @GetMapping
    public List<Vendor> all() { return service.all(); }

    @GetMapping("/status/{status}")
    public List<Vendor> byStatus(@PathVariable Vendor.VendorStatus status) { return service.byStatus(status); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vendor create(@Valid @RequestBody VendorRequest request) {
        return service.create(request.vendorCode(), request.name(), request.email(), request.phone(), request.address());
    }

    @PatchMapping("/{id}/activate")
    public Vendor activate(@PathVariable @Positive Long id) { return service.activate(id); }

    @PatchMapping("/{id}/suspend")
    public Vendor suspend(@PathVariable @Positive Long id) { return service.suspend(id); }

    @PatchMapping("/{id}/deactivate")
    public Vendor deactivate(@PathVariable @Positive Long id) { return service.deactivate(id); }

    public record VendorRequest(@NotBlank @Size(max = 30) String vendorCode,
                                 @NotBlank @Size(max = 160) String name,
                                 @Email @Size(max = 160) String email,
                                 @Size(max = 25) String phone,
                                 @Size(max = 500) String address) {}
}
