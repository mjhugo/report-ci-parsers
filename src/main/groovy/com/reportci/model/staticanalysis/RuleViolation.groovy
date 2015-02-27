package com.reportci.model.staticanalysis

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class RuleViolation {

    Severity severity
    String name
    String message
    String file
    Integer lineNumber
    String sourceLine

}
