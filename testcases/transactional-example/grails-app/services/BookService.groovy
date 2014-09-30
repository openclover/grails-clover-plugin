import grails.transaction.Transactional

/**
 * This checks how Grails' @Transactional annotation on methods changes the AST
 */
class BookService {
    @Transactional(readOnly = true)
    def listBooks(String title, String author) {
        println "called listBooks($title, $author)"
    }

    @Transactional
    def updateBook(Integer id) {
        println "called updateBook($id)"
    }

    def deleteBook() {
        println "called deleteBook"
    }
}
