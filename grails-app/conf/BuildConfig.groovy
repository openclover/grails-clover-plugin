grails.project.work.dir="build"
grails.project.test.reports.dir="build/test"



clover {
  debug = false;
  enabled = false;
  core.version = 'com.cenqua.clover:clover:3.0.0-SNAPSHOT';
  license.path = "clover.license"

  defaultIncludes = ["a", "b", "c"];

}

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies  
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {

        grailsPlugins() // plugins should first look in their lib dir
        mavenLocal() // otherwise look in the local maven repo
        mavenRepo "https://maven.atlassian.com/content/groups/public/"

    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes
         runtime(clover.core.version)
    }
}