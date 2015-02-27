package com.reportci.model.test

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class TestSuite {
    String name
    Long id
    String packageName
    String hostName
    String timestampString
}
