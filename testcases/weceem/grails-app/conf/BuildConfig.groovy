//uncomment for development
//grails.plugin.location.clover = "../../"

grails.project.dependency.resolution = {
    inherits "global" // inherit Grails default dependencies

    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        //compile "commons-codec:commons-codec:1.3"
        compile 'org.apache.ant:ant:1.7.1'
        compile 'org.apache.ant:ant-launcher:1.7.1'
    }

    plugins {
        build ":bean-fields:0.4"
        build ":clover:4.2.0"
        build ":fckeditor:0.9.4"
        build ":navigation:1.1.1"
        build ":quartz:0.4.1"
        build ":searchable:0.5.5"
        compile ":hibernate:1.3.8"
        compile ":tomcat:1.3.8"
    }
}

clover.reports.dir = "target/clover/report"

clover {

  license.path="$userHome/clover.license"
  srcDirs = ["grails-app", "src", "test"]

  // example Custom Clover Report configuration:
  // reporttask is a closure that gets passed a reference to the GantBuilder object.
  // any of Clover's report tasks, in fact any Ant Task, can be included.
  // this closure is invoked as soon as all tests have run
  reporttask = { ant, binding, self ->

    ant.mkdir(dir: "${clover.reports.dir}")

    ant.'clover-report' {
      ant.current(outfile: "${clover.reports.dir}/clover.pdf", summary: true) {
        format(type: "pdf")
      }
      ant.current(outfile: "${clover.reports.dir}") {
        format(type: "html")
        ant.columns {
          lineCount()
          filteredElements()
          uncoveredElements()
          totalPercentageCovered()
        }
      }
      ant.current(outfile: "${clover.reports.dir}/clover.xml") {
        format(type: "xml")
      }
      ant.current(outfile: "${clover.reports.dir}") {
        format(type: "json")

      }
    }

    if (config.view) {
      self.launchReport(clover.reports.dir)
    }
  }
}