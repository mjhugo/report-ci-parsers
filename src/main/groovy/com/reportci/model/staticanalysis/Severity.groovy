package com.reportci.model.staticanalysis

enum Severity {

    HIGH('high', 1),
    MEDIUM('medium', 2),
    LOW('low', 3)
    
    String displayValue
    Integer codeNarcSeverity

    Severity(String displayValue, Integer codeNarcSeverity){
        this.displayValue = displayValue
        this.codeNarcSeverity = codeNarcSeverity
    }
    
    public static Severity fromCodeNarc(Integer codeNarcSeverity){
        Severity.values().find { it.codeNarcSeverity == codeNarcSeverity }
    }
    
}