clover {
  debug = false;
  enabled = false;
  core.version = 'com.cenqua.clover:clover:2.6.3-SNAPSHOT';
  license.path = "clover.license"
}

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"



grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies  
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {
        grailsHome()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        mavenLocal() 
        mavenCentral()
        mavenRepo "https://maven.atlassian.com/private-snapshot"

        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
         build clover.core.version
         runtime clover.core.version
    }

}