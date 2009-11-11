import org.apache.tools.ant.BuildLogger
import org.apache.tools.ant.Project
import groovy.util.ConfigObject
import com.cenqua.clover.tasks.AntInstrumentationConfig



srcDirs = ["src", "grails-app"];
includes = ["**/*.groovy", "**/*.java"];
excludes = ["conf/**", "**/plugins/**"];

eventStatusFinal = {msg ->
  // TODO: generate a Clover report here
  println msg
}

eventCompileStart = {kind ->
  // Ants Project is available via: kind.ant.project
  println "Compile start."
}


/**
 * Takes any CLI arguments and merges them with any configuration defined in BuildConfig.groovy in the clover block.
 */
ConfigObject mergeConfig() {

  final Map argsMap = parseArguments()
  final ConfigObject config = buildConfig.clover == null ? new ConfigObject() : buildConfig.clover

  final ConfigSlurper slurper = new ConfigSlurper()
  final Properties props = new Properties()
  props.putAll(argsMap)

  final ConfigObject argsMapConfig = slurper.parse(props)
  config.merge(argsMapConfig.clover)

  return config

}

eventSetClasspath = {URLClassLoader rootLoader ->

  println "Clover plugin base dir: ${cloverPluginDir}"

  ConfigObject config = mergeConfig()
  println "Using Clover Config: ${config}"

  toggleAntLogging(config)

  if (config.enabled) {
    toggleCloverOn(config)
  }

}

private def toggleCloverOn(ConfigObject clover) {


  configureLicense(clover)
  
  ant.taskdef(resource: 'cloverlib.xml')
  ant.'clover-env'()
  ant.'clover-setup'()

  // create an AntInstrumentationConfig object, and set this on the ant project
  AntInstrumentationConfig antConfig = new AntInstrumentationConfig(ant.project)

  // configure any filesets, patternsets ,testsources
  if (clover.srcDirs) {println "Clover srcDirs is enabled!"}
  
  println "Clover fileset: ${clover.srcDirs}"

  if (clover.srcDirs) {
    srcDirs.addAll(clover.srcDirs)
  }

  if (clover.includes) {
    includes.addAll(clover.includes)
  }

  if (clover.excludes) {
    excludes.addAll(clover.excludes)
  }
  
  srcDirs.each {dir ->
  
    antConfig.addFileset ant.fileset(dir: dir) {
      println "FileSet for Dir: ${dir}"
      excludes.each {
        println "Adding exclude: ${it}"
        exclude(name: it)
      }
      includes.each {
        println "Adding include: ${it}"
        include(name: it)
      }
    }
    
  }

  println "Filesets: ${antConfig.getInstrFilesets()}"


  configureAntInstr(clover, antConfig)
  antConfig.setIn ant.project
  

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
      argsMap[nameValueSwitch[0][1]] = nameValueSwitch[0][2]
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