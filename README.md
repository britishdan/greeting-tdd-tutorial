# TDD Tutorial
This tutorial will give you a glimps into how TDD is done at [Wix](https://www.wix.com/).  
The audience is assumed to have very little experience with TDD.  
The tutorial is best experienced hands-on. Follow the tutorial in your own IDE.

### Overview
At Wix we use an _outside-in_ approach. We create tests that trigger the system from the API.  
Testing from the _outside-in_ shows you how your users will see your API. Consider your tests as your first user.  
Testing from the _outside-in_ means that the test will go through all the layers of the system. It guarentees that the system is always integrated.  
> _Side note_  
> The alternative is the _inside-out_ approach, where each layer is developed separately and then integrated at the end.

#### The 3 rules of TDD
Uncle Bob describes TDD with [3 simple rules](http://butunclebob.com/ArticleS.UncleBob.TheThreeRulesOfTdd)
> 1. You are not allowed to write any production code unless it is to make a failing unit test pass.
> 2. You are not allowed to write any more of a unit test than is sufficient to fail; and compilation failures are failures.
> 3. You are not allowed to write any more production code than is sufficient to pass the one failing unit test.

#### The Red-Green-Refactor cycle
// todo insert the image and explination

## Greeter server
Let's create a web server that greets people but also likes to take an afternoon nap.  
This tutorial should take around 3-4 hours.

The tutorial is written in [Scala](https://www.scala-lang.org/) since most of the backend at Wix is written in Scala.  
The testing framework is [Specs2](http://etorreborre.github.io/specs2/).  
The dependency management tool is [Maven](https://maven.apache.org/).  
The web server is [Jetty](https://www.eclipse.org/jetty/).  
The HTTP client is [sttp](https://sttp.readthedocs.io/en/latest/).  
The IDE I use is [Intellij](https://www.jetbrains.com/idea/), but the tutorial is agnostic to IDE. The tutorial assumes you have an IDE that supports Scala.  

### Requirements
1. [Respond to a GET `/greeting` with 200 HTTP status code](#requirement-1---respond-to-a-get-greeting-with-200-http-status-code)
2. Respond to a GET `/greeting` with “Hello”
3. Respond to a GET `/greeting?name=Dalia` with “Hello Dalia”
4. Respond to any GET `/greeting` with “I’m Sleeping” between 14:00-16:00 (UTC)

#### Requirement 1 - Respond to a GET /greeting with 200 HTTP status code
The first thing we want to do is create an _end to end_ test (E2E).  
> _Take a moment_  
> Think about what you expect the test to do.  
> Try to decribe the test as a "Given, When, Then" sentance.  

First we will create a Maven project with this directory structure
```
/greeter-server
  /src
    /e2e
      /scala
        /com.wix
          GreeterServerE2ETest.scala
  pom.xml
```

**/pom.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.wix</groupId>
    <artifactId>greeting-server</artifactId>
    <version>1.0-SNAPSHOT</version>
</project>
```

We want our test to read:  
_Given a running web server, when a GET request is made to the `/greeting` route, the web server should respond with a 200 HTTP status code._  
So let's create a Specs2 test class in our E2E test file.  

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.mutable.SpecWithJUnit

class GreeterServerE2ETest extends SpecWithJUnit {

}
```
As you can see in your IDE, `SpecWithJUnit` is unrecognized.  
Add Specs2 and Specs2-junit as a Maven dependency.  

**/pom.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.wix</groupId>
    <artifactId>greeting-server</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <!-- https://mvnrepository.com/artifact/org.specs2/specs2-core -->
    <dependency>
        <groupId>org.specs2</groupId>
        <artifactId>specs2-core_2.12</artifactId>
        <version>4.8.1</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.specs2</groupId>
        <artifactId>specs2-junit_2.12</artifactId>
        <version>4.8.1</version>
        <scope>test</scope>
    </dependency>
</project>
```
Allow your IDE to import the dependencies and `SpecWithJUnit` will be recognized.  
> _Side note_  
> When uing Specs2 you can inherit from `SpecificationWithJUnit` or `SpecWithJUnit`.  
> `SpecificationWithJUnit` imports all of the testing library and it increases your compile/build time.  
> `SpecWithJUnit` brings with it a minimal set of imports and you have to add any additional imports yourself.  
> Read more about [lightweight specs](https://github.com/etorreborre/specs2/blob/master/guide/src/test/scala/org/specs2/guide/LightweightSpecs.scala).  

Let's continue our test by trying to instantiate a `GreeterServer` class.  

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.mutable.SpecWithJUnit

class GreeterServerE2ETest extends SpecWithJUnit {
  "GreeterServer" should {
    "Respond to a GET /greeting with 200 HTTP status code" >> {
      val greeterServer = new GreeterServer
    }
  }
}
```
The class does not exist so, due to rule #2, we stop here and create it to fix the compilation failure.

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.mutable.SpecWithJUnit

class GreeterServerE2ETest extends SpecWithJUnit {
  "GreeterServer" should {
    "Respond to a GET /greeting with 200 HTTP status code" >> {
      val greeterServer = new GreeterServer
    }
  }
}

class GreeterServer {

}
```
Notice that I put the `GreeterServer` in the same file, to satisfy rule #3.  
Run the test. You should expect it to pass, since it is only creating an object. But it doesn't run.  
Specs2 complains that there is no assertion in the test. So let's add a simple Specs2's "ok" for now.

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.mutable.SpecWithJUnit

class GreeterServerE2ETest extends SpecWithJUnit {
  "GreeterServer" should {
    "Respond to a GET /greeting with 200 HTTP status code" >> {
      val greeterServer = new GreeterServer
      ok
    }
  }
}

class GreeterServer {

}
```
Run the test again. Now it passes.
The test can now ask the server to start.

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.mutable.SpecWithJUnit

class GreeterServerE2ETest extends SpecWithJUnit {
  "GreeterServer" should {
    "Respond to a GET /greeting with 200 HTTP status code" >> {
      val greeterServer = new GreeterServer
      greeterServer.start()
      ok
    }
  }
}

class GreeterServer {

}
```
The `start()` method does not exist so, due to rule #2, we create it.

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.mutable.SpecWithJUnit

class GreeterServerE2ETest extends SpecWithJUnit {
  "GreeterServer" should {
    "Respond to a GET /greeting with 200 HTTP status code" >> {
      val greeterServer = new GreeterServer
      greeterServer.start()
    }
  }
}

class GreeterServer {
  def start(): Unit = ???
}
```
The compilation failure is fixed.  
Now run the test.  
It fails because `start()` is throwing an `an implementation is missing` exception.  

**I cannot stress enough the importance of seeing the test fail. You only learn from a failing test.**  
The test failed exactly as we expected. It called the `start()` method which throws an exception. We are now 100% sure that the test is indeed calling `start()`.  
So, what can we do to satisfy rule #3?

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.mutable.SpecWithJUnit

class GreeterServerE2ETest extends SpecWithJUnit {
  "GreeterServer" should {
    "Respond to a GET /greeting with 200 HTTP status code" >> {
      val greeterServer = new GreeterServer
      greeterServer.start()
      ok
    }
  }
}

class GreeterServer {
  def start(): Unit = {}
}
```
We give `start()` an empty implementation, so we satisfy rule #3. The test passes.  

We have finished the _given_ section of the test, so we can move on to the _when_ section.  

We want to make a GET request to the `/greeting` route.  
The quickest way to make the request with the [sttp](https://sttp.readthedocs.io/en/latest/) client is by using a `basicRequest`.  

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.mutable.SpecWithJUnit
import sttp.client._

class GreeterServerE2ETest extends SpecWithJUnit {
  "GreeterServer" should {
    "Respond to a GET /greeting with 200 HTTP status code" >> {
      val greeterServer = new GreeterServer
      greeterServer.start()
      
      implicit val backend = HttpURLConnectionBackend()
      val request = basicRequest.get(uri"?????/greeting")
      val response = request.send()
      
      ok
    }
  }
}

class GreeterServer {
  def start(): Unit = {}
}
```
Notice `basicRequest.get(uri"?????/greeting")`.  
We do not know where to send the request because we have not started the web server. So let's to it.  

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.mutable.SpecWithJUnit
import sttp.client._
import org.eclipse.jetty.server.Server

class GreeterServerE2ETest extends SpecWithJUnit {
  "GreeterServer" should {
    "Respond to a GET /greeting with 200 HTTP status code" >> {
      val greeterServer = new GreeterServer
      val port = 9000
      greeterServer.start(port)
      
      implicit val backend = HttpURLConnectionBackend()
      val request = basicRequest.get(uri"http://localhost:$port/greeting")
      val response = request.send()
      
      ok
    }
  }
}

class GreeterServer {
  def start(port: Int): Unit = {
    val server = new Server(port)
    server.start()
  }
}
```
Notice that the port is passed to `start()` so it's available in the test.  
Run the test. It passes.  
We have finished the _when_ section of the test, so we can move on to the _then_ section.  

The request to `/greeting` should return a 200 status code.  

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.mutable.SpecWithJUnit
import sttp.client._
import org.eclipse.jetty.server.Server

class GreeterServerE2ETest extends SpecWithJUnit {
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
  }
}

class GreeterServer {
  def start(port: Int): Unit = {
    val server = new Server(port)
    server.start()
  }
}
```
Run the test. It fails with `404 != 200`. This is a clear and understandable error message, which is great! Complicated and long error messages are hard to understand later on, try to avoid them.  
By seeing the test fail we have learned that the HTTP client call is reaching the web server and that the web server is responding to that call. Very exciting!  
The web server is responding with 404 because we have not created the `/greeting` endpoint. So let's do it.  
From reading the [Jetty](https://www.eclipse.org/jetty/) documentation, this is done with a `Handler`.

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.specs2.mutable.SpecWithJUnit
import sttp.client._
import org.eclipse.jetty.server
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler

class GreeterServerE2ETest extends SpecWithJUnit {
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
  }
}

class GreeterServer {
  def start(port: Int): Unit = {
    val server = new Server(port)
    server.setHandler(new GreetingHandler)
    server.start()
  }
}

class GreetingHandler extends AbstractHandler {
  override def handle(
                       target: String,
                       request: server.Request,
                       httpServletRequest: HttpServletRequest,
                       httpServletResponse: HttpServletResponse
                     ): Unit = {
    if (target == "/greeting") {
      request.setHandled(true)
    }
  }
}
```
Run the test. It passes. By seeing the test fail and then pass, we are sure that our last change fixed it. We are green!  
We now have a running web server that returns 200 status code when the `/greeting` endpoint is called!!  

This now brings me to the _Red-Green-Refactor_ TDD cycle.  
First the test was red and then it passed and became green. The next step is to refactor the code.  
Let's clean up by moving the production classes to their own files in the main scope so that they will be bundled in the JAR that is eventually deployed to production.

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
  }
}
```

**/src/main/scala/com/wix/GreeterServer.scala**
```scala
package com.wix

import org.eclipse.jetty.server.Server

class GreeterServer {
  def start(port: Int): Unit = {
    val server = new Server(port)
    server.setHandler(new GreetingHandler)
    server.start()
  }
}
```

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
      request.setHandled(true)
    }
  }
}
```
In your IDE, notice that there's warning at the end of the test after the closing brace. The warning is that `no implicits found...`. Add the Specs2 trait `MatchResultImplicits` that adds the implicits to get rid of the warning.  
Run the test to make sure it still passes. The test feels like it also needs refactoring. But it is not clear what refactoring to make. So let's wait for another test to help make it clear.  
We are ready for the next test.  

##### Summary
In this section we learned:  
1. How working _outside-in_ integrates the parts of the system and shows how the API will be used.
2. The 3 rules of TDD.
3. How important it is to see the failing test and that the error message is clear.
4. The Red-Green-Refactor cycle.

#### Requirement 2 - Respond to a GET `/greeting` with “Hello”
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

  override def beforeAll(): Unit = {
    val greeterServer = new GreeterServer
    greeterServer.start(port)
  }

  private def whenGreetingIsCalled() = {
    val request = basicRequest.get(uri"http://localhost:$port/greeting")
    val response = request.send()
    response
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
The tests are more concice and readable. Let's move on to the next requirement.

