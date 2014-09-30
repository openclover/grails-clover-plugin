import grails.test.mixin.TestFor
import org.junit.Test

@TestFor(TransactionalAuthorService)
class TransactionalAuthorServiceTest {
    @Test
    void testShowAuthor() {
        service.showAuthor()
    }
}

