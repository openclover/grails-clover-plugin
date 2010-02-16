grails.project.work.dir="build"
grails.project.test.reports.dir="build/test"
clover.reports.dir="${grails.project.work.dir}/clover/report"

grails.plugin.location.clover = "../../"


clover {

  XXXsetuptask = { ant, binding ->
    // example closure that will be invoked to configure clover.
    // any initialisation for clover should be done here.
    // all attributes on the ant clover-setup task can be defined.
    ant.'clover-setup'(initstring:"${binding.projectWorkDir}/clover/custom/clover.db") {
      fileset(dir:"grails-app") { includes: "**/*.groovy"}
    }
  }

  // example Custom Clover Report configuration:
  // reporttask is a closure that gets passed a reference to the GantBuilder object.
  // any of Clover's report tasks, in fact any Ant Task, can be included.
  // this closure is invoked as soon as all tests have run
  reporttask = { ant, binding ->
      
      ant.mkdir(dir:"${clover.reports.dir}")

      ant.'clover-report' {
        ant.current(outfile:"${clover.reports.dir}/clover.pdf", summary:true) {
          format(type:"pdf")
        }
        ant.current(outfile:"${clover.reports.dir}") {
          format(type:"html")
          ant.columns {
            lineCount()
            filteredElements()
            uncoveredElements()
            totalPercentageCovered()
          }
        }
        ant.current(outfile:"${clover.reports.dir}/clover.xml") {
          format(type:"xml")
        }
        ant.current(outfile:"${clover.reports.dir}") {
          format(type:"json")

        }
      }

  }
}