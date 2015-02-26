package com.reportci.parser.staticanalysis

import com.reportci.parser.staticanalysis.CodeNarcReportParser
import com.reportci.model.staticanalysis.Severity
import com.reportci.model.staticanalysis.RuleViolation
import spock.lang.Specification

class CodenarcParserSpec extends Specification {

    void "parse codenarc xml"() {
        setup:
        String report = '''<CodeNarc url="http://www.codenarc.org" version="0.22">
<Report timestamp="Feb 23, 2015 10:41:34 PM"/>
<Project title="CodeNarc Report">
<SourceDirectory/>
</Project>
<PackageSummary totalFiles="8" filesWithViolations="8" priority1="0" priority2="22" priority3="6"/>
<Package path="grails-app" totalFiles="4" filesWithViolations="4" priority1="0" priority2="6" priority3="2"/>
<Package path="grails-app/controllers" totalFiles="2" filesWithViolations="2" priority1="0" priority2="2" priority3="2"/>
<Package path="grails-app/controllers/tester" totalFiles="2" filesWithViolations="2" priority1="0" priority2="2" priority3="2">
<File name="AuthorController.groovy">
<Violation ruleName="NoWildcardImports" priority="3" lineNumber="5">
<SourceLine>
<![CDATA[
import static org.springframework.http.HttpStatus.*
]]>
</SourceLine>
</Violation>
<Violation ruleName="GrailsMassAssignment" priority="2" lineNumber="23">
<SourceLine>
<![CDATA[ respond new Author(params) ]]>
</SourceLine>
<Message>
<![CDATA[ Restrict mass attribute assignment ]]>
</Message>
</Violation>
</File>
<File name="BookController.groovy">
<Violation ruleName="NoWildcardImports" priority="3" lineNumber="5">
<SourceLine>
<![CDATA[
import static org.springframework.http.HttpStatus.*
]]>
</SourceLine>
</Violation>
<Violation ruleName="GrailsMassAssignment" priority="2" lineNumber="23">
<SourceLine>
<![CDATA[ respond new Book(params) ]]>
</SourceLine>
<Message>
<![CDATA[ Restrict mass attribute assignment ]]>
</Message>
</Violation>
</File>
</Package>
</CodeNarc>
'''

        when:
        CodeNarcReportParser codeNarcParser = new CodeNarcReportParser()
        List<RuleViolation> violations = codeNarcParser.parse(report)
        def authorViolations = violations.findAll {it.file == 'grails-app/controllers/tester/AuthorController.groovy'}
        
        then:
        assert Severity.LOW == authorViolations.find {it.lineNumber == 5}.severity
        assert 'NoWildcardImports' == authorViolations.find {it.lineNumber == 5}.name
        assert 'import static org.springframework.http.HttpStatus.*' == authorViolations.find {it.lineNumber == 5}.sourceLine.trim()
        assert '' == authorViolations.find {it.lineNumber == 5}.message
        assert Severity.MEDIUM == authorViolations.find {it.lineNumber == 23}.severity
        assert 'GrailsMassAssignment' == authorViolations.find {it.lineNumber == 23}.name
        assert 'respond new Author(params)' == authorViolations.find {it.lineNumber == 23}.sourceLine.trim()
        assert 'Restrict mass attribute assignment' == authorViolations.find {it.lineNumber == 23}.message

    }

}
