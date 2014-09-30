import grails.test.mixin.TestFor
import org.junit.Test

@TestFor(AuthorService)
class AuthorServiceTest {
    @Test
    void testShowAuthor() {
        service.showAuthor()
    }
}
