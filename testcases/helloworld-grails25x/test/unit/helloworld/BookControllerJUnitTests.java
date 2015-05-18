package helloworld;

import helloworld.BookController;
import org.junit.Assert;
import org.junit.Test;

public class BookControllerJUnitTests {

    @Test
    public void testSayHello() {
        final boolean result = BookController.helloWorld();

        Assert.assertTrue(result);
    }
}
