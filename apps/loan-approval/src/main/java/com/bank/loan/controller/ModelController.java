package com.bank.loan.controller;

import com.bank.risk.model.ScorecardModelService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/model")
public class ModelController {

    private final ScorecardModelService modelService;
    
    public ModelController(ScorecardModelService modelService) {
        this.modelService = modelService;
    }
    
    /**
     * 训练模型
     */
    @PostMapping("/train")
    public Map<String, Object> trainModel(@RequestBody List<Map<String, Object>> trainingData) {
        return modelService.trainModel(trainingData);
    }
    
    /**
     * 获取模型信息
     */
    @GetMapping("/info")
    public Map<String, Object> getModelInfo() {
        return modelService.getModelParameters();
    }
    
    /**
     * 获取特征重要性
     */
    @GetMapping("/feature-importance")
    public Map<String, Double> getFeatureImportance() {
        Map<String, Object> params = modelService.getModelParameters();
        Map<String, Double> weights = (Map<String, Double>) params.get("weights");
        
        if (weights == null) {
            return Map.of();
        }
        
        // 计算特征重要性
        double sum = weights.values().stream().mapToDouble(Math::abs).sum();
        return weights.entrySet().stream()
            .collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                e -> Math.abs(e.getValue()) / sum
            ));
    }
} 