package com.reportci.model.test

enum Outcome {

    FAILURE('failure'),
    ERROR('error'),
    SUCCESS('success'),
    SKIPPED('skipped')
    
    String displayValue
    
    Outcome(String displayValue){
        this.displayValue = displayValue
    }
    
}