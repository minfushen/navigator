package com.bank.loan.controller;

import com.bank.loan.service.ScorecardService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1/fraud")
public class FraudDetectionController {

    private final ScorecardService scorecardService;
    
    public FraudDetectionController(ScorecardService scorecardService) {
        this.scorecardService = scorecardService;
    }
    
    /**
     * 贷款申请反欺诈评估
     */
    @PostMapping("/evaluate")
    public Map<String, Object> evaluateFraudRisk(@RequestBody Map<String, Object> request) {
        String customerId = (String) request.get("customerId");
        Map<String, Object> applicationData = (Map<String, Object>) request.get("applicationData");
        
        return scorecardService.calculateFraudScore(customerId, applicationData);
    }
    
    /**
     * 获取客户风险网络
     */
    @GetMapping("/network/{customerId}")
    public Map<String, Object> getCustomerRiskNetwork(@PathVariable String customerId) {
        // 实现获取客户风险网络
        return Map.of(
            "customerId", customerId,
            "networkSize", 15,
            "riskConnections", List.of("10001", "10086", "20023"),
            "communityId", "C10023"
        );
    }
    
    /**
     * 批量风险评估
     */
    @PostMapping("/batch-evaluate")
    public List<Map<String, Object>> batchEvaluate(@RequestBody List<Map<String, Object>> requests) {
        List<Map<String, Object>> results = new ArrayList<>();
        
        for (Map<String, Object> request : requests) {
            String customerId = (String) request.get("customerId");
            Map<String, Object> applicationData = (Map<String, Object>) request.get("applicationData");
            results.add(scorecardService.calculateFraudScore(customerId, applicationData));
        }
        
        return results;
    }
} 