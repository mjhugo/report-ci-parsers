package com.reportci.parser.test

import com.reportci.model.test.Outcome
import com.reportci.parser.CollectorHandler
import com.reportci.model.test.TestCase
import spock.lang.Specification

class JunitReportParserSpec extends Specification {

    void "simple passed test included in result list"() {
        setup:
        String report = '''<?xml version="1.0" encoding="UTF-8" ?>
<testsuites>
    <testsuite errors="2" failures="1" hostname="local" id="0" name="AuthorControllerSpec" package="tester" tests="7"
               time="6.039" timestamp="2015-02-14T03:35:51">
        <properties/>

        <testcase classname="tester.AuthorControllerSpec" name="Test the index action returns the correct model"
                  time="4.909"/>
    </testsuite>
</testsuites>
'''

        when:
        CollectorHandler collectorHandler = new CollectorHandler()
        JUnitReportParser parser = new JUnitReportParser(collectorHandler)
        parser.parse(report)
        Collection<TestCase> tests = collectorHandler.results
        TestCase firstTest = tests.find {
            it.className == 'tester.AuthorControllerSpec' && it.name == 'Test the index action returns the correct model'
        }
        
        collectorHandler = new CollectorHandler()
        parser.parserHandler = collectorHandler
        parser.parse(new ByteArrayInputStream( report.getBytes()))
        Collection<TestCase> testsFromInputStream = collectorHandler.results
        

        then:
        assert 'tester' == firstTest.testSuite.packageName
        assert 4.909 == firstTest.duration
        assert Outcome.SUCCESS == firstTest.outcome
        assert 'local' == firstTest.testSuite.hostName
        assert 0 == firstTest.testSuite.testSuiteId
        assert 'AuthorControllerSpec' == firstTest.testSuite.name
        assert testsFromInputStream == tests
    }

    void "simple error test included in result list"() {
        setup:
        String report = '''<?xml version="1.0" encoding="UTF-8" ?>
<testsuites>
    <testsuite errors="2" failures="1" hostname="local" id="0" name="AuthorControllerSpec" package="tester" tests="7"
               time="6.039" timestamp="2015-02-14T03:35:51">
        <properties/>

        <testcase classname="tester.AuthorControllerSpec" name="Test the save action correctly persists an instance"
                  time="0.559">
            <error message="Cannot redirect for object [tester.Author : (unsaved)] it is not a domain or has no identifier. Use an explicit redirect instead "
                   type="org.codehaus.groovy.grails.web.servlet.mvc.exceptions.CannotRedirectException">
                org.codehaus.groovy.grails.web.servlet.mvc.exceptions.CannotRedirectException: Cannot redirect for
                object [tester.Author : (unsaved)] it is not a domain or has no identifier. Use an explicit redirect
                instead
                at org.grails.plugins.web.rest.api.ControllersRestApi.redirect(ControllersRestApi.groovy:53)
                at tester.AuthorController.tt__save_closure9_closure14(AuthorController.groovy:43)
                at tester.AuthorController.$tt__save(AuthorController.groovy:40)
                at tester.AuthorControllerSpec.Test the save action correctly persists an
                instance(AuthorControllerSpec.groovy:54)
            </error>

        </testcase>
    </testsuite>
</testsuites>
'''
        when:
        CollectorHandler collectorHandler = new CollectorHandler()
        JUnitReportParser parser = new JUnitReportParser(collectorHandler)
        parser.parse(report)
        Collection<TestCase> tests = collectorHandler.results
        TestCase firstTest = tests.find {
            it.className == 'tester.AuthorControllerSpec' && it.name == 'Test the save action correctly persists an instance'
        }

        then:
        assert 'tester' == firstTest.testSuite.packageName
        assert 0.559 == firstTest.duration
        assert Outcome.ERROR == firstTest.outcome
        assert 'local' == firstTest.testSuite.hostName
        assert 0 == firstTest.testSuite.testSuiteId
        assert 'AuthorControllerSpec' == firstTest.testSuite.name

        assert firstTest.errors.first().message == 'Cannot redirect for object [tester.Author : (unsaved)] it is not a domain or has no identifier. Use an explicit redirect instead '
        assert firstTest.errors.first().type == 'org.codehaus.groovy.grails.web.servlet.mvc.exceptions.CannotRedirectException'
        assert firstTest.errors.first().text.trim().startsWith('org.codehaus.groovy.grails.web.servlet.mvc.exceptions.CannotRedirectException: Cannot red')
        assert firstTest.errors.first().text.trim().endsWith('instance(AuthorControllerSpec.groovy:54)')
        assert firstTest.errors.first().outcome == Outcome.ERROR
        assert firstTest.failures.size() == 0
    }

