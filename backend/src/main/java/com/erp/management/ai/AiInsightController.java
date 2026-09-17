package com.erp.management.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiInsightController {
    private final AiInsightService service;

    @GetMapping("/insights")
    public AiInsightService.AiAnalysis insights() {
        return service.analyze();
    }
}
