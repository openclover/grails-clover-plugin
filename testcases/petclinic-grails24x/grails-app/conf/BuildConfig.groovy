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
        mavenRepo "https://maven.atlassian.com/content/repositories/atlassian-public-snapshot/"
        mavenCentral()
    }

    plugins {
        compile ":clover:4.0.4"
        build   ":tomcat:7.0.54"
        runtime ":hibernate4:4.3.5.5"
    }

    dependencies {
        test "org.hamcrest:hamcrest-core:1.3"
        compile "com.atlassian.clover:clover:4.0.4-SNAPSHOT"
    }

}

clover {
    license.path="$userHome/clover.license"
    instrumentLambda = "block"
    reportStyle = "adg"
}