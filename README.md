

# 银行贷前评分卡反欺诈系统

基于图计算平台的贷前评分卡反欺诈系统，利用社区发现算法和Louvain算法构建图特征，应用于贷前评分卡模型中，完成反欺诈和贷款准入业务场景。

## 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                     贷前评分卡反欺诈系统                      │
├─────────────┬─────────────────────────┬────────────────────┤
│  数据接入层  │        计算引擎层        │      应用服务层     │
├─────────────┼─────────────────────────┼────────────────────┤
│ 客户信息    │ 社区发现算法(Louvain)    │ 反欺诈评分服务      │
│ 交易数据    │ 图嵌入计算               │ 贷款准入决策        │
│ 关系数据    │ 风险传导模型             │ 风险监控预警        │
├─────────────┼─────────────────────────┼────────────────────┤
│             │      存储与管理层        │                    │
│             ├─────────────────────────┤                    │
│             │ 图数据库(Neo4j)          │                    │
│             │ 特征存储(Delta Lake)     │                    │
│             │ 模型仓库                 │                    │
└─────────────┴─────────────────────────┴────────────────────┘
```

## 核心功能

1. **社区发现**：使用Louvain算法识别客户关系网络中的社区结构，发现潜在欺诈团伙
2. **图特征提取**：从关系网络中提取中心性、社区特征等，增强风险识别能力
3. **评分卡模型**：结合传统特征和图特征构建反欺诈评分卡，提高欺诈检测准确率
4. **实时风险监控**：基于Flink的实时风险传导监控，及时发现风险传播
5. **图嵌入表示**：使用Node2Vec生成客户的向量表示，支持相似度计算和聚类分析

## 技术栈

- **后端**：Spring Boot, Neo4j, Spark GraphX, Flink
- **图计算**：Louvain社区发现算法, Node2Vec图嵌入
- **机器学习**：XGBoost/LightGBM, PyTorch Geometric
- **存储**：Neo4j图数据库, Delta Lake

## 项目结构

```
financial-graph-platform/
├── apps/                           # 应用层
│   ├── equity-analysis/            # 股权穿透分析应用
│   │   ├── src/main/java/com/bank/equity/
│   │   │   ├── controller/         # REST控制器
│   │   │   ├── service/            # 业务服务
│   │   │   └── EquityAnalysisApplication.java
│   │   └── pom.xml
│   │
│   └── loan-approval/              # 贷款审批应用
│       ├── src/main/java/com/bank/loan/
│       │   ├── controller/         # REST控制器
│       │   ├── service/            # 业务服务
│       │   ├── dto/                # 数据传输对象
│       │   └── LoanApprovalApplication.java
│       ├── src/main/resources/
│       │   ├── application.yml
│       │   └── logback.xml
│       ├── Dockerfile
│       └── pom.xml
│
├── services/                        # 服务层
│   ├── graph-compute/              # 图计算服务
│   │   ├── src/main/java/com/bank/graph/
│   │   │   ├── algorithm/          # 图算法实现
│   │   │   ├── factory/            # 算法工厂
│   │   │   ├── service/            # 图计算服务
│   │   │   └── api/                # 服务API
│   │   ├── src/main/resources/
│   │   │   ├── cypher/             # Cypher查询
│   │   │   └── application.yml
│   │   └── pom.xml
│   │
│   └── risk-model/                 # 风险模型服务
│       ├── src/main/java/com/bank/risk/
│       │   ├── feature/            # 特征工程
│       │   ├── model/              # 模型实现
│       │   ├── data/               # 数据访问
│       │   ├── monitor/            # 风险监控
│       │   └── controller/         # REST控制器
│       ├── src/main/python/
│       │   ├── embedding/          # 图嵌入模型
│       │   ├── gnn/                # 图神经网络
│       │   └── app.py              # Python服务入口
│       ├── requirements.txt
│       ├── Dockerfile
│       └── pom.xml
│
├── engine/                          # 引擎层
│   ├── compute/                    # 计算引擎
│   │   ├── src/main/java/com/bank/engine/compute/
│   │   │   ├── spark/              # Spark引擎
│   │   │   ├── flink/              # Flink引擎
│   │   │   └── neo4j/              # Neo4j引擎
│   │   └── pom.xml
│   │
│   └── security/                   # 安全引擎
│       ├── src/main/java/com/bank/security/
│       │   ├── encryption/         # 加密服务
│       │   └── audit/              # 审计服务
│       └── pom.xml
│
├── infrastructure/                  # 基础设施层
│   └── common/                     # 公共组件
│       ├── src/main/java/com/bank/infrastructure/
│       │   ├── config/             # 配置类
│       │   ├── exception/          # 异常处理
│       │   ├── util/               # 工具类
│       │   └── model/              # 公共模型
│       └── pom.xml
│
├── docker-compose.yml               # Docker编排
├── pom.xml                          # 父POM
└── README.md                        # 项目文档
```

## 快速开始

### 环境准备

- JDK 11+
- Maven 3.6+
- Python 3.8+
- Docker & Docker Compose

### 构建项目

```bash
# 克隆项目
git clone https://github.com/your-org/financial-graph-platform.git
cd financial-graph-platform

