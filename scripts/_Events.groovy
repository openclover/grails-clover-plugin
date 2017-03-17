import org.apache.tools.ant.BuildLogger
import org.apache.tools.ant.Project

// Note: the GRAILS-5755 fix has solved problem with loading dependencies only partially:
//  - we don't have to load classes manually using 'classLoader.loadClass(name)' but
//  - during plugin installation via 'grails install-plugin' clover.jar is still not available
//     - note that after installation it is accessible
//     - note that it's also available when installation is made via BuildConfig.groovy)
// Workaround:
//  - instead of 'import com.atlassian.clover.ant.tasks.AntInstrumentationConfig + new AntInstrumentationConfig()' we use
//    'com.atlassian.clover.ant.tasks.AntInstrumentationConfig.newInstance()' (thanks to Groovy syntax it does not complain)
//  - FileOptimizable does not implement Optimizable interface and the "raw" method TestOptimizer.optimizeObjects() is used


/* SOME CLOVER DEFAULT VALUES */

defCloverSrcDirs = ["src/java", "src/groovy", "test/unit", "test/integration", "test/functional", "grails-app"]
defCloverIncludes = ["**/*.groovy", "**/*.java"]
defCloverExcludes = ["**/conf/**", "**/plugins/**"]
defCloverReportDir = "${projectTargetDir}/clover/report" // flim-flamming between projectWorkDir and build. build is consistent
defCloverHistoryDir = "${basedir}/.cloverhistory"
defCloverReportStyle = "adg"
defCloverReportTitle = metadata["app.name"]
defCloverHistorical = true // by default, we will generate a historical report.
defCloverSnapshotFile = new File("$projectWorkDir", "clover.snapshot") // this location can be overridden via the -clover.snapshotLocation argument

defStoredTestTargetPatterns = []

/* HELPER METHODS */

/**
 * Return Class 'org.codehaus.groovy.grails.test.GrailsTestTargetPattern' (grails 2.4.3 and older) or
 * 'org.grails.test.GrailsTestTargetPattern' (grails 2.4.4 and newer)
 *
 * @return Class
 * @throws ClassNotFoundException if none of two names is found
 */
Class getGrailsTestTargetPatternClass() {
    try {
        return Class.forName("org.codehaus.groovy.grails.test.GrailsTestTargetPattern")
    } catch (ClassNotFoundException ex) {
        return Class.forName("org.grails.test.GrailsTestTargetPattern")
    }
}

/* EVENT HANDLERS */

eventCompileStart = {kind ->
    ConfigObject config = mergeConfig()
    // Ants Project is available via: kind.ant.project
    if (config.on && config.debug) {
        println "Clover: Compile start. Setting 'grover.ast.dump=" + config.dumpAST + "' system property."
    }
    System.setProperty("grover.ast.dump", "" + config.dumpAST)
}

eventSetClasspath = {URLClassLoader rootLoader ->
//  grailsSettings.compileDependencies.each { println it }

    ConfigObject config = mergeConfig()

    if (config.debug) {
        println "Clover: Dumping binding variables:"
        binding.variables.each { println it.key + " = " + it.value } // dumps all available vars and their values
    }

    toggleAntLogging(config)

    // automatically enable clover when optimizing
    if (config.on || config.optimize) {
        println "Clover: Clover is enabled. Configuration: ${config}"
        toggleCloverOn(config)

        // do not clean when optimizing or when user explicitly set clover.forceClean=false
        if ((!config.containsKey('forceClean') || config.forceClean) && !config.optimize) {
            // force a clean
            def webInf = "${basedir}/web-app/WEB-INF"
            def cleanDirs = ["${webInf}/classes", "${webInf}/lib", "${projectWorkDir}/gspcompile", classesDirPath, testDirPath, "${projectWorkDir}/clover"]

            println "Clover: Forcing a clean to ensure Clover instrumentation occurs. Disable by setting: clover.forceClean=false "
            cleanDirs.each {
                ant.delete(dir: it, failonerror: false)
            }
        }
    }
}

eventTestPhasesStart = {phase ->

//  binding.variables.each { println it.key + " = " + it.value } // dumps all available vars and their values
    defStoredTestTargetPatterns = testNames.collect {
        String it -> getGrailsTestTargetPatternClass().newInstance(it)
    }
}