    void "simple failure test included in result list"() {
        setup:
        String report = '''<?xml version="1.0" encoding="UTF-8" ?>
<testsuites>
    <testsuite errors="2" failures="1" hostname="local" id="0" name="AuthorControllerSpec" package="tester" tests="7"
               time="6.039" timestamp="2015-02-14T03:35:51">
        <properties/>

        <testcase classname="tester.AuthorControllerSpec"
                  name="Test that the delete action deletes an instance if it exists" time="0.086">
            <failure
                    message="Condition not satisfied:&#xa;&#xa;Author.count() == 1&#xa;       |       |&#xa;       0       false&#xa;"
                    type="junit.framework.AssertionFailedError">junit.framework.AssertionFailedError: Condition not
                satisfied:

                Author.count() == 1
                | |
                0 false

                at tester.AuthorControllerSpec.Test that the delete action deletes an instance if it
                exists(AuthorControllerSpec.groovy:142)
            </failure>

        </testcase>
    </testsuite>
</testsuites>
'''
        when:
        CollectorHandler collectorHandler = new CollectorHandler()
        JUnitReportParser parser = new JUnitReportParser(collectorHandler)
        parser.parse(report)
        Collection<TestCase> tests = collectorHandler.results
        TestCase firstTest = tests.find {
            it.className == 'tester.AuthorControllerSpec' && it.name == 'Test that the delete action deletes an instance if it exists'
        }

        then:
        assert 'tester' == firstTest.testSuite.packageName
        assert 0.086 == firstTest.duration
        assert Outcome.FAILURE == firstTest.outcome
        assert 'local' == firstTest.testSuite.hostName
        assert 0 == firstTest.testSuite.testSuiteId
        assert 'AuthorControllerSpec' == firstTest.testSuite.name

        assert firstTest.failures.first().message == 'Condition not satisfied:\n\nAuthor.count() == 1\n       |       |\n       0       false\n'
        assert firstTest.failures.first().type == 'junit.framework.AssertionFailedError'
        assert firstTest.failures.first().text.trim().startsWith('junit.framework.AssertionFailedError: Condition not')
        assert firstTest.failures.first().text.trim().endsWith('exists(AuthorControllerSpec.groovy:142)')
        assert firstTest.failures.first().outcome == Outcome.FAILURE
        assert firstTest.errors.size() == 0
    }

    void "simple skipped test included in result list"() {
        setup:
        String report = '''<?xml version="1.0" encoding="UTF-8" ?>
<testsuites>
    <testsuite errors="2" failures="1" hostname="local" id="0" name="AuthorControllerSpec" package="tester" tests="7"
               time="6.039" timestamp="2015-02-14T03:35:51">
        <properties/>
        <testcase classname="tester.AuthorControllerSpec" name="Test the create action returns the correct model"
                  time="0">
            <skipped/>
        </testcase>
    </testsuite>
</testsuites>
'''
        when:
        CollectorHandler collectorHandler = new CollectorHandler()
        JUnitReportParser parser = new JUnitReportParser(collectorHandler)
        parser.parse(report)
        Collection<TestCase> tests = collectorHandler.results
        TestCase firstTest = tests.find {
            it.className == 'tester.AuthorControllerSpec' && it.name == 'Test the create action returns the correct model'
        }

        then:
        assert 'tester' == firstTest.testSuite.packageName
        assert 0 == firstTest.duration
        assert Outcome.SKIPPED == firstTest.outcome
        assert 'local' == firstTest.testSuite.hostName
        assert 0 == firstTest.testSuite.testSuiteId
        assert 'AuthorControllerSpec' == firstTest.testSuite.name
    }

}
