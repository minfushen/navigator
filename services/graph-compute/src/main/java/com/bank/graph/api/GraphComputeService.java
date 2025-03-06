package com.bank.graph.api;

import org.apache.spark.graphx.Graph;
import java.util.Map;

public interface GraphComputeService {
    /**
     * 执行社区发现算法
     */
    Map<String, Object> detectCommunities(String graphQuery);
    
    /**
     * 计算节点中心性
     */
    Map<String, Double> calculateCentrality(String nodeId, String centralityType);
    
    /**
     * 执行图剪枝算法
     */
    Graph<String, Double> pruneGraph(Graph<String, Double> graph, double threshold);
} 