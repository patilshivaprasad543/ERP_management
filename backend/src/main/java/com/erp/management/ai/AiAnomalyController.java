package com.erp.management.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ai/anomalies")
@RequiredArgsConstructor
public class AiAnomalyController {
    private final AiAnomalyService service;

    @GetMapping("/expenses")
    public List<AiAnomalyService.ExpenseAnomaly> expenseAnomalies() {
        return service.detect();
    }
}
