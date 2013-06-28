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
        compile ":clover:3.1.12"
        compile ":tomcat:$grailsVersion"
        compile ":hibernate:$grailsVersion"
    }
}

clover {
    license.path="$userHome/clover.license"

    setuptask = { ant, binding, plugin ->
        ant.delete(dir: "target/clover/db")
        ant.delete(dir: "target/clover/tmp")
        ant.'clover-setup'(initstring: "target/clover/db/clover.db",
                tmpDir: "target/clover/tmp", source : "1.6") {
            ant.profiles {
                ant.profile(name: "default", coverageRecorder: "SHARED")
            }
        }
    }

    reporttask =  { ant, binding, plugin ->
        ant.delete(dir: "target/clover/report")
        ant.'clover-report'(initstring: "target/clover/db/clover.db") {
            ant.current(outfile: "target/clover/report/clover.xml") {
                format(type: "xml")
            }
            ant.current(outfile: "target/clover/report") {
                format(type: "html")
            }
        }
    }

}

