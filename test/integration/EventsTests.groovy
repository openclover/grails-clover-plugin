import grails.test.AbstractCliTestCase

/**
 */
public class EventsTests extends AbstractCliTestCase {

  void testToggleCloverOn() {

    File testProjectDir = new File("testcases/petclinic")
    setWorkDir(testProjectDir)
    setOutputDir(new File("build/testcases/petclinic"))

    execute(["test-app", "-clover.enabled"])
    assertEquals 0, waitForProcess()
    verifyHeader()
    assertTrue new File(testProjectDir, "build/clover/report/index.html").exists()

  }

}