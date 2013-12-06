package org.grails.samples

/**
 */

public class MyIntegrationTests extends GroovyTestCase
{

  public MyIntegrationTests() {
    super();
    setName("MyIntegrationTests");
  }

  public void testMyWebUi() {
    println "Running test: ${getName()}"
  }
}