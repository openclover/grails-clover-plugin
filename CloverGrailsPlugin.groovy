class CloverGrailsPlugin
{
  def version = "4.4.0"
  def grailsVersion = "2.0.* > *"
  def pluginExcludes = [
          "samples",
          "test",
          "etc/clover.license",
          "testcases",
          "docs"
  ]

  def environments = ['test']

  def author = "OpenClover.org"
  def authorEmail = "support@openclover.org"
  def title = "OpenClover Code Coverage for Grails 2"
  def description = '''\
A plugin that integrates OpenClover into Grails 2 projects.
OpenClover is a code coverage tool that produces beautiful reports which are highly configurable and extensible.
OpenClover not only reports which lines of code were executed during a test run, it also reports which tests hit which lines of code.
OpenClover's reports include:
* Tag Clouds of your entire project that compare Complexity with Coverage
* A TreeMap to visualise the most complex, uncovered areas of your project
* Fully Cross Referenced HTML view of your source code
* Much more: See http://openclover.org
'''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/plugins/clover"
}
