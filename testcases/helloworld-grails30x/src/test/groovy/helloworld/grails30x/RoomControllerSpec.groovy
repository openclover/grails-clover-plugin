package helloworld.grails30x

import grails.test.mixin.*
import spock.lang.*

@TestFor(RoomController)
@Mock(Room)
class RoomControllerSpec extends Specification {

    def populateValidParams(params) {
        assert params != null
        params["title"] = 'someValidTitle '
    }

    void "Test the index action returns the correct model"() {

        when:"The index action is executed"
            controller.index()

        then:"The model is correct"
            !model.roomList
            model.roomCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
            controller.create()

        then:"The model is correctly created"
            model.room!= null
    }

    void "Test the save action correctly persists an instance"() {

        when:"The save action is executed with an invalid instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'POST'
            def room = new Room()
            room.validate()
            controller.save(room)

        then:"The create view is rendered again with the correct model"
            model.room!= null
            view == 'create'

        when:"The save action is executed with a valid instance"
            response.reset()
            populateValidParams(params)
            room = new Room(params)

            controller.save(room)

        then:"A redirect is issued to the show action"
            response.redirectedUrl == '/room/show/1'
            controller.flash.message != null
            Room.count() == 1
    }

    void "Test that the show action returns the correct model"() {
        when:"The show action is executed with a null domain"
            controller.show(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the show action"
            populateValidParams(params)
            def room = new Room(params)
            controller.show(room)

        then:"A model is populated containing the domain instance"
            model.room == room
    }

    void "Test that the edit action returns the correct model"() {
        when:"The edit action is executed with a null domain"
            controller.edit(null)

        then:"A 404 error is returned"
            response.status == 404

        when:"A domain instance is passed to the edit action"
            populateValidParams(params)
            def room = new Room(params)
            controller.edit(room)

        then:"A model is populated containing the domain instance"
            model.room == room
    }

    void "Test the update action performs an update on a valid domain instance"() {
        when:"Update is called for a domain instance that doesn't exist"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'PUT'
            controller.update(null)

        then:"A 404 error is returned"
            response.redirectedUrl == '/room/index'
            flash.message != null

        when:"An invalid domain instance is passed to the update action"
            response.reset()
            def room = new Room()
            room.validate()
            controller.update(room)

        then:"The edit view is rendered again with the invalid instance"
            view == 'edit'
            model.room == room

        when:"A valid domain instance is passed to the update action"
            response.reset()
            populateValidParams(params)
            room = new Room(params).save(flush: true)
            controller.update(room)

        then:"A redirect is issued to the show action"
            room != null
            response.redirectedUrl == "/room/show/$room.id"
            flash.message != null
    }

    void "Test that the delete action deletes an instance if it exists"() {
        when:"The delete action is called for a null instance"
            request.contentType = FORM_CONTENT_TYPE
            request.method = 'DELETE'
            controller.delete(null)

        then:"A 404 is returned"
            response.redirectedUrl == '/room/index'
            flash.message != null

        when:"A domain instance is created"
            response.reset()
            populateValidParams(params)
            def room = new Room(params).save(flush: true)

        then:"It exists"
            Room.count() == 1

        when:"The domain instance is passed to the delete action"
            controller.delete(room)

        then:"The instance is deleted"
            Room.count() == 0
            response.redirectedUrl == '/room/index'
            flash.message != null
    }
}
