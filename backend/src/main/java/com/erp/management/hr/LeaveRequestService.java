package com.erp.management.hr;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {
    private final LeaveRequestRepository leaves;

    public List<LeaveRequest> employeeLeaves(Long employeeId) {
        return leaves.findByEmployeeIdOrderByStartDateDesc(employeeId);
    }

    public List<LeaveRequest> pending() {
        return leaves.findByStatusOrderByStartDateAsc(LeaveRequest.LeaveStatus.PENDING);
    }

    @Transactional
    public LeaveRequest request(Long employeeId, LocalDate startDate, LocalDate endDate,
                                LeaveRequest.LeaveType leaveType, String reason) {
        validateDates(startDate, endDate);
        if (leaves.existsByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employeeId, LeaveRequest.LeaveStatus.APPROVED, endDate, startDate)) {
            throw new IllegalArgumentException("Leave dates overlap an approved leave");
        }
        if (leaves.existsByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                employeeId, LeaveRequest.LeaveStatus.PENDING, endDate, startDate)) {
            throw new IllegalArgumentException("Leave dates overlap a pending leave request");
        }
        return leaves.save(LeaveRequest.builder()
                .employeeId(employeeId)
                .startDate(startDate)
                .endDate(endDate)
                .leaveType(leaveType)
                .reason(reason)
                .status(LeaveRequest.LeaveStatus.PENDING)
                .build());
    }

    @Transactional
    public LeaveRequest decide(Long id, LeaveRequest.LeaveStatus status) {
        if (status != LeaveRequest.LeaveStatus.APPROVED && status != LeaveRequest.LeaveStatus.REJECTED) {
            throw new IllegalArgumentException("Decision must be APPROVED or REJECTED");
        }
        LeaveRequest leave = leaves.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
        if (leave.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Only pending requests can be decided");
        }
        if (status == LeaveRequest.LeaveStatus.APPROVED && leaves.existsByEmployeeIdAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                leave.getEmployeeId(), LeaveRequest.LeaveStatus.APPROVED, leave.getEndDate(), leave.getStartDate())) {
            throw new IllegalArgumentException("Leave dates overlap an approved leave");
        }
        leave.setStatus(status);
        return leaves.save(leave);
    }

    @Transactional
    public LeaveRequest cancel(Long id) {
        LeaveRequest leave = leaves.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
        if (leave.getStatus() != LeaveRequest.LeaveStatus.PENDING && leave.getStatus() != LeaveRequest.LeaveStatus.APPROVED) {
            throw new IllegalArgumentException("Only pending or approved leave can be cancelled");
        }
        leave.setStatus(LeaveRequest.LeaveStatus.CANCELLED);
        return leaves.save(leave);
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }
}
