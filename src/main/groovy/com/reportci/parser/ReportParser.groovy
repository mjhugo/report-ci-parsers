package com.reportci.parser

abstract class ReportParser {

    abstract void parse(String text)
    abstract void parse(InputStream inputStream)

    ParserHandler parserHandler
    
    ReportParser(ParserHandler parserHandler) {
        this.parserHandler = parserHandler
    }

}