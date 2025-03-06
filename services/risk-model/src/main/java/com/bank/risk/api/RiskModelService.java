package com.bank.risk.api;

import java.util.Map;
import java.util.List;

public interface RiskModelService {
    /**
     * 计算欺诈风险评分
     */
    Map<String, Object> calculateFraudRisk(String customerId, Map<String, Object> applicationData);
    
    /**
     * 训练评分卡模型
     */
    Map<String, Object> trainScorecardModel(List<Map<String, Object>> trainingData);
    
    /**
     * 提取客户图特征
     */
    Map<String, Double> extractGraphFeatures(String customerId);
} 