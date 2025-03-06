package com.bank.risk.data;

import org.neo4j.driver.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DataLoaderService {

    private final Driver neo4jDriver;
    
    public DataLoaderService(Driver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }
    
    /**
     * 加载客户关系数据
     * @param customerId 客户ID
     * @return 关系数据
     */
    public Map<String, Object> loadCustomerRelationships(String customerId) {
        Map<String, Object> result = new HashMap<>();
        
        try (Session session = neo4jDriver.session()) {
            // 查询客户直接关系
            Result directRelations = session.run(
                "MATCH (c:Customer {id: $customerId})-[r]-(other:Customer) " +
                "RETURN type(r) AS relationType, other.id AS otherId, other.riskScore AS riskScore",
                Values.parameters("customerId", customerId)
            );
            
            List<Map<String, Object>> relations = new ArrayList<>();
            directRelations.forEachRemaining(record -> {
                Map<String, Object> relation = new HashMap<>();
                relation.put("relationType", record.get("relationType").asString());
                relation.put("otherId", record.get("otherId").asString());
                relation.put("riskScore", record.get("riskScore").asDouble());
                relations.add(relation);
            });
            
            result.put("directRelations", relations);
            
            // 查询客户所在社区
            Result community = session.run(
                "MATCH (c:Customer {id: $customerId})-[:BELONGS_TO]->(community:Community) " +
                "RETURN community.id AS communityId, community.size AS size, " +
                "community.riskScore AS riskScore",
                Values.parameters("customerId", customerId)
            );
            
            if (community.hasNext()) {
                Record record = community.next();
                Map<String, Object> communityInfo = new HashMap<>();
                communityInfo.put("communityId", record.get("communityId").asString());
                communityInfo.put("size", record.get("size").asInt());
                communityInfo.put("riskScore", record.get("riskScore").asDouble());
                result.put("community", communityInfo);
            }
        }
        
        return result;
    }
    
    /**
     * 加载社区风险数据
     * @param communityId 社区ID
     * @return 社区风险数据
     */
    public Map<String, Object> loadCommunityRiskData(String communityId) {
        Map<String, Object> result = new HashMap<>();
        
        try (Session session = neo4jDriver.session()) {
            // 查询社区高风险客户
            Result highRiskCustomers = session.run(
                "MATCH (c:Customer)-[:BELONGS_TO]->(community:Community {id: $communityId}) " +
                "WHERE c.riskScore > 700 " +
                "RETURN c.id AS customerId, c.riskScore AS riskScore " +
                "ORDER BY c.riskScore DESC LIMIT 10",
                Values.parameters("communityId", communityId)
            );
            
            List<Map<String, Object>> customers = new ArrayList<>();
            highRiskCustomers.forEachRemaining(record -> {
                Map<String, Object> customer = new HashMap<>();
                customer.put("customerId", record.get("customerId").asString());
                customer.put("riskScore", record.get("riskScore").asDouble());
                customers.add(customer);
            });
            
            result.put("highRiskCustomers", customers);
            
            // 查询社区风险统计
            Result stats = session.run(
                "MATCH (c:Customer)-[:BELONGS_TO]->(community:Community {id: $communityId}) " +
                "RETURN count(c) AS totalCustomers, " +
                "sum(CASE WHEN c.riskScore > 700 THEN 1 ELSE 0 END) AS highRiskCount, " +
                "avg(c.riskScore) AS avgRiskScore",
                Values.parameters("communityId", communityId)
            );
            
            if (stats.hasNext()) {
                Record record = stats.next();
                Map<String, Object> statistics = new HashMap<>();
                statistics.put("totalCustomers", record.get("totalCustomers").asInt());
                statistics.put("highRiskCount", record.get("highRiskCount").asInt());
                statistics.put("avgRiskScore", record.get("avgRiskScore").asDouble());
                statistics.put("highRiskRatio", 
                    (double) record.get("highRiskCount").asInt() / record.get("totalCustomers").asInt());
                result.put("statistics", statistics);
            }
        }
        
        return result;
    }
    
    /**
     * 加载训练数据
     * @param sampleSize 样本大小
     * @return 训练数据
     */
    public List<Map<String, Object>> loadTrainingData(int sampleSize) {
        List<Map<String, Object>> trainingData = new ArrayList<>();
        
        try (Session session = neo4jDriver.session()) {
            Result result = session.run(
                "MATCH (c:Customer) " +
                "WHERE exists(c.age) AND exists(c.income) AND exists(c.creditHistory) " +
                "RETURN c.id AS customerId, c.age AS age, c.income AS income, " +
                "c.creditHistory AS creditHistory, c.isFraud AS isFraud " +
                "LIMIT $sampleSize",
                Values.parameters("sampleSize", sampleSize)
            );
            
            result.forEachRemaining(record -> {
                Map<String, Object> sample = new HashMap<>();
                sample.put("customerId", record.get("customerId").asString());
                
                Map<String, Object> applicationData = new HashMap<>();
                applicationData.put("age", record.get("age").asDouble());
                applicationData.put("income", record.get("income").asDouble());
                applicationData.put("creditHistory", record.get("creditHistory").asDouble());
                
                sample.put("applicationData", applicationData);
                sample.put("isFraud", record.get("isFraud").asBoolean());
                
                trainingData.add(sample);
            });
        }
        
        return trainingData;
    }
} 