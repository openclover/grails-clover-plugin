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
        compile ":redis:1.0.0.M4"
        runtime ":blueprint:1.0.2"
        runtime ":quartz:0.4.2"
        runtime ":resources:1.0.2"
        runtime(":cached-resources:1.0") {
          exclude "resources"
          exclude "cache-headers"
        }
        runtime(":zipped-resources:1.0") {
          exclude "resources"
        }
        runtime ":hibernate:1.3.8"
        build ":clover:3.2.0"
        build ":tomcat:1.3.8"
    }
    dependencies {
    }
}
