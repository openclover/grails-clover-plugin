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
        compile ":clover:3.2.2"
        compile ":tomcat:7.0.47"
        compile ":hibernate:3.6.10.4"
    }

    dependencies {
        compile "com.cenqua.clover:clover:3.2.2"
    }

}

clover {
  license.path="$userHome/clover.license"
  instrumentLambda = "block"
}