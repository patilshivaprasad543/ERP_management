package com.erp.management.hr;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "attendance", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "attendance_date"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attendance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "employee_id", nullable = false) private Long employeeId;
    @Column(name = "attendance_date", nullable = false) private LocalDate attendanceDate;
    private LocalTime checkIn;
    private LocalTime checkOut;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    private Status status;

    public enum Status { PRESENT, ABSENT, HALF_DAY, WORK_FROM_HOME, ON_LEAVE }
}
