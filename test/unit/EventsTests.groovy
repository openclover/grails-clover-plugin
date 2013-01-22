import org.codehaus.gant.GantBuilder
import org.apache.tools.ant.DefaultLogger
import org.apache.tools.ant.Project
import com.atlassian.clover.api.optimization.StringOptimizable
import com.cenqua.clover.test.optimization.Snapshot
import com.cenqua.clover.CloverDatabase
import com.cenqua.clover.context.ContextSet
import com.cenqua.clover.CoverageDataSpec
import com.cenqua.clover.util.FileUtils
import com.cenqua.clover.tasks.AntInstrumentationConfig
import com.atlassian.clover.api.optimization.TestOptimizer
import com.atlassian.clover.api.optimization.OptimizationOptions
import org.apache.tools.ant.BuildEvent

/**
 * Tests for some methods from _Events.groovy file
 */
public class EventsTests extends GroovyTestCase {

    /** Helper class for logging into buffer */
    static class StringLogger extends DefaultLogger {
        private final List<String> buffer = new LinkedList<String>()

        public void log(int level, String message, Throwable t) {
            buffer.add("log:" + message)
        }

        public void messageLogged(BuildEvent event) {
            buffer.add("messageLogged: " + event.getMessage());
        }

        protected void printMessage(String message, PrintStream stream, int priority) {
            buffer.add("printMessage:" + message);
        }

        public boolean containsFragment(final String fragment) {
            for (String line : buffer) {
                if (line.indexOf(fragment) >= 0) {
                    return true
                }
            }
            return false
        }

        public void clear() {
            buffer.clear()
        }
    }

    String projectDir = System.getProperty("project.dir")
    String testRunTmpDir = System.getProperty("testrun.tmpdir")
    Binding binding
    GantBuilder ant
    Object script
    StringLogger antBuildListener = new StringLogger()

    public void setUp() {
        ant = new GantBuilder()
        ant.project.addBuildListener(antBuildListener)

        binding = new Binding([projectWorkDir: testRunTmpDir,
                basedir: testRunTmpDir,
                metadata: [],
                ant: ant,
                userHome: testRunTmpDir,
                grailsWorkDir: testRunTmpDir,
                projectTargetDir: testRunTmpDir,
                cloverPluginDir: new File(".")
        ]);
        GroovyScriptEngine shell = new GroovyScriptEngine("./scripts/_Events.groovy");
        script = shell.run("_Events.groovy", binding);
    }

    /**
     * Test for configureAntInstr()
     */
    public void testConfigureAntInstr() {
        // input data
        ConfigObject clover = new ConfigObject()
        clover.preserve = true
        clover.tmpDir = "/some/tmp/dir"

        // populate into Ant config
        AntInstrumentationConfig antConfig = new AntInstrumentationConfig(ant.project)
        script.configureAntInstr(clover, antConfig)

        // verify values
        assertEquals true, antConfig.isPreserve()
        assertEquals new File("/some/tmp/dir").getAbsolutePath(), antConfig.getTmpDir().getAbsolutePath()
    }

    /**
     * Test for configureLicense() with evaluation license key
     */
    public void testConfigureUsingEvalLicense() {
        ConfigObject config = new ConfigObject()
        String origLicenseLoc = System.properties.remove('clover.license.path')
        try {
            script.configureLicense(config)
            assertNotNull System.getProperty('clover.license.path')
        }
        finally {
            if (origLicenseLoc) {
                System.setProperty('clover.license.path', origLicenseLoc)
            }
        }
    }

    /**
     * Test for configureLicense()
     */
    public void testConfigureLicense() {
        ConfigObject config = new ConfigObject()
        String origLicenseLoc = System.properties.remove('clover.license.path')
        try {
            config.license.path = "/path/to/clover.license"
            script.configureLicense(config)
            assertEquals config.license.path, System.getProperty('clover.license.path')
        }
        finally {
            if (origLicenseLoc) {
                System.setProperty('clover.license.path', origLicenseLoc)
            }
        }
    }

    /**
     * Test for createTestPattern()
     */
    public void testCreateTestPattern() {
        assertEquals "Something", script.createTestPattern("Something")
        assertEquals "Some", script.createTestPattern("SomeTests")
    }

    /**
     * Test for scanForSourceFiles
     */
//    public void testScanForSourceFiles() {
//        // search for the following files
//        //  /test
//        //     /unit
//        //        MyTest.java        - OK
//        //        Something.groovy   - OK
//        //     /integration
//        //        MyIT.java          - OK
//        //        NotATest.txt       - NOT OK
//
//        GrailsTestTargetPattern testTargetPattern = new GrailsTestTargetPattern("**/")
//
//        List<File> unitTests = script.scanForSourceFiles(testTargetPattern, script.binding, "unit")
//        List<File> integrationTests = script.scanForSourceFiles(testTargetPattern, script.binding, "integration")
//
//        assertEquals 2, unitTests.size()
//        assertEquals 1, integrationTests.size()
//    }

    /**
     * Test for toggleAntLogging()
     * @throws Exception
     */
    public void testToggleAntLogging() throws Exception {
        ConfigObject config = new ConfigObject()
        config.debug = true
        int outputLevel = 0; // this gets modified by toggleAntLogging
        def logger = [setMessageOutputLevel: {outputLevel = it}] as DefaultLogger
        ant.project.addBuildListener(logger)
        script.toggleAntLogging config
        println outputLevel
        assertTrue outputLevel == Project.MSG_DEBUG
    }


    /**
     * Test for toggleCloverOn()
     */
    public void testToggleCloverOn() {
        ConfigObject cloverConfig = new ConfigObject()
        cloverConfig.on = true
        cloverConfig.initstring = "my_clover.db"

        antBuildListener.clear()
        script.toggleCloverOn(cloverConfig)
        assertTrue antBuildListener.containsFragment("Clover is enabled with initstring")
        assertTrue antBuildListener.containsFragment("my_clover.db")
    }

    /**
     * Some calls of Clover API are made through reflections. Check
     */
    public void testReflections() {
        // input data
        List optimizables = new ArrayList()
        optimizables << new StringOptimizable("AbcTest")

        // prepare empty database and snapshot file
        File dbFile = File.createTempFile("clover", "clover.db");
        dbFile.delete()
        File snapshotFile = File.createTempFile("clover", "clover.snapshot")
        snapshotFile.delete()
        CloverDatabase db = new CloverDatabase(dbFile, false, "My Project", new ContextSet(), new CoverageDataSpec())
        Snapshot snapshot = Snapshot.generateFor(db, snapshotFile.absolutePath)
        snapshot.store()

        // run optimization - as there are no classes in snapshot it shall return all input
        def builder = new OptimizationOptions.Builder()
        def options = builder
                .enabled(true)
                .initString(dbFile.absolutePath)
                .snapshot(snapshotFile)
                .build()
        def optimizer = new TestOptimizer(options)
        List optimizedTests = optimizer.optimizeObjects(optimizables)

        // validation: one element having getName() method and returning "AbcTest"
        assertEquals 1, optimizedTests.size()
        optimizedTests.each {
            // Clover Cover<->Clover Grails interface test - check call of getName() via reflections works
            final String className = (String)it.getClass().getMethod("getName").invoke(it)
            assertEquals "AbcTest", className
        }
    }
}
