package helloworld.grails30x

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class RoomController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    static boolean helloWorld() {
        println "Hello World"
        return true;
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Room.list(params), model:[roomCount: Room.count()]
    }

    def show(Room room) {
        respond room
    }

    def create() {
        respond new Room(params)
    }

    @Transactional
    def save(Room room) {
        if (room == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (room.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond room.errors, view:'create'
            return
        }

        room.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'room.label', default: 'Room'), room.id])
                redirect room
            }
            '*' { respond room, [status: CREATED] }
        }
    }

    def edit(Room room) {
        respond room
    }

    @Transactional
    def update(Room room) {
        if (room == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        if (room.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond room.errors, view:'edit'
            return
        }

        room.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'room.label', default: 'Room'), room.id])
                redirect room
            }
            '*'{ respond room, [status: OK] }
        }
    }

    @Transactional
    def delete(Room room) {

        if (room == null) {
            transactionStatus.setRollbackOnly()
            notFound()
            return
        }

        room.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'room.label', default: 'Room'), room.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'room.label', default: 'Room'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
