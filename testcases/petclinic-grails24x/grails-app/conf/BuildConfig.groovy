grails.project.dependency.resolution = {
    inherits "global"
    log      "warn"

    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
        grailsRepo "http://plugins.grails.org"
        mavenLocal()
        mavenRepo "https://maven.atlassian.com/content/groups/public/"
        mavenRepo "https://maven.atlassian.com/content/repositories/atlassian-public-snapshot/"
        mavenCentral()
    }

    plugins {
        compile ":clover:4.0.0"
        build   ":tomcat:7.0.54"
        runtime ":hibernate4:4.3.5.4"
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