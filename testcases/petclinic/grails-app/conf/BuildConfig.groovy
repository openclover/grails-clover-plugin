grails.project.work.dir="build"
grails.project.test.reports.dir="build/test"


clover {
  enabled = false

  // example Custom Clover Report configuration:
  // reporttask is a closure that gets passed a reference to the GantBuilder object.
  // any of Clover's report tasks, in fact any Ant Task, can be included.
  // this closure is invoked as soon as all tests have run
  reporttask = { ant ->
      println "In Closure. It is: " + ant
      ant.mkdir(dir:"mybuild/clover/report/")

      ant.'clover-report' {
        ant.current(outfile:"mybuild/clover/report/clover.pdf", summary:true) {
          format(type:"pdf")
        }
        ant.current(outfile:"mybuild/clover/report/") {
          format(type:"html")
          ant.columns {
            lineCount()
            filteredElements()
            uncoveredElements()
            totalPercentageCovered()
          }
        }
        ant.current(outfile:"mybuild/clover/report/clover.xml") {
          format(type:"xml")
        }
        ant.current(outfile:"mybuild/clover/report/") {
          format(type:"json")

        }
      }

  }
}