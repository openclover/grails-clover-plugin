import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.GroovyTestCase;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import org.codehaus.gant.GantBuilder;
import org.apache.tools.ant.DefaultLogger
import org.apache.tools.ant.Project;

/**
 */
public class EventsTests extends GroovyTestCase
{

  String projectDir = System.getProperty("project.dir")
  String testRunTmpDir = System.getProperty("testrun.tmpdir")
  Binding binding
  GantBuilder ant
  Object script
  
  public void setUp() {
    ant = new GantBuilder()
    binding = new Binding([projectWorkDir: testRunTmpDir,
                           basedir: testRunTmpDir,
                           metadata: [],
                           ant: ant,
                           userHome: testRunTmpDir,
                           grailsWorkDir: testRunTmpDir
                            ]);
    GroovyScriptEngine shell = new GroovyScriptEngine("grails/scripts/_Events.groovy");
    script = shell.run("_Events.groovy", binding);
  }

  public void testToggleAntLogging() throws Exception
  {
    ConfigObject config = new ConfigObject()
    config.debug = true
    int outputLevel = 0; // this gets modified by toggleAntLogging
    def logger =  [setMessageOutputLevel: {outputLevel = it}] as DefaultLogger
    ant.project.addBuildListener(logger)
    script.toggleAntLogging config
    println outputLevel
    assertTrue outputLevel == Project.MSG_DEBUG
  }

  public void testConfigureMissingLicense()
  {
    ConfigObject config = new ConfigObject()
    String origLicenseLoc =  System.properties.remove('clover.license.path')
    try
    {
      script.configureLicense(config)
      assertNull System.getProperty('clover.license.path')
    }
    finally
    {
      System.setProperty('clover.license.path', origLicenseLoc)
    }
  }



}
