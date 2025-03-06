package com.bank.equity.controller;

import com.bank.equity.service.EquityService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/equity")
public class EquityController {
    
    private final EquityService equityService;
    
    public EquityController(EquityService equityService) {
        this.equityService = equityService;
    }
    
    @GetMapping("/analysis/{companyId}")
    public Map<String, Object> analyzeEquity(@PathVariable String companyId) {
        return equityService.analyzeEquityStructure(companyId);
    }
} 