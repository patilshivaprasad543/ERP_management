package com.erp.management.hr;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hr/leave")
@RequiredArgsConstructor
public class LeaveRequestController {
    private final LeaveRequestRepository leaves;

    @GetMapping("/employee/{employeeId}")
    public List<LeaveRequest> employeeLeaves(@PathVariable Long employeeId) {
        return leaves.findByEmployeeIdOrderByStartDateDesc(employeeId);
    }

    @GetMapping("/pending")
    public List<LeaveRequest> pending() {
        return leaves.findByStatusOrderByStartDateAsc(LeaveRequest.LeaveStatus.PENDING);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequest request(@Valid @RequestBody LeaveRequestRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        return leaves.save(LeaveRequest.builder()
                .employeeId(request.employeeId())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .leaveType(request.leaveType())
                .reason(request.reason())
                .status(LeaveRequest.LeaveStatus.PENDING)
                .build());
    }

    @PatchMapping("/{id}/decision")
    public LeaveRequest decide(@PathVariable Long id, @Valid @RequestBody DecisionRequest request) {
        LeaveRequest leave = leaves.findById(id).orElseThrow(() -> new IllegalArgumentException("Leave request not found"));
        if (leave.getStatus() != LeaveRequest.LeaveStatus.PENDING) {
            throw new IllegalArgumentException("Only pending requests can be decided");
        }
        leave.setStatus(request.status());
        return leaves.save(leave);
    }

    public record LeaveRequestRequest(@NotNull Long employeeId, @NotNull LocalDate startDate,
                                      @NotNull LocalDate endDate, @NotNull LeaveRequest.LeaveType leaveType,
                                      @Size(max = 500) String reason) {}
    public record DecisionRequest(@NotNull LeaveRequest.LeaveStatus status) {}
}
