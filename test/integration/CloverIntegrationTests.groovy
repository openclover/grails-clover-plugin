import grails.test.AbstractCliTestCase

/**
 */
class CloverIntegrationTests extends AbstractCliTestCase {

    void testToggleCloverOn() {
        File testProjectDir = new File("testcases/petclinic203")

        setWorkDir(testProjectDir)
        setOutputDir(new File("build/testcases/petclinic203"))

        execute(["test-app", "-clover.on"])
        int status = waitForProcess()
        println "OUTPUT: " + getOutput()
        assertEquals 0, status
        verifyHeader()
        assertTrue new File(testProjectDir, "target/clover/report/index.html").exists()
    }

}