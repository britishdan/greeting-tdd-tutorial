## Requirement 3 - Respond to a GET `/greeting?name=Dalia` with “Hello Dalia”
We want our test to read:  
_Given a running web server, when a GET request is made to `/greeting` with the `?name=Name` query param, the web server should respond with "Hello Name"._ 

### Red

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

  private def whenGreetingIsCalled(withName: Option[String] = None) = {
    val greetingBaseUri = uri"http://localhost:$port/greeting"
    val greetingUri = withName match {
      case None ⇒ uri"$greetingBaseUri"
      case Some(name) ⇒ uri"$greetingBaseUri?name=$name"
    }
    val request = basicRequest.get(greetingUri)
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
    
    "Respond to a GET `/greeting?name=Dalia` with “Hello Dalia”" >> {
      val Dalia = "Dalia"
      val response = whenGreetingIsCalled(withName = Some(Dalia))

      response.body must beRight(s"Hello $Dalia")
    }
  }
}
```
The test needs to pass the name to the `whenGreetingIsCalled()` method so it can be sent on the request.  
We do not want the change to effect the other tests, so we make the name param optional and only add the query param to the request if it is passed.  
Before running the test, how do you expect the test to fail?  
We expect the test to fail with `'Hello' != 'Hello Dalia'`.  
Run the test and see that the test fails as expected. 

### Green
Let's implement the minimum amount of code to pass the test.  

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
      val name = request.getParameter("name")
      httpServletResponse.getWriter.print(s"Hello $name")
      request.setHandled(true)
    }
  }
}
```
We get the name param from the request and concat it to the response.  
Run the tests. Our test passes!  
BUT!! The second test has failed with `'Hello null' != 'Hello'`. This shows that when the name query param is not passed in the API, it is `null`.  
Let's take care of it.  

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
      val maybeName = Option(request.getParameter("name"))
      val greeting = maybeName match {
        case None ⇒ "Hello"
        case Some(name) ⇒ s"Hello $name"
      }
      httpServletResponse.getWriter.print(greeting)
      request.setHandled(true)
    }
  }
}
```
The tests are now green.  

### Refactor
It's time to refactor!  
It has become clear that the `GreetingHandler` is doing more than one thing. The handler is creating the greeting and also dealing with the Jetty request and response. Let's separate the concerns to separate classes.  

**/src/main/scala/com/wix/GreeterHandler.scala**
```scala
package com.wix

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.eclipse.jetty.server
import org.eclipse.jetty.server.handler.AbstractHandler

class GreetingHandler extends AbstractHandler {
  private val greeter = new Greeter
  
  override def handle(
                       target: String,
                       request: server.Request,
                       httpServletRequest: HttpServletRequest,
                       httpServletResponse: HttpServletResponse
                     ): Unit = {
    if (target == "/greeting") {
      val maybeName = Option(request.getParameter("name"))
      val greeting = greeter.greet(maybeName)
      httpServletResponse.getWriter.print(greeting)
      request.setHandled(true)
    }
  }
}
```

**/src/main/scala/com/wix/Greeter.scala**
```scala
package com.wix

class Greeter {
  def greet(maybeName: Option[String]): String =
    maybeName match {
      case None ⇒ "Hello"
      case Some(name) ⇒ s"Hello $name"
    }
}
```
Run the tests to make sure they still pass.  

## Summary
In this section we learned:  
1. Adding another E2E test for a request value that goes through the layers
2. Importance of seeing the failing test message
3. Refactoring - separation of concerns

We're ready for the last requirement.  
[Part 4](./part-4.md)