import org.apache.tools.ant.BuildLogger
import org.apache.tools.ant.Project

import com.cenqua.clover.tasks.AntInstrumentationConfig



srcDirs = ["src/java", "src/groovy", "test",
           "grails-app/controllers", "grails-app/domain", "grails-app/services", "grails-app/utils", "grails-app/taglib"];

includes = ["**/*.groovy", "**/*.java"];
excludes = ["**/conf/**", "**/plugins/**"];


eventCompileStart = {kind ->
  ConfigObject config = mergeConfig()
  // Ants Project is available via: kind.ant.project
  println "Compile start."

//  binding.variables.each { println "${it.key} ${it.value}"} // dumps all available vars and their values

  System.setProperty "grover.ast.dump", "" + config.dumpAST

}

eventSetClasspath = {URLClassLoader rootLoader ->

  println "Clover plugin base dir: ${cloverPluginDir}"

//  grailsSettings.compileDependencies.each { println it }
  
  ConfigObject config = mergeConfig()
  println "Using Clover Config: ${config}"

  toggleAntLogging(config)

  if (config.enabled) {

    toggleCloverOn(config)

    if (!config.preserve) {
          // force a clean
          println "Forcing a clean"

          def webInf = "${basedir}/web-app/WEB-INF"
          ant.delete(dir:"${webInf}/classes")
          ant.delete(file:webXmlFile.absolutePath, failonerror:false)
          ant.delete(dir:"${projectWorkDir}/gspcompile", failonerror:false)
          ant.delete(dir:"${webInf}/lib")
          ant.delete(dir:"${basedir}/web-app/plugins")
          ant.delete(dir:classesDirPath)
          ant.delete(dir:resourcesDirPath)
          ant.delete(dir:testDirPath)
          ant.delete(dir:"${projectWorkDir}/clover")
    }
  }
}

// copied from $GRAILS_HOME/scripts/_GrailsClean.groovy
private def cleanCompiledSources() {

}

eventStatusFinal = {msg ->

}

eventTestPhasesStart = {

}

eventTestPhasesEnd = {
  ConfigObject config = mergeConfig()
  println "Tests ended"

  if (!config.enabled) {
    return;
  }

  // TODO: save a history point?

  if (!config.reporttask) {
    // generate a report
    ant.'clover-html-report'(outdir:config.reportdir ?: "build/clover/report")
  } else {

    // reporttask is a user defined closure that takes a single parameter that is a reference to the org.codehaus.gant.GantBuilder instance.
    // this closure can be used to generate a custom html report.
    // see : http://groovy.codehaus.org/Using+Ant+from+Groovy
    config.reporttask(ant, binding)
  }

}

private def toggleCloverOn(ConfigObject clover) {

  configureLicense(clover)
  
  ant.taskdef(resource: 'cloverlib.xml')
  ant.'clover-env'()

  // create an AntInstrumentationConfig object, and set this on the ant project
  AntInstrumentationConfig antConfig = new AntInstrumentationConfig(ant.project)
  configureAntInstr(clover, antConfig)
  antConfig.setIn ant.project

  if (clover.setuptask) {
    println "Using custom clover-setup configuration."

    clover.setuptask(ant, binding)
  } else {
    println "Using default clover-setup configuration."

    final String initString = clover.get("initstring") != null ? clover.initstring : "${projectWorkDir}/clover/db/clover.db"
    antConfig.initstring = initString
    ant.'clover-setup'()

    ant.'clover-setup'(initString: initString, tmpDir: "${projectWorkDir}/clover/tmp") {

        srcDirs.each {dir ->
          ant.fileset(dir: dir) {
            excludes.each { exclude(name: it) }
            includes.each { include(name: it) }
          }
          println "DIR: ${dir} added includes: ${includes}, excludes ${excludes}"
        }


        ant.fileset(dir:'grails-app', includes:'**/*.groovy', excludes: '**/conf/**, **/plugins/**')
        ant.fileset(dir:'src', includes:'**/*.groovy, **/*.java')
    }
    
  }
}

/**
 * Takes any CLI arguments and merges them with any configuration defined in BuildConfig.groovy in the clover block.
 */
private def ConfigObject mergeConfig() {

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
private def configureAntInstr(ConfigObject clover, AntInstrumentationConfig antConfig) {

   return clover.each {

    if (antConfig.getProperties().containsKey(it.key)) {

      String setter = MetaProperty.getSetterName(it.key)
      MetaProperty property = antConfig.metaClass.getMetaProperty(it.key.toString())

      final def val;
      switch (property.type) {
        case Integer.class.getPrimitiveClass("int"):
          val = it.value.toInteger()
          break;
        case Long.class.getPrimitiveClass("long"):
          val = it.value.toLong()
          break;
        case Boolean.class.getPrimitiveClass("boolean"):
          val = (it.value == null || Boolean.parseBoolean(it.value.toString()))
          break;
        default:
          val = it.value
      }
      
      antConfig.invokeMethod(setter, val)
    }
  }
}

private def configureLicense(ConfigObject clover) {
// the directories to search for a clover.license file
  final String[] licenseSearchPaths = ["${userHome}", "${basedir}", "${basedir}/etc", "${grailsWorkDir}"]

  // the name of the system property that holds the clover license file
  final LICENSE_PROP = 'clover.license.path'

  final license;
  if (clover.license.path) {
    license = clover.license.path
  } else {

    licenseSearchPaths.each {
      final String licensePath = "${it}/clover.license"
      if (new File(licensePath).exists()) {
        license = licensePath;
        return;
      }
    }
  }

  if (!license) {
    println """No clover.license configured. Please define license.path=/path/to/clover.license in the
               clover configuration in conf/BuildConfig.groovy"""
  } else {
    System.setProperty LICENSE_PROP, license
    println "Using clover license path: ${System.getProperty LICENSE_PROP}"
  }
}

private void toggleAntLogging(ConfigObject clover) {
// get any BuildListeners and turn logging on
  if (clover.debug) {
    ant.project.buildListeners.each {listener ->
      if (listener instanceof BuildLogger) {
        listener.messageOutputLevel = Project.MSG_DEBUG
      }
    }
  }
}

// Copied from _GrailsArgParsing.groovy since _GrailsCompile.groovy does not depend on parseArguments target
// and the argsMap is not populated in time for the testStart event.
// see: http://jira.codehaus.org/browse/GRAILS-2663
private Map parseArguments() {
  // Only ever parse the arguments once. We also don't bother parsing
  // the arguments if the "args" string is empty.
//    if (argsMap.size() > 1 || argsMap["params"] || !args) return
  argsMap = [params: []]

  args?.tokenize().each {token ->
    def nameValueSwitch = token =~ "--?(.*)=(.*)"
    if (nameValueSwitch.matches()) { // this token is a name/value pair (ex: --foo=bar or -z=qux)
      final def value = nameValueSwitch[0][2]
      argsMap[nameValueSwitch[0][1]] = "false".equalsIgnoreCase(value) ? false : value; 
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
    println "Setting non-interactive mode"
    isInteractive = !(argsMap.'non-interactive')
  }
  return argsMap
}