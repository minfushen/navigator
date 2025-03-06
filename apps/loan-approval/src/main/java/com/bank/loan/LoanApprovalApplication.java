package com.bank.loan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bank.loan", "com.bank.risk", "com.bank.graph"})
public class LoanApprovalApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LoanApprovalApplication.class, args);
    }
} 