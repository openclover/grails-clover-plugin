grails.project.work.dir="build"
grails.project.test.reports.dir="build/test"

clover {
  debug = false;
  on = false;
  core.version = 'com.cenqua.clover:clover:3.0.0-m5';
  license.path = "clover.license"
}

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies  
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {

        grailsPlugins() // plugins should first look in their lib dir
        mavenLocal() // otherwise look in the local maven repo
        mavenRepo "https://maven.atlassian.com/content/groups/public/"
        mavenRepo "https://maven.atlassian.com/private-snapshot/"

    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes
         build(clover.core.version, changing:true)
         compile(clover.core.version, changing:true)
         runtime(clover.core.version, changing:true)
    }
}