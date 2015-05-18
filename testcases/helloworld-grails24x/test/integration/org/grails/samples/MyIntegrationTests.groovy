package org.grails.samples

import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin

@TestMixin(IntegrationTestMixin)
public class MyIntegrationTests {

    public MyIntegrationTests() {
        super();
        setTarget("MyIntegrationTests");
    }

    public void testMyWebUi() {
        println "Running test: ${getTarget()}"
    }
}