class FileOptimizable /* Cannot declare 'implements com.atlassian.clover.api.optimization.Optimizable' due to
        problem with dependency resolution during 'grails install-plugin' */ {

    final File file
    final File baseDir

    FileOptimizable(file, baseDir) {
        this.file = file
        this.baseDir = baseDir
    }

    String getName() {
        sourceFileToClassName(baseDir, file)
    }

    @Override
    String toString() {
        return getName()
    }

    /**
     * Gets the corresponding class name for a source file of this test type.
     *
     * Copied from GrailsTestTypeSupport.groovy
     */
    String sourceFileToClassName(File sourceDir, File sourceFile) {
        String relativePath = getRelativePathName(sourceDir, sourceFile)
        def suffixPos = relativePath.lastIndexOf(".")
        relativePath[0..(suffixPos - 1)].replace(File.separatorChar, '.' as char)
    }

    String getRelativePathName(File sourceDir, File sourceFile) {
        def filePath = sourceFile.canonicalPath
        def basePath = sourceDir.canonicalPath

        if (!filePath.startsWith(basePath)) {
            throw new IllegalArgumentException("File path (${filePath}) is not descendent of base path (${basePath}).")
        }

        return filePath.substring(basePath.size() + 1)
    }
}

eventTestCompileStart = { type ->
    ConfigObject config = mergeConfig()
    if (config.optimize || config.on) {
        // GrailsProjectTestRunner has been introduced in grails 2.3 so let's check if we've got it here
        // using getVariable() instead of hasVariable() because the latter is not available in grails 1.3
        try {
            getVariable('projectTestRunner')

            // copy config from the current project to the testRunner's internal one
            def antConfig = com.atlassian.clover.ant.tasks.AntInstrumentationConfig.getFrom(ant.project)
            antConfig.setIn(projectTestRunner.projectTestCompiler.ant.project)

            // add GroovycSupport's build listener to this project, it will reconfigure groovyc tasks (since grails 2.3)
            com.atlassian.clover.ant.groovy.GroovycSupport.ensureAddedTo(projectTestRunner.projectTestCompiler.ant.project)
        } catch (MissingPropertyException ex) {
            // ignore
        }
    }
}

eventTestCompileEnd = { type ->

    def phasesToRun = [type.name]
    ConfigObject config = mergeConfig()
    if (config.optimize) {
        println "Clover: Test source compilation phase ended"

        def antInstrConfig = com.atlassian.clover.ant.tasks.AntInstrumentationConfig.getFrom(ant.project)
        def builder = com.atlassian.clover.api.optimization.OptimizationOptions.Builder.newInstance()
        def options = builder.enabled(true).
                debug(true).
                initString(antInstrConfig.initString).
                snapshot(defCloverSnapshotFile).build()

        println "Clover: Configuring test optimization with options " + options.toString()
        def optimizer = com.atlassian.clover.api.optimization.TestOptimizer.newInstance(options)

        // convert the testTargetPatterns into a list of optimizables...
        List optimizables = new ArrayList()

        // for each phase, gather source files and turn into optimizables
        phasesToRun.each {phaseName ->

            List<File> files = new LinkedList<File>()
            defStoredTestTargetPatterns.each { files.addAll(scanForSourceFiles(it, binding, phaseName.toString())) }

            files.each { optimizables << new FileOptimizable(it, new File("test", phaseName)) }

            if (config.verbose) {
                println("Clover: Tests to be optimized in ${phaseName} test phase: " + optimizables.toListString())
            }
        }


        List optimizedTests = optimizer.optimizeObjects(optimizables)

        final List/*<GrailsTestTargetPattern>*/ optimizedTestTargetPatterns = new LinkedList/*<GrailsTestTargetPattern>*/()
        optimizedTests.each {
            // String className = it.getName()
            final String className = (String)it.getClass().getMethod("getName").invoke(it)
            optimizedTestTargetPatterns << getGrailsTestTargetPatternClass().newInstance(createTestPattern(className))
        }

        println("Clover: Test Optimization selected " + optimizedTestTargetPatterns.size() + " out of " + optimizables.size() + " tests for execution")
        if (config.verbose) {
            println("Clover: Selected tests: " + optimizedTestTargetPatterns)
        }

        // Set variable read by _GrailsTest.groovy
        testTargetPatterns = optimizedTestTargetPatterns.toArray() /*as GrailsTestTargetPattern[]*/
    }
}

private String createTestPattern(String name) {
    return name.endsWith("Tests") ? name.substring(0, name.lastIndexOf("Tests")) : name
}

private List<File> scanForSourceFiles(Object/*GrailsTestTargetPattern*/ targetPattern, Binding binding, String phaseName) {
    def sourceFiles = []
    def resolveResources = binding['resolveResources']
    def testSuffixes = ['']
    def testExtensions = ["java", "groovy"]
    def sourceDir = new File("test", phaseName)

    testSuffixes.each { suffix ->
        testExtensions.each { extension ->
            def resources = resolveResources("file:${sourceDir.absolutePath}/${targetPattern.filePattern}${suffix}.${extension}".toString())
            sourceFiles.addAll(resources*.file.findAll { it.exists() }.toList())
        }
    }

    sourceFiles
}

