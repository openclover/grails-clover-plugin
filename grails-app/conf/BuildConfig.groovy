grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"

clover {
    debug = false;
    on = false;
    core.version = 'com.cenqua.clover:clover:3.1.5';
    license.path = "clover.license"
}

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails' default dependencies
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'

    repositories {
        grailsPlugins() // plugins should first look in their lib dir
        grailsHome()    // next in $GRAILS_HOME/lib
        grailsCentral() // next in Grails Central (SVN + Maven)

        mavenLocal()    // otherwise look in the local maven repo
        mavenRepo "https://maven.atlassian.com/content/groups/public/"
        mavenRepo "https://maven.atlassian.com/private-snapshot/"
        mavenCentral()  // and finally in the Maven Central
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes
         build(clover.core.version, changing:false)
         compile(clover.core.version, changing:false)
         runtime(clover.core.version, changing:false)
    }

    plugins {
         build ":maven-publisher:0.8.1"
    }
}
