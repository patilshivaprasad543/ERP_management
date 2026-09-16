package com.erp.management.procurement;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseRequestRepository extends JpaRepository<PurchaseRequest, Long> {
    List<PurchaseRequest> findByRequesterEmployeeIdOrderByRequestedDateDesc(Long requesterEmployeeId);
    List<PurchaseRequest> findByStatusOrderByRequestedDateAsc(PurchaseRequest.PurchaseRequestStatus status);
}
