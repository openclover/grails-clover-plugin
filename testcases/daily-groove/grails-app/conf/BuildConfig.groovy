//uncomment following line if you wish to develop without reinstalling clover plugin
//grails.plugin.location.clover = "../../"

grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolution = {
    inherits "global"
    log      "warn"
    
    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    plugins {
        build ":clover:3.1.6"
        build ":tomcat:1.3.7"
        compile ":redis:1.0.0.M4"
        compile ":jquery:1.4.4.1"
        runtime ":blueprint:1.0.2"
        runtime ":quartz:0.4.2"
        runtime ":resources:1.0-RC2-SNAPSHOT"
        runtime ":cached-resources:1.0-alpha6"
        runtime ":zipped-resources:1.0-RC1"
        runtime ":hibernate:1.3.7"
    }
    dependencies {
    }
}
