package com.erp.management.hr;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/hr/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceRepository attendance;

    @GetMapping("/{employeeId}")
    public List<Attendance> history(@PathVariable Long employeeId) {
        return attendance.findByEmployeeIdOrderByAttendanceDateDesc(employeeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Attendance save(@Valid @RequestBody AttendanceRequest request) {
        if (attendance.findByEmployeeIdAndAttendanceDate(request.employeeId(), request.attendanceDate()).isPresent()) {
            throw new IllegalArgumentException("Attendance already exists for this employee and date");
        }
        return attendance.save(Attendance.builder()
                .employeeId(request.employeeId())
                .attendanceDate(request.attendanceDate())
                .checkIn(request.checkIn())
                .checkOut(request.checkOut())
                .status(request.status())
                .build());
    }

    public record AttendanceRequest(@NotNull Long employeeId, @NotNull LocalDate attendanceDate,
                                    java.time.LocalTime checkIn, java.time.LocalTime checkOut,
                                    @NotNull Attendance.Status status) {}
}
