package helloworld.grails30x

class Room {

    static constraints = {
        title blank: false, nullable: false
    }

    String title
}
