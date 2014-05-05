grails.project.work.dir = "target"

clover {
    debug = false
    on = false
    core.version = "com.cenqua.clover:clover:4.0.0-SNAPSHOT"
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
        mavenRepo "https://maven.atlassian.com/public/"
        mavenRepo "https://maven.atlassian.com/public-snapshot/"
        mavenCentral()  // and finally in the Maven Central
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes
        build(clover.core.version)
        compile(clover.core.version)
        runtime(clover.core.version)
    }

}
