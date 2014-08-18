package org.grails.samples

import grails.test.mixin.TestMixin
import grails.test.mixin.integration.IntegrationTestMixin

@TestMixin(IntegrationTestMixin)
public class MyIntegrationTests
{

  public MyIntegrationTests() {
    super();
    setName("MyIntegrationTests");
  }

  public void testMyWebUi() {
    println "Running test: ${getName()}"
  }
}