# 构建项目
mvn clean package
```

### 启动服务

```bash
# 启动所有服务
docker-compose up -d

# 初始化数据
cat services/graph-compute/src/main/resources/cypher/init_data.cypher | \
  docker exec -i neo4j cypher-shell -u neo4j -p password
```

### 访问服务

- 贷款审批应用: http://localhost:8083
- 风险模型服务: http://localhost:8084
- Neo4j浏览器: http://localhost:7474

## API接口文档

### 反欺诈评估接口

```
POST /api/v1/fraud/evaluate
```

请求示例:
```json
{
  "customerId": "10001",
  "applicationData": {
    "age": 35,
    "income": 120000,
    "creditHistory": 720
  }
}
```

响应示例:
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "customerId": "10001",
    "fraudScore": 750,
    "creditScore": 680,
    "decision": "APPROVE",
    "reasons": [],
    "communityId": "C1001",
    "riskLevel": "低风险"
  },
  "timestamp": "2023-07-15T10:30:45"
}
```

### 风险网络查询接口

```
GET /api/v1/fraud/network/{customerId}
```

响应示例:
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "customerId": "10001",
    "networkSize": 15,
    "riskConnections": ["10002", "10004", "10005"],
    "communityId": "C1001"
  },
  "timestamp": "2023-07-15T10:32:15"
}
```

### 模型训练接口

```
POST /api/v1/model/train
```

请求示例:
```json
[
  {
    "customerId": "10001",
    "applicationData": {
      "age": 35,
      "income": 120000,
      "creditHistory": 720
    },
    "isFraud": false
  },
  {
    "customerId": "10003",
    "applicationData": {
      "age": 28,
      "income": 65000,
      "creditHistory": 620
    },
    "isFraud": true
  }
]
```

响应示例:
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "modelId": "scorecard-20230715",
    "metrics": {
      "auc": 0.92,
      "ks": 0.78,
      "precision": 0.85,
      "recall": 0.82
    },
    "featureImportance": {
      "community_risk_ratio": 0.35,
      "risk_exposure": 0.28,
      "credit_history": 0.22,
      "income": 0.15
    }
  },
  "timestamp": "2023-07-15T10:35:20"
}
```

## 核心算法

### Louvain社区发现算法

Louvain算法是一种基于模块度优化的社区发现算法，通过迭代优化模块度来发现网络中的社区结构。在本系统中，我们使用Louvain算法来识别客户关系网络中的潜在欺诈团伙。

```java
public class LouvainCommunityDetection implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private final int MAX_ITERATIONS = 10;
    private final double MIN_MODULARITY_GAIN = 0.0001;
    
    /**
     * 执行Louvain社区发现算法
     * @param graph 输入图
     * @return 社区标签图
     */
    public Graph<Long, Double> detect(Graph<String, Double> graph) {
        // 初始化：每个节点分配唯一社区ID
        Graph<Long, Double> communityGraph = graph.mapVertices(
            (vid, attr) -> (long) vid,
            (edge) -> edge
        );
        
        double modularity = calculateModularity(communityGraph);
        double newModularity;
        int iterations = 0;
        
        // 迭代优化社区划分
        do {
            // 第一阶段：节点重分配
            communityGraph = localMoving(communityGraph);
            
            // 第二阶段：社区合并
            communityGraph = aggregateCommunities(communityGraph);
            
            newModularity = calculateModularity(communityGraph);
            double gain = newModularity - modularity;
            modularity = newModularity;
            iterations++;
            
            if (gain < MIN_MODULARITY_GAIN || iterations >= MAX_ITERATIONS) {
                break;
            }
        } while (true);
        
        return communityGraph;
    }
}
```

