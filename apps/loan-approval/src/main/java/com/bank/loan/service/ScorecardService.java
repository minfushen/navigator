package com.bank.loan.service;

import com.bank.risk.feature.GraphFeatureExtractor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ScorecardService {

    private final GraphFeatureExtractor featureExtractor;
    
    public ScorecardService(GraphFeatureExtractor featureExtractor) {
        this.featureExtractor = featureExtractor;
    }
    
    /**
     * 计算申请人的反欺诈评分
     * @param customerId 客户ID
     * @param applicationData 申请数据
     * @return 评分结果
     */
    public Map<String, Object> calculateFraudScore(String customerId, Map<String, Object> applicationData) {
        // 1. 提取传统特征
        Map<String, Double> traditionalFeatures = extractTraditionalFeatures(applicationData);
        
        // 2. 提取图特征
        Map<String, Double> graphFeatures = featureExtractor.extractFeatures(customerId);
        
        // 3. 特征合并
        Map<String, Double> allFeatures = new HashMap<>();
        allFeatures.putAll(traditionalFeatures);
        allFeatures.putAll(graphFeatures);
        
        // 4. 模型评分
        double fraudScore = predictFraudScore(allFeatures);
        double creditScore = predictCreditScore(allFeatures);
        
        // 5. 决策规则
        String decision = makeDecision(fraudScore, creditScore);
        List<String> reasons = generateReasons(allFeatures, fraudScore);
        
        // 6. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("customerId", customerId);
        result.put("fraudScore", fraudScore);
        result.put("creditScore", creditScore);
        result.put("decision", decision);
        result.put("reasons", reasons);
        result.put("communityId", graphFeatures.get("community_id"));
        result.put("riskLevel", calculateRiskLevel(fraudScore));
        
        return result;
    }
    
    /**
     * 提取传统特征
     */
    private Map<String, Double> extractTraditionalFeatures(Map<String, Object> applicationData) {
        // 实现传统特征提取
        Map<String, Double> features = new HashMap<>();
        features.put("age", Double.parseDouble(applicationData.get("age").toString()));
        features.put("income", Double.parseDouble(applicationData.get("income").toString()));
        features.put("credit_history", Double.parseDouble(applicationData.get("creditHistory").toString()));
        // 更多特征...
        return features;
    }
    
    /**
     * 预测欺诈评分
     */
    private double predictFraudScore(Map<String, Double> features) {
        // 实现欺诈评分模型
        // 这里简化为线性模型，实际应使用XGBoost等
        double score = 600;
        
        // 图特征权重
        score -= features.getOrDefault("community_risk_ratio", 0.0) * 100;
        score -= features.getOrDefault("risk_exposure", 0.0) * 80;
        
        // 传统特征权重
        score += features.getOrDefault("credit_history", 0.0) * 50;
        score += features.getOrDefault("income", 0.0) * 0.01;
        
        return Math.max(300, Math.min(900, score));
    }
    
    /**
     * 预测信用评分
     */
    private double predictCreditScore(Map<String, Double> features) {
        // 实现信用评分模型
        return 720.0; // 简化实现
    }
    
    /**
     * 做出贷款决策
     */
    private String makeDecision(double fraudScore, double creditScore) {
        if (fraudScore < 600) {
            return "REJECT";
        } else if (fraudScore < 700 && creditScore < 650) {
            return "MANUAL_REVIEW";
        } else {
            return "APPROVE";
        }
    }
    
    /**
     * 生成拒绝原因
     */
    private List<String> generateReasons(Map<String, Double> features, double fraudScore) {
        List<String> reasons = new ArrayList<>();
        
        if (features.getOrDefault("community_risk_ratio", 0.0) > 0.3) {
            reasons.add("申请人所在社区存在高风险");
        }
        
        if (features.getOrDefault("risk_exposure", 0.0) > 0.4) {
            reasons.add("申请人风险暴露度高");
        }
        
        if (features.getOrDefault("community_growth_rate", 0.0) > 0.5) {
            reasons.add("申请人所在社区增长异常");
        }
        
        return reasons;
    }
    
    /**
     * 计算风险等级
     */
    private String calculateRiskLevel(double fraudScore) {
        if (fraudScore < 500) return "高风险";
        if (fraudScore < 700) return "中风险";
        return "低风险";
    }
} 