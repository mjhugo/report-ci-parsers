package com.reportci.model.test

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class TestError {
    String type
    String message
    String text
    Outcome outcome
}