### Node2Vec图嵌入算法

Node2Vec是一种图嵌入算法，可以将图中的节点映射到低维向量空间，保留节点之间的结构相似性。在本系统中，我们使用Node2Vec来生成客户的向量表示，用于相似度计算和聚类分析。

```python
class GraphEmbeddingService:
    def __init__(self, uri, user, password):
        self.driver = GraphDatabase.driver(uri, auth=(user, password))
        self.embedding_dim = 128
        self.model = None
    
    def train_node2vec(self, G, walk_length=80, num_walks=10, p=1, q=1):
        """训练Node2Vec模型"""
        # 将NetworkX图转换为PyTorch Geometric格式
        edge_index = torch.tensor(list(G.edges())).t().contiguous()
        
        # 初始化Node2Vec模型
        model = Node2Vec(
            edge_index=edge_index,
            embedding_dim=self.embedding_dim,
            walk_length=walk_length,
            num_walks=num_walks,
            p=p,
            q=q,
            sparse=True
        )
        
        # 训练模型
        loader = model.loader(batch_size=128, shuffle=True)
        optimizer = torch.optim.SparseAdam(model.parameters(), lr=0.01)
        
        model.train()
        for epoch in range(100):
            total_loss = 0
            for pos_rw, neg_rw in loader:
                optimizer.zero_grad()
                loss = model.loss(pos_rw, neg_rw)
                loss.backward()
                optimizer.step()
                total_loss += loss.item()
            
            if (epoch + 1) % 10 == 0:
                print(f'Epoch: {epoch+1}, Loss: {total_loss/len(loader):.4f}')
        
        self.model = model
        return model
```

## 业务场景应用

### 贷前反欺诈

系统通过分析申请人的关系网络和交易行为，识别潜在的欺诈风险。主要步骤包括：

1. 提取申请人的传统特征（年龄、收入、信用历史等）
2. 提取申请人的图特征（社区特征、中心性特征、风险传导特征等）
3. 结合传统特征和图特征，使用评分卡模型计算欺诈风险分数
4. 根据风险分数和预设规则，做出贷款决策（通过、拒绝、人工审核）

### 风险监控预警

系统通过实时监控客户关系网络中的风险传导，及时发现潜在风险。主要功能包括：

1. 监控社区风险指数变化，发现异常社区
2. 监控客户交易行为，发现异常交易模式
3. 分析风险传导路径，预测风险传播方向
4. 生成风险预警报告，支持风险管理决策

## 部署方案

### 单机部署

适用于开发测试环境，所有服务部署在同一台服务器上。

```bash
# 启动所有服务
docker-compose up -d
```

### 分布式部署

适用于生产环境，各服务分布在不同服务器上，通过服务注册与发现实现通信。

1. 部署Neo4j集群
2. 部署Spark集群
3. 部署Flink集群
4. 部署应用服务
5. 配置负载均衡

## 性能优化

1. **图计算优化**：使用Spark GraphX进行分布式图计算，提高大规模图处理能力
2. **数据存储优化**：使用Neo4j图数据库存储关系数据，提高图查询效率
3. **实时计算优化**：使用Flink进行实时风险监控，降低延迟
4. **模型优化**：使用特征选择和模型调优，提高模型性能
5. **缓存优化**：对频繁访问的数据进行缓存，减少数据库访问

## 安全措施

1. **数据加密**：使用国密SM4算法对敏感数据进行加密
2. **访问控制**：基于角色的访问控制，限制用户权限
3. **审计日志**：记录所有操作日志，支持安全审计
4. **数据脱敏**：对展示数据进行脱敏处理，保护客户隐私
5. **安全扫描**：定期进行安全漏洞扫描，及时修复安全问题

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建Pull Request

## 许可证

本项目采用 MIT 许可证

## 联系方式

- 项目负责人:404006180@qq.com
- 技术支持: 404006180@qq.com
