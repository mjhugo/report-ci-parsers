package com.reportci.parser.staticanalysis

import com.reportci.model.staticanalysis.RuleViolation
import com.reportci.model.staticanalysis.Severity
import com.reportci.parser.ParserHandler
import com.reportci.parser.ReportParser
import groovy.util.slurpersupport.GPathResult

class CodeNarcReportParser extends ReportParser {

    CodeNarcReportParser(ParserHandler parserHandler) {
        super(parserHandler)
    }

    void parse(InputStream inputStream) {
        parseInternal(new XmlSlurper().parse(inputStream))
    }

    void parse(String reportText) {
        parseInternal(new XmlSlurper().parseText(reportText))
    }

    private parseInternal(GPathResult root) {
        root.Package.each { thePackage ->
            thePackage.File.each { theFile ->
                theFile.Violation.each { violation ->
                    RuleViolation v = new RuleViolation()
                    v.severity = Severity.fromCodeNarc(violation.@priority.toInteger())
                    v.file = "${thePackage.@path}/${theFile.@name}"
                    v.name = violation.@ruleName
                    v.lineNumber = violation.@lineNumber.toInteger()
                    v.sourceLine = violation.SourceLine.text().trim()
                    v.message = violation.Message.text().trim()
                    parserHandler.handle(v)
                }
            }
        }
    }

}
