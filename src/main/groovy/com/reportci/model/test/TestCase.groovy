package com.reportci.model.test

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class TestCase {

    TestSuite testSuite

    String className
    String name
    Double duration
    Outcome outcome
    
    Set<TestError> failures = []
    Set<TestError> errors = []

}
