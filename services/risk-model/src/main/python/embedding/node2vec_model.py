import torch
import numpy as np
from torch_geometric.nn import Node2Vec
import networkx as nx
from neo4j import GraphDatabase

class GraphEmbeddingService:
    def __init__(self, uri, user, password):
        self.driver = GraphDatabase.driver(uri, auth=(user, password))
        self.embedding_dim = 128
        self.model = None
    
    def extract_graph_from_neo4j(self, query):
        """从Neo4j提取图数据"""
        with self.driver.session() as session:
            result = session.run(query)
            G = nx.Graph()
            
            for record in result:
                source = record["source"]
                target = record["target"]
                weight = record.get("weight", 1.0)
                G.add_edge(source, target, weight=weight)
            
            return G
    
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
        
        # 定义优化器
        loader = model.loader(batch_size=128, shuffle=True)
        optimizer = torch.optim.SparseAdam(model.parameters(), lr=0.01)
        
        # 训练模型
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
    
    def get_node_embedding(self, node_id):
        """获取节点嵌入向量"""
        if self.model is None:
            raise ValueError("模型尚未训练")
        
        with torch.no_grad():
            # 获取节点嵌入
            node_idx = torch.tensor([node_id])
            embedding = self.model(node_idx).numpy()
            return embedding[0]
    
    def get_customer_embeddings(self, customer_ids):
        """批量获取客户嵌入向量"""
        embeddings = {}
        
        for customer_id in customer_ids:
            embeddings[customer_id] = self.get_node_embedding(customer_id)
        
        return embeddings
    
    def calculate_similarity(self, customer_id1, customer_id2):
        """计算两个客户的相似度"""
        emb1 = self.get_node_embedding(customer_id1)
        emb2 = self.get_node_embedding(customer_id2)
        
        # 计算余弦相似度
        similarity = np.dot(emb1, emb2) / (np.linalg.norm(emb1) * np.linalg.norm(emb2))
        return similarity 