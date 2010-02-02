
clover {
  debug = false;
  enabled = false;
  core.version = 'com.cenqua.clover:clover:3.0.0-SNAPSHOT';
//  license.path = "clover.license"

  srcDirs = []
  includes = []
  excludes = []

}

grails.project.work.dir="build"
grails.project.test.reports.dir="build/test"


grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies  
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {

        grailsPlugins() // plugins should first look in their lib dir
        flatDir name:'clover-ant', dirs:'../build/clover-ant' // when building this plugin, look in build/clover-ant
        mavenLocal() // otherwise look in the local maven repo
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes
         build clover.core.version
         runtime clover.core.version
    }
    credentials {
          realm = 'maven.atlassian.com'
          host = 'maven.atlassian.com'
          username = '' // TODO: have these passed from the command line somehow
          password = ''
    }

}