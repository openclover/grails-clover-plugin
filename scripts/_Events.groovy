import org.apache.tools.ant.BuildLogger
import org.apache.tools.ant.Project

eventCreatedArtefact = {type, name ->
  println "Created $type $name"
}

eventStatusUpdate = {msg ->
  println msg
}

eventStatusFinal = {msg ->
  println msg
}

eventCompileStart = {kind ->
  println "COMPILE START $kind.ant.project"
  println "Build Config ${buildConfig}"
  println "Clover Config ${buildConfig.clover.includes.toString()}"
}

eventCompileEnd  = {kind ->
  println "COMPILE END $kind"
}

eventSetClasspath = {URLClassLoader rootLoader ->
  println "SET CLASSPATH: $rootLoader"
  debug = false;
  
  File cloverJar = new File("/Users/niick/clover.jar")
  rootLoader.addURL(new File("/Users/niick/grover.jar").toURL());
//  rootLoader.addURL(cloverJar.toURL());
//  Class clazz = rootLoader.findClass("grover.GroverASTTransformer")
//  println "Found Grover Class: ${clazz}"


  // get any BuildListeners and turn logging on

  if (debug) {
    ant.project.buildListeners.each {listener ->
      if (listener instanceof BuildLogger) {
        listener.messageOutputLevel = Project.MSG_DEBUG
      }
    }
  }

  ant.taskdef(resource:'cloverlib.xml', classpath:cloverJar)
  ant.'clover-env'()
  ant.'clover-setup'()
  
}