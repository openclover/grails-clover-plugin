import org.apache.tools.ant.BuildLogger
import org.apache.tools.ant.Project

// some clover defaults
defCloverSrcDirs = ["src/java", "src/groovy", "test", "grails-app"];
defCloverIncludes = ["**/*.groovy", "**/*.java"];
defCloverExcludes = ["**/conf/**", "**/plugins/**"];
defCloverReportDir = "build/clover/report" // flim-flamming between projectWorkDir and build. build is consistent
defCloverHistoryDir = "${basedir}/.cloverhistory"
defCloverReportTitle = metadata["app.name"]

// HACK to work-around: http://jira.codehaus.org/browse/GRAILS-5755
loadDependencyClass = {name ->
  def doLoad = { -> classLoader.loadClass(name) }
  try {
    doLoad()
  } catch (ClassNotFoundException e) {
    includeTargets << grailsScript("_GrailsCompile")
    compile()
    doLoad()
  }
}


eventCompileStart = {kind ->
  ConfigObject config = mergeConfig()
  // Ants Project is available via: kind.ant.project
  println "Clover: Compile start."
  System.setProperty "grover.ast.dump", "" + config.dumpAST
}

eventSetClasspath = {URLClassLoader rootLoader ->

//  grailsSettings.compileDependencies.each { println it }

  ConfigObject config = mergeConfig()
  println "Clover: Using config: ${config}"

  if (config.debug) {
    println "Clover: Dumping binding variables:"
    binding.variables.each { println it.key + " = " + it.value } // dumps all available vars and their values
  }

  toggleAntLogging(config)

  if (config.on)
  {

    toggleCloverOn(config)

    if (!config.containsKey('forceClean') || config.forceClean)
    {
      // force a clean
      def webInf = "${basedir}/web-app/WEB-INF"
      def cleanDirs = ["${webInf}/classes", "${webInf}/lib", "${projectWorkDir}/gspcompile", classesDirPath, testDirPath, "${projectWorkDir}/clover"]

      println "Clover: Forcing a clean to ensure Clover instrumentation occurs. Disable by setting: clover.forceClean=false "
      cleanDirs.each {ant.delete(dir: it, failonerror: false)}

    }
  }
}

// copied from $GRAILS_HOME/scripts/_GrailsClean.groovy

private def cleanCompiledSources()
{

}

eventStatusFinal = {msg ->

}

eventTestPhasesStart = {

}

eventTestPhasesEnd = {
  ConfigObject config = mergeConfig()
  println "Clover: Tests ended"

  if (!config.on)
  {
    return;
  }

  def historyDir = config.historydir ?: defCloverHistoryDir
  def reportLocation = config.reportdir ?: defCloverReportDir

  if (!config.historypointtask)
  {
    ant.'clover-historypoint'(historyDir: historyDir)
  }
  else
  {
    config.historypointtask(ant, binding)
  }

  if (!config.reporttask)
  {
    // generate a report
    ant.'clover-html-report'(outdir: reportLocation,
            historyDir: historyDir,
            title: config.title ?: defCloverReportTitle)

    if (config.view) {
      launchReport(reportLocation)
    }
  }
  else
  {
    // reporttask is a user defined closure that takes a single parameter that is a reference to the org.codehaus.gant.GantBuilder instance.
    // this closure can be used to generate a custom html report.
    // see : http://groovy.codehaus.org/Using+Ant+from+Groovy
    config.reporttask(ant, binding, this)
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
public def launchReport(def reportLocation )
{
  File openFile = new File(reportLocation, "index.html")
  if (openFile.exists())
  {
    if (testNames.size() > 0) // if there is a wildcard in the testname, we can't do anything...
    {
      StringBuffer testName = new StringBuffer()
      testNames[0].split("\\.").each { testName.append(it).append(File.separator) }
      if (testName.length() > 1)  testName.deleteCharAt(testName.length() - 1)
      String suffix = testName.toString().endsWith("Tests") ? "" : "Tests"
      File testFile = new File(reportLocation, testName + suffix + ".html")
      openFile = testFile.exists() ? testFile : openFile
    }

    String openLoc = openFile.toURI().toString()
    println "About to launch: ${openLoc}"
    com.cenqua.clover.reporters.util.BrowserLaunch.openURL openLoc;
  }
}

def toggleCloverOn(ConfigObject clover)
{

  configureLicense(clover)

  ant.taskdef(resource: 'cloverlib.xml')
  ant.'clover-env'()

  // create an AntInstrumentationConfig object, and set this on the ant project
  def antInstrConfClass = loadDependencyClass('com.cenqua.clover.tasks.AntInstrumentationConfig')

  def antConfig = antInstrConfClass.newInstance(ant.project)
  configureAntInstr(clover, antConfig)
  antConfig.setIn ant.project

  if (clover.setuptask)
  {
    println "Using custom clover-setup configuration."

    clover.setuptask(ant, binding, this)
  }
  else
  {
    println "Using default clover-setup configuration."

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
        ant.fileset(dir: dir) {
          cloverExcludes.each { exclude(name: it) }
          cloverIncludes.each { include(name: it) }
        }
      }
    }

  }
}

