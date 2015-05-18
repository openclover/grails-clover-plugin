package helloworld

import spock.lang.Specification

class BookControllerSpec extends Specification {

    void "Test helloWorld method"() {

        when: "Hello world is called"
        def result = BookController.helloWorld()

        then: "Result is true"
        result == true
    }
}
