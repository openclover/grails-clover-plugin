package helloworld.grails30x;

import org.junit.Assert;
import org.junit.Test;

public class RoomControllerJUnitTests {

    @Test
    public void testSayHello() {
        final boolean result = RoomController.helloWorld();

        Assert.assertTrue(result);
    }
}
