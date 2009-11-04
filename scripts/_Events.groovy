import org.apache.tools.ant.BuildLogger
import org.apache.tools.ant.Project

eventStatusFinal = {msg ->
  // TODO: generate a Clover report here
  println msg
}

eventCompileStart = {kind ->
  // Ants Project is available via: kind.ant.project
  println "Compile start."
}

eventSetClasspath = {URLClassLoader rootLoader ->

  println "Clover plugin base dir: ${cloverPluginDir}"
  argsMap = parseArguments()

  // get any BuildListeners and turn logging on
  if (argsMap.debug || buildConfig.clover.debug) {
    ant.project.buildListeners.each {listener ->
      if (listener instanceof BuildLogger) {
        listener.messageOutputLevel = Project.MSG_DEBUG
      }
    }
  }

  LICENSE_PROP = 'clover.license.path'

  if (buildConfig.clover.license.path) {
    System.setProperty LICENSE_PROP, buildConfig.clover.license.path
  } else {
    // TODO: by default, look in this directory, then look in the GRAILS_HOME/clover.license, then warn?
    println """No clover.license configured. Please define license.path=/path/to/clover.license in the
               clover configuration in conf/BuildConfig.groovy"""
  }

  println "Using clover license path: ${System.getProperty LICENSE_PROP}"


  if (argsMap.enabled || buildConfig.clover.enabled) {

    System.setProperty LICENSE_PROP, buildConfig.clover.license.path

    ant.taskdef(resource: 'cloverlib.xml')
    ant.'clover-env'()
    ant.'clover-setup'()
  }

}

// Copied from _GrailsArgParsing.groovy since _GrailsCompile.groovy does not depend on parseArguments target
// and the argsMap is not populated in time for the testStart event.
// see: http://jira.codehaus.org/browse/GRAILS-2663
def parseArguments() {
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