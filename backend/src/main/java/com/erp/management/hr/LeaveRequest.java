package com.erp.management.hr;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveRequest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long employeeId;
    @Column(nullable = false) private LocalDate startDate;
    @Column(nullable = false) private LocalDate endDate;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private LeaveType leaveType;
    @Column(length = 500) private String reason;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20) private LeaveStatus status;

    public enum LeaveType { CASUAL, SICK, EARNED, UNPAID, MATERNITY, PATERNITY }
    public enum LeaveStatus { PENDING, APPROVED, REJECTED, CANCELLED }
}
