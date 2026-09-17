package com.erp.management.reports;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ErpReportController {
    private final ErpReportService service;

    @GetMapping("/dashboard")
    public ErpReportService.DashboardReport dashboard() {
        return service.dashboard();
    }
}
