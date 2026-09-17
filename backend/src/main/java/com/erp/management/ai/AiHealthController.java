package com.erp.management.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiHealthController {
    private final AiInsightService insightService;

    @GetMapping("/status")
    public Map<String, Object> status() {
        AiInsightService.AiAnalysis analysis = insightService.analyze();
        return Map.of("enabled", true, "engine", "ERP rule intelligence", "generatedAt", Instant.now(), "insightCount", analysis.insightCount(), "highPriorityCount", analysis.highPriorityCount());
    }
}
