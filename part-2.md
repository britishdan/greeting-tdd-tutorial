## Requirement 2 - Respond to a GET `/greeting` with “Hello”
We want our test to read:  
_Given a running web server, when a GET request is made to the `/greeting` route, the web server should respond with "Hello"._  

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.matcher.MatchResultImplicits
import org.specs2.mutable.SpecWithJUnit
import sttp.client._

class GreeterServerE2ETest extends SpecWithJUnit with MatchResultImplicits {
  "GreeterServer" should {
    "Respond to a GET /greeting with 200 HTTP status code" >> {
      val greeterServer = new GreeterServer
      val port = 9000
      greeterServer.start(port)
      
      implicit val backend = HttpURLConnectionBackend()
      val request = basicRequest.get(uri"http://localhost:$port/greeting")
      val response = request.send()
      
      response.code.code must beEqualTo(200)
    }
    
    "Respond to a GET /greeting with Hello" >> {
      val port = 9000
      val greeterServer = new GreeterServer
      greeterServer.start(port)

      implicit val backend = HttpURLConnectionBackend()
      val request = basicRequest.get(uri"http://localhost:$port/greeting")
      val response = request.send()

      response.body must beRight("Hello")
    }
  }
}
```
We can use the `beRight` Specs2 matcher because `sttp` returns the response body as an Either.
Before you run the tests, how do you expect the test to fail? What is the message you expect?  
We expect the message to be `'' != 'Hello'`.  
Now run the tests. The new test fails with a `Failed to bind` exception. This is because the new test is trying to start the greeter server again.  
We want the server to start once before the tests run. So let's use the `BeforeAll` trait and move the code that starts the server to the `beforeAll()` method.

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.matcher.MatchResultImplicits
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.BeforeAll
import sttp.client._

class GreeterServerE2ETest extends SpecWithJUnit with MatchResultImplicits with BeforeAll {
  val port = 9000
  
  override def beforeAll(): Unit = {
    givenGreeterServerIsRunning()
  }
  
  private def givenGreeterServerIsRunning(): Unit = {
    val greeterServer = new GreeterServer
    greeterServer.start(port)
  }
  
  "GreeterServer" should {
    "Respond to a GET /greeting with 200 HTTP status code" >> {
      implicit val backend = HttpURLConnectionBackend()
      val request = basicRequest.get(uri"http://localhost:$port/greeting")
      val response = request.send()
      
      response.code.code must beEqualTo(200)
    }
    
    "Respond to a GET /greeting with Hello" >> {
      implicit val backend = HttpURLConnectionBackend()
      val request = basicRequest.get(uri"http://localhost:$port/greeting")
      val response = request.send()

      response.body must beRight("Hello")
    }
  }
}
```
Run the tests again. The second test fails as we expected: `'' != 'Hello'`.  
Let's implement the feature to pass the test.

**/src/main/scala/com/wix/GreeterHandler.scala**
```scala
package com.wix

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.eclipse.jetty.server
import org.eclipse.jetty.server.handler.AbstractHandler

class GreetingHandler extends AbstractHandler {
  override def handle(
                       target: String,
                       request: server.Request,
                       httpServletRequest: HttpServletRequest,
                       httpServletResponse: HttpServletResponse
                     ): Unit = {
    if (target == "/greeting") {
      httpServletResponse.getWriter.print("Hello")
      request.setHandled(true)
    }
  }
}
```
Run the tests. They both pass. It's time to refactor!  
Adding the second test introduced code duplication. Test code is as important (if not more important) than the production code. So let's refactor the test code.  
We only need one `HttpURLConnectionBackend` to manage the connection pool.  
The call to the server is repeated, so we can extract it to a method.

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.matcher.MatchResultImplicits
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.BeforeAll
import sttp.client._

class GreeterServerE2ETest extends SpecWithJUnit with MatchResultImplicits with BeforeAll {
  val port = 9000
  implicit val backend = HttpURLConnectionBackend()

  private def givenGreeterServerIsRunning(): Unit = {
    val greeterServer = new GreeterServer
    greeterServer.start(port)
  }

  private def whenGreetingIsCalled() = {
    val request = basicRequest.get(uri"http://localhost:$port/greeting")
    val response = request.send()
    response
  }
  
  override def beforeAll(): Unit = {
    givenGreeterServerIsRunning()
  }

  "GreeterServer" should {
    "Respond to a GET /greeting with 200 HTTP status code" >> {
      val response = whenGreetingIsCalled()

      response.code.code must beEqualTo(200)
    }

    "Respond to a GET /greeting with Hello" >> {
      val response = whenGreetingIsCalled()

      response.body must beRight("Hello")
    }
  }
}
```
The tests are more concice and readable.  

## Summary
In this section we learned:  
1. Test code is code and should be kept to the same standards as production code (in this case, the DRY principle).
2. Adding another E2E test shows that "Hello" value goes through all the layers of the system and reaches the user.
3. How important it is to see the failing test and that the error message is clear.

Let's move on to the next requirement.  
[Part 3](./part-3.md)