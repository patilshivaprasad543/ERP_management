package com.erp.management.hr;

import com.erp.management.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendance;
    private final EmployeeRepository employees;

    public List<Attendance> history(Long employeeId) { return attendance.findByEmployeeIdOrderByAttendanceDateDesc(employeeId); }

    @Transactional
    public Attendance save(AttendanceController.AttendanceRequest request) {
        requireEmployee(request.employeeId());
        if (request.attendanceDate().isAfter(LocalDate.now())) throw new IllegalArgumentException("Attendance date cannot be in the future");
        if (attendance.findByEmployeeIdAndAttendanceDate(request.employeeId(), request.attendanceDate()).isPresent()) throw new IllegalArgumentException("Attendance already exists for this employee and date");
        validateTimes(request.checkIn(), request.checkOut());
        return attendance.save(Attendance.builder().employeeId(request.employeeId()).attendanceDate(request.attendanceDate()).checkIn(request.checkIn()).checkOut(request.checkOut()).status(request.status()).build());
    }

    @Transactional
    public Attendance checkIn(Long employeeId) {
        requireEmployee(employeeId);
        LocalDate today = LocalDate.now();
        if (attendance.findByEmployeeIdAndAttendanceDate(employeeId, today).isPresent()) throw new IllegalArgumentException("Today's attendance already exists");
        return attendance.save(Attendance.builder().employeeId(employeeId).attendanceDate(today).checkIn(LocalTime.now()).status(Attendance.Status.PRESENT).build());
    }

    @Transactional
    public Attendance checkOut(Long employeeId) {
        requireEmployee(employeeId);
        Attendance record = attendance.findByEmployeeIdAndAttendanceDate(employeeId, LocalDate.now()).orElseThrow(() -> new IllegalArgumentException("Check-in is required before check-out"));
        if (record.getCheckIn() == null) throw new IllegalArgumentException("Check-in is required before check-out");
        if (record.getCheckOut() != null) throw new IllegalArgumentException("Today's attendance is already checked out");
        LocalTime now = LocalTime.now();
        if (!now.isAfter(record.getCheckIn())) throw new IllegalArgumentException("Check-out time must be after check-in time");
        record.setCheckOut(now); return attendance.save(record);
    }

    private void requireEmployee(Long id) { if (!employees.existsById(id)) throw new IllegalArgumentException("Employee not found"); }
    private void validateTimes(LocalTime checkIn, LocalTime checkOut) { if (checkIn != null && checkOut != null && !checkOut.isAfter(checkIn)) throw new IllegalArgumentException("Check-out time must be after check-in time"); }
}
