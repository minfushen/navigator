package com.bank.risk.model;

import com.bank.risk.feature.GraphFeatureExtractor;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScorecardModelService {

    private final GraphFeatureExtractor featureExtractor;
    private Map<String, Object> modelParameters;
    private static final String MODEL_PATH = "models/scorecard_model.ser";
    
    public ScorecardModelService(GraphFeatureExtractor featureExtractor) {
        this.featureExtractor = featureExtractor;
        this.modelParameters = loadModel();
    }
    
    /**
     * 训练评分卡模型
     * @param trainingData 训练数据
     * @return 训练结果
     */
    public Map<String, Object> trainModel(List<Map<String, Object>> trainingData) {
        // 1. 提取特征
        List<Map<String, Double>> featuresList = extractFeatures(trainingData);
        
        // 2. 划分训练集和测试集
        Map<String, List<Map<String, Double>>> datasets = splitDataset(featuresList, 0.8);
        List<Map<String, Double>> trainSet = datasets.get("train");
        List<Map<String, Double>> testSet = datasets.get("test");
        
        // 3. 训练模型（这里简化为权重计算）
        Map<String, Double> weights = calculateWeights(trainSet);
        
        // 4. 评估模型
        Map<String, Double> metrics = evaluateModel(weights, testSet);
        
        // 5. 保存模型
        modelParameters = new HashMap<>();
        modelParameters.put("weights", weights);
        modelParameters.put("metrics", metrics);
        modelParameters.put("timestamp", System.currentTimeMillis());
        saveModel(modelParameters);
        
        // 6. 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("modelId", UUID.randomUUID().toString());
        result.put("metrics", metrics);
        result.put("featureImportance", calculateFeatureImportance(weights));
        
        return result;
    }
    
    /**
     * 提取特征
     */
    private List<Map<String, Double>> extractFeatures(List<Map<String, Object>> data) {
        return data.stream().map(record -> {
            String customerId = (String) record.get("customerId");
            Map<String, Object> applicationData = (Map<String, Object>) record.get("applicationData");
            boolean isFraud = (boolean) record.get("isFraud");
            
            // 提取传统特征
            Map<String, Double> features = new HashMap<>();
            features.put("age", Double.parseDouble(applicationData.get("age").toString()));
            features.put("income", Double.parseDouble(applicationData.get("income").toString()));
            features.put("credit_history", Double.parseDouble(applicationData.get("creditHistory").toString()));
            
            // 提取图特征
            Map<String, Double> graphFeatures = featureExtractor.extractFeatures(customerId);
            features.putAll(graphFeatures);
            
            // 添加标签
            features.put("isFraud", isFraud ? 1.0 : 0.0);
            
            return features;
        }).collect(Collectors.toList());
    }
    
    /**
     * 划分数据集
     */
    private Map<String, List<Map<String, Double>>> splitDataset(List<Map<String, Double>> data, double trainRatio) {
        Collections.shuffle(data);
        int trainSize = (int) (data.size() * trainRatio);
        
        Map<String, List<Map<String, Double>>> result = new HashMap<>();
        result.put("train", data.subList(0, trainSize));
        result.put("test", data.subList(trainSize, data.size()));
        
        return result;
    }
    
    /**
     * 计算模型权重
     */
    private Map<String, Double> calculateWeights(List<Map<String, Double>> trainSet) {
        // 简化的逻辑回归实现
        Map<String, Double> weights = new HashMap<>();
        
        // 初始化权重
        Set<String> features = trainSet.get(0).keySet();
        features.remove("isFraud");
        for (String feature : features) {
            weights.put(feature, 0.0);
        }
        
        // 梯度下降训练
        double learningRate = 0.01;
        int iterations = 100;
        
        for (int i = 0; i < iterations; i++) {
            Map<String, Double> gradients = new HashMap<>();
            for (String feature : features) {
                gradients.put(feature, 0.0);
            }
            
            for (Map<String, Double> sample : trainSet) {
                double y = sample.get("isFraud");
                double prediction = predict(weights, sample);
                double error = prediction - y;
                
                for (String feature : features) {
                    double value = sample.getOrDefault(feature, 0.0);
                    gradients.put(feature, gradients.get(feature) + error * value);
                }
            }
            
            // 更新权重
            for (String feature : features) {
                double gradient = gradients.get(feature) / trainSet.size();
                weights.put(feature, weights.get(feature) - learningRate * gradient);
            }
        }
        
        return weights;
    }
    
    /**
     * 预测函数
     */
    private double predict(Map<String, Double> weights, Map<String, Double> features) {
        double z = 0.0;
        for (Map.Entry<String, Double> entry : weights.entrySet()) {
            String feature = entry.getKey();
            double weight = entry.getValue();
            double value = features.getOrDefault(feature, 0.0);
            z += weight * value;
        }
        
        // Sigmoid函数
        return 1.0 / (1.0 + Math.exp(-z));
    }
    
    /**
     * 评估模型
     */
    private Map<String, Double> evaluateModel(Map<String, Double> weights, List<Map<String, Double>> testSet) {
        int tp = 0, fp = 0, tn = 0, fn = 0;
        double threshold = 0.5;
        
        for (Map<String, Double> sample : testSet) {
            double y = sample.get("isFraud");
            double prediction = predict(weights, sample);
            boolean predictedPositive = prediction >= threshold;
            boolean actualPositive = y >= 0.5;
            
            if (predictedPositive && actualPositive) tp++;
            else if (predictedPositive && !actualPositive) fp++;
            else if (!predictedPositive && !actualPositive) tn++;
            else fn++;
        }
        
        double accuracy = (double) (tp + tn) / testSet.size();
        double precision = (double) tp / (tp + fp);
        double recall = (double) tp / (tp + fn);
        double f1 = 2 * precision * recall / (precision + recall);
        
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("accuracy", accuracy);
        metrics.put("precision", precision);
        metrics.put("recall", recall);
        metrics.put("f1", f1);
        
        return metrics;
    }
    
    /**
     * 计算特征重要性
     */
    private Map<String, Double> calculateFeatureImportance(Map<String, Double> weights) {
        Map<String, Double> importance = new HashMap<>();
        double sum = weights.values().stream().mapToDouble(Math::abs).sum();
        
        for (Map.Entry<String, Double> entry : weights.entrySet()) {
            importance.put(entry.getKey(), Math.abs(entry.getValue()) / sum);
        }
        
        return importance;
    }
    
    /**
     * 保存模型
     */
    private void saveModel(Map<String, Object> model) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MODEL_PATH))) {
            oos.writeObject(model);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 加载模型
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> loadModel() {
        File file = new File(MODEL_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<String, Object>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
    /**
     * 获取模型参数
     */
    public Map<String, Object> getModelParameters() {
        return modelParameters;
    }
} 