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
        compile ":clover:3.3.0"
        compile ":tomcat:7.0.52.1"
        compile ":hibernate:3.6.10.10"
    }

    dependencies {
        compile "com.cenqua.clover:clover:3.3.0"
    }

}

clover {
  license.path="$userHome/clover.license"
  instrumentLambda = "block"
}