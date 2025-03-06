package com.bank.infrastructure.repository;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Repository
public class Neo4jRepository {

    private final Driver driver;
    
    public Neo4jRepository(Driver driver) {
        this.driver = driver;
    }
    
    public <T> List<T> query(String cypher, Map<String, Object> parameters, Function<Record, T> mapper) {
        try (Session session = driver.session()) {
            Result result = session.run(cypher, Values.value(parameters));
            List<T> items = new ArrayList<>();
            result.forEachRemaining(record -> items.add(mapper.apply(record)));
            return items;
        }
    }
    
    public <T> T querySingle(String cypher, Map<String, Object> parameters, Function<Record, T> mapper) {
        try (Session session = driver.session()) {
            Result result = session.run(cypher, Values.value(parameters));
            if (result.hasNext()) {
                return mapper.apply(result.next());
            }
            return null;
        }
    }
    
    public void execute(String cypher, Map<String, Object> parameters) {
        try (Session session = driver.session()) {
            session.run(cypher, Values.value(parameters));
        }
    }
} 