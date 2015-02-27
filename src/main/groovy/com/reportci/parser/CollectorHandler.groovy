package com.reportci.parser

class CollectorHandler implements ParserHandler {
    
    List results = []
    
    @Override
    void handle(def Object result) {
        results << result
    }
}
