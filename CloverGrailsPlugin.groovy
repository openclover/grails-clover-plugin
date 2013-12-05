class CloverGrailsPlugin
{
  // the plugin version
  def version = "3.3.0"
  // the version or versions of Grails the plugin is designed for
  def grailsVersion = "2.0.* > *"
  // the other plugins this plugin depends on
  def dependsOn = [:]
  // resources that are excluded from plugin packaging
  def pluginExcludes = [
          "grails-app/views/error.gsp",
          "samples",
          "test",
          "etc/clover.license",
          "testcases",
          "docs"
  ]

  def environments = ['test']

  def author = "Atlassian"
  def authorEmail = "support@atlassian.com"
  def title = "Clover Code Coverage for Grails 2.x"
  def description = '''\\
A plugin that integrates Clover into GRAILS 2.x projects.
Clover is a Code Coverage tool that produces beautiful reports which are highly configurable and extensible.
Clover not only reports which lines of code were executed during a test run, it also reports which tests hit which lines of code.
Clover's reports include:
* Tag Clouds of your entire project that compare Complexity with Coverage
* A TreeMap to visualise the most complex, uncovered areas of your project
* Fully Cross Referenced HTML view of your source code
* Much more: See http://atlassian.com/clover

Looking for plugin for Grails 1.x? See http://grails.org/plugins/clover
'''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/plugins/clover"

  def doWithSpring = {

  }

  def doWithApplicationContext = { applicationContext ->

  }

  def doWithWebDescriptor = { xml ->

  }

  def doWithDynamicMethods = { ctx ->

  }

  def onChange = { event ->
  }

  def onConfigChange = { event ->

  }
}
