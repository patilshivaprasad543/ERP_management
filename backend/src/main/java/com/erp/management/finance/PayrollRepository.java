package com.erp.management.finance;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<PayrollRecord, Long> {
    Optional<PayrollRecord> findByEmployeeIdAndPayrollMonth(Long employeeId, String payrollMonth);
    List<PayrollRecord> findByPayrollMonthOrderByEmployeeIdAsc(String payrollMonth);
}
