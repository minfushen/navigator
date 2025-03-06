package com.bank.governance.lineage;

import org.neo4j.driver.*;

public class GraphLineageTracker {
    
    private final Driver neo4jDriver;
    
    public void trackLineage(String sourceId, String targetId, String operation) {
        try (Session session = neo4jDriver.session()) {
            session.writeTransaction(tx -> {
                tx.run("MERGE (s:Node {id: $source}) " +
                      "MERGE (t:Node {id: $target}) " +
                      "CREATE (s)-[:LINEAGE {op: $op}]->(t)",
                      Values.parameters("source", sourceId,
                                     "target", targetId,
                                     "op", operation));
                return null;
            });
        }
    }
} 