package com.erp.management.hr;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/hr/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService service;

    @GetMapping("/{employeeId}")
    public List<Attendance> history(@PathVariable @Positive Long employeeId) { return service.history(employeeId); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Attendance save(@Valid @RequestBody AttendanceRequest request) { return service.save(request); }

    @PostMapping("/check-in/{employeeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Attendance checkIn(@PathVariable @Positive Long employeeId) { return service.checkIn(employeeId); }

    @PostMapping("/check-out/{employeeId}")
    public Attendance checkOut(@PathVariable @Positive Long employeeId) { return service.checkOut(employeeId); }

    public record AttendanceRequest(@NotNull @Positive Long employeeId, @NotNull @PastOrPresent LocalDate attendanceDate,
                                    LocalTime checkIn, LocalTime checkOut, @NotNull Attendance.Status status) {}
}
