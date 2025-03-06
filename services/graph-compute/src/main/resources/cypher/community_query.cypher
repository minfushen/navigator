// 查询客户所在社区
MATCH (c:Customer {id: $customerId})-[:BELONGS_TO]->(community:Community)
RETURN community.id AS communityId, community.size AS size, community.riskScore AS riskScore

// 查询客户一度关系
MATCH (c:Customer {id: $customerId})-[r]-(other:Customer)
RETURN type(r) AS relationshipType, other.id AS otherId, other.riskScore AS riskScore

// 查询社区内高风险客户
MATCH (c:Customer)-[:BELONGS_TO]->(community:Community {id: $communityId})
WHERE c.riskScore > 700
RETURN c.id AS customerId, c.riskScore AS riskScore
ORDER BY c.riskScore DESC
LIMIT 10

// 查询风险传导路径
MATCH path = shortestPath((source:Customer {id: $sourceId})-[*..5]-(target:Customer {id: $targetId}))
RETURN [node IN nodes(path) | node.id] AS pathNodes,
       [rel IN relationships(path) | type(rel)] AS pathRelationships 