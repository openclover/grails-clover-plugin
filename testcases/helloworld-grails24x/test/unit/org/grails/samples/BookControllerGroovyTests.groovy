package org.grails.samples

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import helloworld.Book
import helloworld.BookController

@TestFor(BookController)
@TestMixin(DomainClassUnitTestMixin)
class BookControllerGroovyTests {

    void testSomething() {
        when: ""
        then: ""
    }

    void testAddGET() {
        controller.request.method = 'GET'
        def model = controller.add()

        assertNotNull model.bookBean
        assertTrue model.bookBean instanceof Book
    }

    void testAddInvalidBook() {
        mockDomain(Book)
        controller.request.method = 'POST'

        controller.add()

        assertEquals "/book/add", view
        assertNotNull model.bookBean
    }

    void testValidBook() {
        mockDomain(Book)

        controller.params.book = [title: 'flintstone']

        controller.request.method = "PUT"
        controller.add()

        assertEquals "/book/add", view
    }


    void testFindOneResult() {
        mockDomain(Book, [new Book(title: "flintstone")])

        controller.request.method = 'POST'
        controller.params.title = 'flintstone'
        controller.find.call()

        assertEquals "/book/show/1", response.redirectUrl
    }

    void testFindManyResults() {
        mockDomain(Book, [new Book(title: "flintstone"), new Book(title: "flintstone")])

        controller.request.method = 'POST'
        controller.params.title = 'flintstone'
        controller.find.call()

        assertEquals "/book/selection", view
        assertNotNull model.books
        assertEquals 2, model.books.size()
    }

}