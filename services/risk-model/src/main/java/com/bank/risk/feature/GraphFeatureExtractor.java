package com.bank.risk.feature;

import com.bank.graph.algorithm.LouvainCommunityDetection;
import org.apache.spark.graphx.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class GraphFeatureExtractor {

    private final LouvainCommunityDetection communityDetection;
    
    public GraphFeatureExtractor(LouvainCommunityDetection communityDetection) {
        this.communityDetection = communityDetection;
    }
    
    /**
     * 提取申请人的图特征
     * @param customerId 客户ID
     * @return 图特征Map
     */
    public Map<String, Double> extractFeatures(String customerId) {
        Map<String, Double> features = new HashMap<>();
        
        // 1. 社区特征
        Map<String, Object> communityFeatures = extractCommunityFeatures(customerId);
        features.put("community_size", (Double) communityFeatures.get("size"));
        features.put("community_density", (Double) communityFeatures.get("density"));
        features.put("community_risk_ratio", (Double) communityFeatures.get("riskRatio"));
        
        // 2. 中心性特征
        Map<String, Double> centralityFeatures = extractCentralityFeatures(customerId);
        features.put("degree_centrality", centralityFeatures.get("degree"));
        features.put("betweenness_centrality", centralityFeatures.get("betweenness"));
        features.put("closeness_centrality", centralityFeatures.get("closeness"));
        
        // 3. 风险传导特征
        Map<String, Double> riskPropagationFeatures = extractRiskPropagationFeatures(customerId);
        features.put("risk_exposure", riskPropagationFeatures.get("exposure"));
        features.put("risk_influence", riskPropagationFeatures.get("influence"));
        
        // 4. 时序特征
        Map<String, Double> temporalFeatures = extractTemporalFeatures(customerId);
        features.put("community_growth_rate", temporalFeatures.get("growthRate"));
        features.put("activity_frequency", temporalFeatures.get("frequency"));
        
        return features;
    }
    
    /**
     * 提取社区特征
     */
    private Map<String, Object> extractCommunityFeatures(String customerId) {
        // 实现社区特征提取
        Map<String, Object> features = new HashMap<>();
        features.put("size", 15.0);
        features.put("density", 0.75);
        features.put("riskRatio", 0.23);
        return features;
    }
    
    /**
     * 提取中心性特征
     */
    private Map<String, Double> extractCentralityFeatures(String customerId) {
        // 实现中心性特征提取
        Map<String, Double> features = new HashMap<>();
        features.put("degree", 8.0);
        features.put("betweenness", 0.45);
        features.put("closeness", 0.67);
        return features;
    }
    
    /**
     * 提取风险传导特征
     */
    private Map<String, Double> extractRiskPropagationFeatures(String customerId) {
        // 实现风险传导特征提取
        Map<String, Double> features = new HashMap<>();
        features.put("exposure", 0.32);
        features.put("influence", 0.28);
        return features;
    }
    
    /**
     * 提取时序特征
     */
    private Map<String, Double> extractTemporalFeatures(String customerId) {
        // 实现时序特征提取
        Map<String, Double> features = new HashMap<>();
        features.put("growthRate", 0.15);
        features.put("frequency", 3.5);
        return features;
    }
} 