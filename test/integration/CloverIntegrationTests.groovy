import grails.test.AbstractCliTestCase

/**
 */
public class CloverIntegrationTests extends AbstractCliTestCase {

  void testToggleCloverOn() {

    File testProjectDir = new File("testcases/petclinic")

    setWorkDir(testProjectDir)
    setOutputDir(new File("build/testcases/petclinic"))

    execute(["test-app", "-clover.on"])
    int status = waitForProcess()
    println "OUTPUT: " + getOutput()
    assertEquals 0, status
    verifyHeader()
    assertTrue new File(testProjectDir, "build/clover/report/index.html").exists()

  }

}