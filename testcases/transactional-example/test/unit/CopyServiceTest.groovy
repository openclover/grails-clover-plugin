import grails.test.mixin.TestFor
import org.junit.Test

@TestFor(CopyService)
class CopyServiceTest {
    @Test
    void testCopyBook() {
        service.copyBook()
    }
}
