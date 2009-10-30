
class HelloWorldTests extends GroovyTestCase {

  void testSayHello() {
    
    HelloWorld hi = new HelloWorld()
    assertEquals "World",  hi.getMessage()
    assertEquals "Hello, World", hi.sayHello()
    hi.setMessage "Universe"
    assertEquals "Hello, Universe", hi.sayHello()

  }

}