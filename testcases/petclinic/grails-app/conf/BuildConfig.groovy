//uncomment following line if you wish to develop without reinstalling clover plugin
//grails.plugin.location.clover = "../../"

//grails.project.work.dir = "build"
//grails.project.test.reports.dir = "build/test"
//clover.reports.dir = "${grails.project.work.dir}/clover/report"

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
        compile ":clover:3.3.0"
        compile ":tomcat:$grailsVersion"
        compile ":hibernate:$grailsVersion"
    }
}

clover {
    license.path="$userHome/clover.license"
}