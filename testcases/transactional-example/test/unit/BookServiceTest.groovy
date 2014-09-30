import grails.test.mixin.TestFor
import org.junit.Test

@TestFor(BookService)
class BookServiceTest {
    @Test
    void testBookService() {
        service.listBooks("Lord of the Rings", "J.R.R. Tolkien")
        service.updateBook(123)
        service.deleteBook()
    }
}