eventTestPhasesEnd = {
    ConfigObject config = mergeConfig()
    if (!config.on && !config.optimize) {
        return
    }

    println "Clover: Tests ended. Generating reports"

    def historyDir = config.historydir ?: defCloverHistoryDir
    def reportLocation = config.reportdir ?: defCloverReportDir
    def reportStyle = config.reportStyle ?: defCloverReportStyle

    def historical = defCloverHistorical
    if (config.historical != null) {
        historical = config.historical
    }

    if (historical) {
        if (!config.historypointtask) {
            println "Clover: Generating history point using default 'clover-historypoint' task"
            ant.'clover-historypoint'(historyDir: historyDir)
        }
        else {
            println "Clover: Generating history point using custom 'config.historypointtask' closure"
            config.historypointtask(ant, binding)
        }
    }

    if (!config.reporttask) {
        println "Clover: Generating report using default 'clover-report' task"
        ant.'clover-report' {
            ant.current(outfile: reportLocation, title: config.title ?: defCloverReportTitle) {
                format(type: "html", reportStyle: reportStyle)
                ant.columns {
                    lineCount()
                    filteredElements()
                    uncoveredElements()
                    totalPercentageCovered()
                }
            }
            if (historical) {
                ant.historical(outfile: reportLocation, historyDir: historyDir)
            }
            ant.current(outfile: "${reportLocation}/clover.xml") {
                format(type: "xml")
            }
            if (config.json) {
                ant.current(outfile: reportLocation) {
                    format(type: "json")
                }
            }
        }


        if (config.view) {
            launchReport(reportLocation)
        }

    } else {
        // reporttask is a user defined closure that takes a single parameter that is a reference to the org.codehaus.gant.GantBuilder instance.
        // this closure can be used to generate a custom html report.
        // see : http://groovy.codehaus.org/Using+Ant+from+Groovy
        println "Clover: Generating report using custom 'config.reporttask' closure"
        config.reporttask(ant, binding, this)
    }

    // TODO: if -clover.optimize, save a snapshot file to -clover.snapshotLocation


    if (config.optimize) {
        println "Clover: Saving optimization snapshot"
        ant.'clover-snapshot'(file: defCloverSnapshotFile)
    }

}

/**
 * Tries to launch a HTML report in your browser.
 *
 * If only a single test was run, then just that test's page will be shown.
 * Otherwise, the dashboard page is displayed. This is useful if using IDEA/Eclipse to run grails tests.
 *
 * @param reportLocation the directory containing the report to launch
 * @return
 */
def launchReport(reportLocation) {
    File openFile = new File(reportLocation, "index.html")
    if (openFile.exists()) {
        // if there is a wildcard in the testname, we can't do anything...
        if (testNames) {
            String testName = testNames[0].replace((char) '.', File.separatorChar)
            String suffix = testName.toString().endsWith("Tests") ? "" : "Tests"
            File testFile = new File(reportLocation, testName + suffix + ".html")
            openFile = testFile.exists() ? testFile : openFile
        }

        String openLoc = openFile.toURI().toString()
        println "Clover: About to launch broswer: ${openLoc}"
        com.atlassian.clover.reporters.util.BrowserLaunch.openURL openLoc
    }
}

def toggleCloverOn(ConfigObject clover) {

    ant.taskdef(resource: 'cloverlib.xml')
    ant.'clover-env'()

    // create an AntInstrumentationConfig object, and set this on the ant project
    def antConfig = com.atlassian.clover.ant.tasks.AntInstrumentationConfig.newInstance(ant.project)
    configureAntInstr(clover, antConfig)
    antConfig.setIn(ant.project)

    if (clover.setuptask) {
        println "Clover: using custom clover-setup configuration."
        clover.setuptask(ant, binding, this)
    } else {
        println "Clover: using default clover-setup configuration."

        final String initString = clover.get("initstring") != null ? clover.initstring : "${projectWorkDir}/clover/db/clover.db"
        antConfig.initstring = initString

        def cloverSrcDirs = clover.srcDirs ? clover.srcDirs : this.defCloverSrcDirs
        def cloverIncludes = clover.includes ? clover.includes : this.defCloverIncludes
        def cloverExcludes = clover.excludes ? clover.excludes : this.defCloverExcludes

        println """Clover:
               directories: ${cloverSrcDirs}
               includes:    ${cloverIncludes}
               excludes     ${cloverExcludes}"""

        ant.'clover-setup'(initString: initString, tmpDir: "${projectWorkDir}/clover/tmp") {

            cloverSrcDirs.each {dir ->
                if (new File(dir.toString()).exists()) {
                    ant.fileset(dir: dir) {
                        cloverExcludes.each { exclude(name: it) }
                        cloverIncludes.each { include(name: it) }
                    }
                }
            }
        }
    }

    if (clover.snapshotLocation) {
        defCloverSnapshotFile = new File(clover.snapshotLocation)
    }

}

