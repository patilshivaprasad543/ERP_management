package com.erp.management.procurement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseRequestService {
    private final PurchaseRequestRepository requests;
    private final VendorRepository vendors;

    public List<PurchaseRequest> all() { return requests.findAll(); }

    public List<PurchaseRequest> byEmployee(Long employeeId) {
        return requests.findByRequesterEmployeeIdOrderByRequestedDateDesc(employeeId);
    }

    public List<PurchaseRequest> byStatus(PurchaseRequest.PurchaseRequestStatus status) {
        return requests.findByStatusOrderByRequestedDateAsc(status);
    }

    @Transactional
    public PurchaseRequest create(Long employeeId, Long vendorId, String itemDescription,
                                  BigDecimal quantity, BigDecimal estimatedUnitPrice,
                                  LocalDate requestedDate) {
        if (quantity.signum() <= 0) throw new IllegalArgumentException("Quantity must be greater than zero");
        if (estimatedUnitPrice.signum() < 0) throw new IllegalArgumentException("Estimated unit price cannot be negative");
        Vendor vendor = vendors.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
        if (vendor.getStatus() != Vendor.VendorStatus.ACTIVE) {
            throw new IllegalArgumentException("Purchase requests require an active vendor");
        }
        return requests.save(PurchaseRequest.builder()
                .requesterEmployeeId(employeeId)
                .vendorId(vendorId)
                .itemDescription(itemDescription.trim())
                .quantity(quantity)
                .estimatedUnitPrice(estimatedUnitPrice)
                .requestedDate(requestedDate)
                .status(PurchaseRequest.PurchaseRequestStatus.DRAFT)
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Transactional
    public PurchaseRequest submit(Long id) { return transition(id, PurchaseRequest.PurchaseRequestStatus.SUBMITTED); }

    @Transactional
    public PurchaseRequest decide(Long id, PurchaseRequest.PurchaseRequestStatus target) {
        if (target != PurchaseRequest.PurchaseRequestStatus.APPROVED && target != PurchaseRequest.PurchaseRequestStatus.REJECTED) {
            throw new IllegalArgumentException("Decision must be APPROVED or REJECTED");
        }
        return transition(id, target);
    }

    @Transactional
    public PurchaseRequest cancel(Long id) { return transition(id, PurchaseRequest.PurchaseRequestStatus.CANCELLED); }

    private PurchaseRequest transition(Long id, PurchaseRequest.PurchaseRequestStatus target) {
        PurchaseRequest request = requests.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase request not found"));
        PurchaseRequest.PurchaseRequestStatus current = request.getStatus();
        boolean allowed = switch (current) {
            case DRAFT -> target == PurchaseRequest.PurchaseRequestStatus.SUBMITTED || target == PurchaseRequest.PurchaseRequestStatus.CANCELLED;
            case SUBMITTED -> target == PurchaseRequest.PurchaseRequestStatus.APPROVED
                    || target == PurchaseRequest.PurchaseRequestStatus.REJECTED
                    || target == PurchaseRequest.PurchaseRequestStatus.CANCELLED;
            case APPROVED, REJECTED, CANCELLED -> false;
        };
        if (!allowed) throw new IllegalArgumentException("Invalid purchase request status transition: " + current + " -> " + target);
        request.setStatus(target);
        return requests.save(request);
    }
}
