package com.erp.management.hr;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hr/leave")
@RequiredArgsConstructor
public class LeaveRequestController {
    private final LeaveRequestService service;

    @GetMapping("/employee/{employeeId}")
    public List<LeaveRequest> employeeLeaves(@PathVariable Long employeeId) {
        return service.employeeLeaves(employeeId);
    }

    @GetMapping("/pending")
    public List<LeaveRequest> pending() {
        return service.pending();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequest request(@Valid @RequestBody LeaveRequestRequest request) {
        return service.request(request.employeeId(), request.startDate(), request.endDate(), request.leaveType(), request.reason());
    }

    @PatchMapping("/{id}/decision")
    public LeaveRequest decide(@PathVariable Long id, @Valid @RequestBody DecisionRequest request) {
        return service.decide(id, request.status());
    }

    @PatchMapping("/{id}/cancel")
    public LeaveRequest cancel(@PathVariable Long id) {
        return service.cancel(id);
    }

    public record LeaveRequestRequest(@NotNull Long employeeId, @NotNull LocalDate startDate,
                                      @NotNull LocalDate endDate, @NotNull LeaveRequest.LeaveType leaveType,
                                      @Size(max = 500) String reason) {}
    public record DecisionRequest(@NotNull LeaveRequest.LeaveStatus status) {}
}
