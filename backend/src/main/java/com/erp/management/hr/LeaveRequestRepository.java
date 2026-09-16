package com.erp.management.hr;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeIdOrderByStartDateDesc(Long employeeId);
    List<LeaveRequest> findByStatusOrderByStartDateAsc(LeaveRequest.LeaveStatus status);
}
