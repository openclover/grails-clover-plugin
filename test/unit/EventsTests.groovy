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

  public void setUp()
  {
    ant = new GantBuilder()
    binding = new Binding([projectWorkDir: testRunTmpDir,
            basedir: testRunTmpDir,
            metadata: [],
            ant: ant,
            userHome: testRunTmpDir,
            grailsWorkDir: testRunTmpDir,
            projectTargetDir: testRunTmpDir,
            cloverPluginDir:new File(".")
    ]);
    GroovyScriptEngine shell = new GroovyScriptEngine("./scripts/_Events.groovy");
    script = shell.run("_Events.groovy", binding);
  }

  public void testToggleAntLogging() throws Exception
  {
    ConfigObject config = new ConfigObject()
    config.debug = true
    int outputLevel = 0; // this gets modified by toggleAntLogging
    def logger = [setMessageOutputLevel: {outputLevel = it}] as DefaultLogger
    ant.project.addBuildListener(logger)
    script.toggleAntLogging config
    println outputLevel
    assertTrue outputLevel == Project.MSG_DEBUG
  }

  public void testConfigureUsingEvalLicense()
  {
    ConfigObject config = new ConfigObject()
    String origLicenseLoc = System.properties.remove('clover.license.path')
    try
    {
      script.configureLicense(config)
      assertNotNull System.getProperty('clover.license.path')
    }
    finally
    {
      if (origLicenseLoc)
      {
        System.setProperty('clover.license.path', origLicenseLoc)
      }
    }
  }

  public void testConfigureLicense()
  {
    ConfigObject config = new ConfigObject()
    String origLicenseLoc = System.properties.remove('clover.license.path')
    try
    {
      config.license.path = "/path/to/clover.license"
      script.configureLicense(config)
      assertEquals config.license.path, System.getProperty('clover.license.path')
    }
    finally
    {
      if (origLicenseLoc)
      {
        System.setProperty('clover.license.path', origLicenseLoc)
      }
    }
  }


}
