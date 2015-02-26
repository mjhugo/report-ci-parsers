package com.reportci.parser.staticanalysis

import com.reportci.model.staticanalysis.RuleViolation
import com.reportci.model.staticanalysis.Severity

class CodeNarcReportParser {

    Collection<RuleViolation> parse(String reportText) {
        Collection<RuleViolation> violatons = []
        def slurper = new XmlSlurper().parseText(reportText)
        slurper.Package.each { thePackage ->
            thePackage.File.each { theFile ->
                theFile.Violation.each { violation ->
                    RuleViolation v = new RuleViolation()
                    v.severity = Severity.fromCodeNarc(violation.@priority.toInteger())
                    v.file = "${thePackage.@path}/${theFile.@name}"
                    v.name = violation.@ruleName
                    v.lineNumber = violation.@lineNumber.toInteger()
                    v.sourceLine = violation.SourceLine.text().trim()
                    v.message = violation.Message.text().trim()
                    violatons << v
                }
            }
        }
        return violatons
    }

}