/**
 * Takes any CLI arguments and merges them with any configuration defined in BuildConfig.groovy in the clover block.
 */
private def ConfigObject mergeConfig()
{

  final Map argsMap = parseArguments()
  final ConfigObject config = buildConfig.clover == null ? new ConfigObject() : buildConfig.clover

  final ConfigSlurper slurper = new ConfigSlurper()
  final Properties props = new Properties()
  props.putAll(argsMap)

  final ConfigObject argsMapConfig = slurper.parse(props)
  config.merge(argsMapConfig.clover)

  return config

}

/**
 * Populates an AntInstrumentationConfig instance with any matching properties in the ConfigObject.
 *
 * Currently only primitive boolean, int and long are supported.
 * As are String.
 *
 */
private def configureAntInstr(ConfigObject clover, def antConfig)
{

  return clover.each {

    if (antConfig.getProperties().containsKey(it.key))
    {

      String setter = MetaProperty.getSetterName(it.key)
      MetaProperty property = antConfig.metaClass.getMetaProperty(it.key.toString())

      final def val;
      switch (property.type)
      {
        case Integer.class.getPrimitiveClass("int"):
          val = it.value.toInteger()
          break;
        case Long.class.getPrimitiveClass("long"):
          val = it.value.toLong()
          break;
        case Boolean.class.getPrimitiveClass("boolean"):
          val = (it.value == null || Boolean.parseBoolean(it.value.toString()))
          break;
        case File.class:
          val = new File(it.value.toString())
          break;
        default:
          val = it.value
      }

      antConfig.invokeMethod(setter, val)
    }
  }
}

private def configureLicense(ConfigObject clover)
{
// the directories to search for a clover.license file
  final String[] licenseSearchPaths = ["${userHome}", "${basedir}", "${basedir}/etc", "${grailsWorkDir}"]

  // the name of the system property that holds the clover license file
  final LICENSE_PROP = 'clover.license.path'

  final license;
  if (clover.license.path)
  {
    license = clover.license.path
  }
  else
  {

    licenseSearchPaths.each {
      final String licensePath = "${it}/clover.license"
      if (new File(licensePath).exists())
      {
        license = licensePath;
        return;
      }
    }
  }

  // check for a bundled eval clover license
  final File evalLicense = new File(cloverPluginDir, "grails-app/conf/clover/clover-evaluation.license")
  if (!license && evalLicense.exists()) {
    license = evalLicense.getAbsolutePath()
  }

  if (!license)
  {
    println """
               No clover.license configured. Please define license.path=/path/to/clover.license in the
               clover configuration in conf/BuildConfig.groovy"""
  }
  else
  {
    System.setProperty LICENSE_PROP, license
    println "Using clover license path: ${System.getProperty LICENSE_PROP}"
  }
}

private void toggleAntLogging(ConfigObject clover)
{
// get any BuildListeners and turn logging on
  if (clover.debug)
  {
    ant.project.buildListeners.each {listener ->
      if (listener instanceof BuildLogger)
      {
        listener.messageOutputLevel = Project.MSG_DEBUG
      }
    }
  }
}

// Copied from _GrailsArgParsing.groovy since _GrailsCompile.groovy does not depend on parseArguments target
// and the argsMap is not populated in time for the testStart event.
// see: http://jira.codehaus.org/browse/GRAILS-2663

private Map parseArguments()
{
  // Only ever parse the arguments once. We also don't bother parsing
  // the arguments if the "args" string is empty.
//    if (argsMap.size() > 1 || argsMap["params"] || !args) return
  argsMap = [params: []]

  args?.tokenize().each {token ->
    def nameValueSwitch = token =~ "--?(.*)=(.*)"
    if (nameValueSwitch.matches())
    { // this token is a name/value pair (ex: --foo=bar or -z=qux)
      final def value = nameValueSwitch[0][2]
      argsMap[nameValueSwitch[0][1]] = "false".equalsIgnoreCase(value) ? false : value;
    }
    else
    {
      def nameOnlySwitch = token =~ "--?(.*)"
      if (nameOnlySwitch.matches())
      {  // this token is just a switch (ex: -force or --help)
        argsMap[nameOnlySwitch[0][1]] = true
      }
      else
      { // single item tokens, append in order to an array of params
        argsMap["params"] << token
      }
    }
  }

  if (argsMap.containsKey('non-interactive'))
  {
    println "Setting non-interactive mode"
    isInteractive = !(argsMap.'non-interactive')
  }
  return argsMap
}