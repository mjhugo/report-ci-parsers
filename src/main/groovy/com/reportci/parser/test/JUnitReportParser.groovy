package com.reportci.parser.test

import com.reportci.model.test.Outcome
import com.reportci.model.test.TestCase
import com.reportci.model.test.TestError
import com.reportci.model.test.TestSuite

class JUnitReportParser {

    Collection<TestCase> parse(String reportText) {
        Collection<TestCase> tests = []
        def xmlSlurper = new XmlSlurper().parseText(reportText)
        xmlSlurper.testsuite.each { testSuiteXml ->
            TestSuite testSuiteDto = new TestSuite()
            testSuiteDto.packageName = testSuiteXml.@package
            testSuiteDto.id = testSuiteXml.@id.toLong()
            testSuiteDto.name = testSuiteXml.@name
            testSuiteDto.timestampString = testSuiteXml.@timestamp
            testSuiteDto.hostName = testSuiteXml.@hostname

            testSuiteXml.testcase.each { testcase ->
                TestCase test = new TestCase(testSuite: testSuiteDto)
                test.className = testcase.@classname
                test.name = testcase.@name
                test.duration = testcase.@time.toDouble()
                test.outcome = Outcome.SUCCESS

                if (testcase.error.size()) {
                    test.outcome = Outcome.ERROR
                    testcase.error.each { error ->
                        test.errors << new TestError(
                                type: error.@type,
                                message: error.@message,
                                text: error.text(),
                                outcome: Outcome.ERROR
                        )
                    }
                }

                if (testcase.failure.size()) {
                    test.outcome = Outcome.FAILURE
                    testcase.failure.each { error ->
                        test.failures << new TestError(
                                type: error.@type,
                                message: error.@message,
                                text: error.text(),
                                outcome: Outcome.FAILURE
                        )
                    }
                }

                if (testcase.skipped.size()) {
                    test.outcome = Outcome.SKIPPED
                }

                tests << test
            }
        }
        return tests
    }
}
