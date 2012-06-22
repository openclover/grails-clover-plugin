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
        mavenCentral()
    }

    plugins {
        compile ":clover:3.1.6"
        compile ":tomcat:1.3.0"
        compile ":hibernate:1.3.0"
    }
}

clover {

  license.path="../../etc/clover-development.license"
  

// TODO: investigate why the fileset appears to be ignored below...
//  setuptask = { ant, binding, plugin ->
//    // example closure that will be invoked to configure clover.
//    // any initialisation for clover should be done here.
//    // all attributes on the ant clover-setup task can be defined.
//    ant.'clover-setup'(initstring: "${binding.projectWorkDir}/clover/custom/clover.db") {
//      fileset(dir: "grails-app") {
//          ant.excludes: "**/conf/**"
//      }
//    }
//  }

  // example Custom Clover Report configuration:
  // reporttask is a closure that gets passed a reference to the GantBuilder object.
  // any of Clover's report tasks, in fact any Ant Task, can be included.
  // this closure is invoked as soon as all tests have run
  Xreporttask = { ant, binding, self ->

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

    self.launchReport(clover.reports.dir)
  }
}