/**
 * Populates an AntInstrumentationConfig instance with any matching properties in the ConfigObject.
 * Currently only primitive boolean, int and long are supported. As are String.
 */
private configureAntInstr(ConfigObject clover, antConfig) {

    return clover.each {

        if (antConfig.getProperties().containsKey(it.key)) {

            String setter = MetaProperty.getSetterName(it.key)
            MetaProperty property = antConfig.metaClass.getMetaProperty(it.key.toString())

            final val
            switch (property.type) {
                case Integer.getPrimitiveClass("int"):
                    val = it.value.toInteger()
                    break
                case Long.getPrimitiveClass("long"):
                    val = it.value.toLong()
                    break
                case Boolean.getPrimitiveClass("boolean"):
                    val = (it.value == null || Boolean.parseBoolean(it.value.toString()))
                    break
                case File:
                    val = new File(it.value.toString())
                    break
                default:
                    val = it.value
            }

            antConfig.invokeMethod(setter, val)
        }
    }
}

/**
 * Sets ant logging level to MSG_DEBUG or MSG_VERBOSE depending whether
 * clover.debug=true or clover.verbose=true properties are set.
 * @param clover
 */
private void toggleAntLogging(ConfigObject clover) {
    // get any BuildListeners and turn logging on
    if (clover.debug) {
        ant.project.buildListeners.each {listener ->
            if (listener instanceof BuildLogger) {
                listener.messageOutputLevel = Project.MSG_DEBUG
            }
        }
        println "Clover: Ant task logging level set to DEBUG"

    } else if (clover.verbose) {
        ant.project.buildListeners.each {listener ->
            if (listener instanceof BuildLogger) {
                listener.messageOutputLevel = Project.MSG_VERBOSE
            }
        }
        println "Clover: Ant task logging level set to VERBOSE"
    }
}

/**
 * Takes any CLI arguments and merges them with any configuration defined in BuildConfig.groovy in the clover block.
 */
private ConfigObject mergeConfig() {

    final Map argsMap = parseArguments()
    final ConfigObject config = buildConfig.clover == null ? new ConfigObject() : buildConfig.clover

    final ConfigSlurper slurper = new ConfigSlurper()
    final Properties props = new Properties()
    props.putAll(argsMap)

    final ConfigObject argsMapConfig = slurper.parse(props)
    config.merge(argsMapConfig.clover)

    return config
}

// Copied from _GrailsArgParsing.groovy since _GrailsCompile.groovy does not depend on parseArguments target
// and the argsMap is not populated in time for the testStart event.
// see: http://jira.codehaus.org/browse/GRAILS-2663

private Map parseArguments() {
    // Only ever parse the arguments once. We also don't bother parsing
    // the arguments if the "args" string is empty.
//    if (argsMap.size() > 1 || argsMap["params"] || !args) return
    argsMap = [params: []]

    args?.tokenize().each { token ->
        def nameValueSwitch = token =~ "--?(.*)=(.*)"
        if (nameValueSwitch.matches()) { // this token is a name/value pair (ex: --foo=bar or -z=qux)
            final value = nameValueSwitch[0][2]
            argsMap[nameValueSwitch[0][1]] = "false".equalsIgnoreCase(value) ? false : value
        }
        else {
            def nameOnlySwitch = token =~ "--?(.*)"
            if (nameOnlySwitch.matches()) {  // this token is just a switch (ex: -force or --help)
                argsMap[nameOnlySwitch[0][1]] = true
            }
            else { // single item tokens, append in order to an array of params
                argsMap["params"] << token
            }
        }
    }

    if (argsMap.containsKey('non-interactive')) {
        println "Clover: Setting non-interactive mode"
        isInteractive = !(argsMap.'non-interactive')
    }
    return argsMap
}

/**
 * Initialize Clover stuff as soon as plugin is installed. We don't do this in _Install.groovy script,
 * because we need access to project configuration object as well as global variables from _Events.groovy.
 */
eventPluginInstalled = { fullPluginName ->
    if (((String) fullPluginName).startsWith("clover-")) {
        println "Clover: Plugin was installed, loading new Ant task definitions ..."
        // Call event callback internally in order to load clover task definitions,
        // toggle on Clover, set global variables like source paths etc
        eventSetClasspath(null)
    }
}
