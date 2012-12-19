grails.project.dependency.resolution = {
    inherits "global"
    log      "warn"

    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
        grailsRepo "http://plugins.grails.org"
        mavenLocal()
        mavenRepo "https://maven.atlassian.com/public-snapshot"
        mavenRepo "https://maven.atlassian.com/public"
        mavenCentral()
    }

    plugins {
        compile ":clover:3.1.9"
        compile ":tomcat:$grailsVersion"
        compile ":hibernate:$grailsVersion"
    }
}

clover {
    license.path="$userHome/clover.license"
}