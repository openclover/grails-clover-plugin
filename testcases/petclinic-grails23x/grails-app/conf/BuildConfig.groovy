grails.project.dependency.resolution = {
    inherits "global"
    log      "warn"

    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
        grailsRepo "http://plugins.grails.org"
        mavenLocal()
        mavenRepo "https://maven.atlassian.com/repository/public"
        mavenRepo "https://maven.atlassian.com/content/repositories/atlassian-public-snapshot"
        mavenCentral()
    }

    plugins {
        compile ":clover:4.0.0"
        compile ":tomcat:7.0.54"
        compile ":hibernate:3.6.10.16"
    }

    dependencies {
        compile "com.atlassian.clover:clover:4.0.0"
    }

}

clover {
    license.path="$userHome/clover.license"
    instrumentLambda = "block"
    reportStyle = "adg"
}