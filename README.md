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
// todo link to sections

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
    <!-- https://mvnrepository.com/artifact/com.softwaremill.sttp/core -->
    <dependency>
        <groupId>com.softwaremill.sttp.client</groupId>
        <artifactId>core_2.12</artifactId>
        <version>1.7.2</version>
        <scope>test</scope>
    </dependency>
</project>
```

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
We do not know where to send the request because we have not created and started the Jetty web server. So let's to it.  

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
    <!-- https://mvnrepository.com/artifact/com.softwaremill.sttp.client/core -->
    <dependency>
        <groupId>com.softwaremill.sttp.client</groupId>
        <artifactId>core_2.12</artifactId>
        <version>2.0.0-RC3</version>
        <scope>test</scope>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.eclipse.jetty/jetty-server -->
    <dependency>
        <groupId>org.eclipse.jetty</groupId>
        <artifactId>jetty-server</artifactId>
        <version>9.4.24.v20191120</version>
    </dependency>
</project>
```

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
The tests are more concice and readable. Let's move on to the next requirement.

##### Summary
In this section we learned:  
1. Test code is code and should be kept to the same standards as production code (in this case, the DRY principle).
2. Adding another E2E test shows that "Hello" value goes through all the layers of the system and reaches the user.
3. How important it is to see the failing test and that the error message is clear.

#### Requirement 3 - Respond to a GET `/greeting?name=Dalia` with “Hello Dalia”
We want our test to read:  
_Given a running web server, when a GET request is made to `/greeting` with the `?name=Name` query param, the web server should respond with "Hello Name"._ 

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
Before running the test, how do yo uexpect the test to fail?  
We expect the test to fail with `'Hello' != 'Hello Dalia'`.  
Run the test and see that the test fails as expected. Let's implement the minimum amount of code to pass the test.  

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
It is time to refactor!  
It has become clear that the `GreetingHandler` is doing more than one thing. The handler is creating the greeting and also dealing with the Jetty request and response. Let's seperate the concerns to separate classes.  

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
We're ready for the last requirement.  

##### Summary
In this section we learned:  
1. Adding another E2E test for a request value that goes through the layers
2. Importance of seeing the failing test message
3. Refactoring - separation of concerns

#### Requirement 4 - Respond to any GET `/greeting` with “I’m Sleeping” between 14:00-16:00 (UTC)
Let's start to write the test.

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
    
    "Respond to any GET `/greeting` with “I’m Sleeping” between 14:00-16:00 (UTC)" >> {
      val response = whenGreetingIsCalled()

      response.body must beRight("I'm Sleeping")
    }
  }
}
```
Obviously, the test fails with `'Hello' != 'I'm Sleeping'`.  
Time to think...  

How can write the test to make the server sleep?  
Hmm... Well, how would we implement the production code?  
Usually, we would get the system time or use a library (like joda-time) to get the time from the internal clock.  
But how can the test change the system's internal clock? Even if it is possible, it sounds destructive and probably not a good idea to manipulate the computer's clock.  
So we need a different way to get the time.  

Take a few minutes to think about it and then come back.  

Really... Stand up and walk away...  Think about it and come back in a few minutes.

Ready?

Welcome back :)

Some ideas that come to mind:
- We agree that the first idea of manipulating the system time is not a good idea.
_ We could change the tests to assert "I'm Sleeping" instead of "Hello" if the tests are running during nap time. This is not desireable because we want all the features of the system to be tested at all times.
- We could add the time to the request in the same way that name is passed. Then in our server we could check that if the time param exists, use it instead of the system's clock. Adding this param to the user facing API makes the API untidy and opens up the system for hacking which makes the system vulnerable. So it is also not a good idea.
- We could add a configuration file that our server will read when it starts. In the configuration file we can add a "testing" flag. If the configuration is in test mode, we could say that the server is sleeping. The problem with this solution is that it adds a branch to the server (if (inTestMode) ... else ... ). That means that only the test side of the branch is tested and we never test the production branch. So it is also not a good idea.

Here are some better ideas:
- The server could request the time from an extenal time server by making a GET HTTP request. Then in our tests we can start a fake time server and set the time in it for each test. The server would get the url to the time server from a configuration file, which points to the fake time server in tests. You might not like this solution due to the operational cost in production of making an HTTP call to get the time. But still, it is a valid design.
- The nap hours could be put into a configuration file that the server reads when it starts. Then in our tests we can put hours in the configuration file that are inside or outside the current system time. But this requires us to start and stop the server with each test, which is undesireable due to the time it adds to the build. But still, it is a valid design.
- We could use reflection to load a fake clock class in tests and a real system clock class in production, as long as the classes are in the classpath. Then in the test we can manipulate the fake clock with a static mutable variable. The name of the clock implementation class will be passed into the system or put into a configuration file. This is a common method used by libaraies (such as log4j and JDBC). This solution is advanced but it is a valid design.

Let's implement the reflection solution.

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.matcher.MatchResultImplicits
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.BeforeAll
import sttp.client._

class GreeterServerE2ETest extends SpecWithJUnit with MatchResultImplicits with BeforeAll with BeforeEach {
  sequential
  
  val port = 9000
  implicit val backend = HttpURLConnectionBackend()

  private def givenGreeterServerIsRunning(): Unit = {
    val greeterServer = new GreeterServer
    greeterServer.start(port, "com.wix.FakeClock")
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
  
  override protected def before(): Unit = {
    FakeClock.setHour(9)
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
    
    "Respond to any GET `/greeting` with “I’m Sleeping” between 14:00-16:00 (UTC)" >> {
      FakeClock.setHour(15)
      val response = whenGreetingIsCalled()

      response.body must beRight("I'm Sleeping")
    }
  }
}

class FakeClock extends Clock {
  def hour: Int = FakeClock.theHour.get()
}

object FakeClock {
  private val theHour: AtomicInteger = new AtomicInteger()

  def setHour(hour: Int): Unit = theHour.set(hour)
}
```

**/src/main/scala/com/wix/GreeterServer.scala**
```scala
package com.wix

import org.eclipse.jetty.server.Server

import scala.reflect.runtime.universe

class GreeterServer {
  def start(port: Int, clockClassName: String): Unit = {
    val clock: Clock = getClock(clockClassName)
    val server = new Server(port)
    server.setHandler(new GreetingHandler(clock))
    server.start()
  }

  private def getClock(clockClassName: String): Clock = {
    val mirror = universe.runtimeMirror(getClass.getClassLoader)
    val classSymbol = mirror.staticClass(clockClassName)
    val constructorSymbol = classSymbol.primaryConstructor.asMethod

    val classMirror = mirror.reflectClass(classSymbol)
    classMirror.reflectConstructor(constructorSymbol).apply().asInstanceOf[Clock]
  }
}
```

**/src/main/scala/com/wix/GreeterHandler.scala**
```scala
package com.wix

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.eclipse.jetty.server
import org.eclipse.jetty.server.handler.AbstractHandler

class GreetingHandler(clock: Clock) extends AbstractHandler {
  private val greeter = new Greeter(clock)

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

**/src/main/scala/com/wix/Clock.scala**
```scala
package com.wix

import java.util.{Calendar, TimeZone}

trait Clock {
  def hour: Int
}
```
Notice that the FakeClock uses an `AtomicInteger` for the mutable state. We want access to this variable to be thread-safe because it is accessed from 2 threads, the test thread and the server thread.  

Run the tests and see that the new test is still failing. We can now implement the feature using our new `Clock` class in the `greet()` method.  

**/src/main/scala/com/wix/Greeter.scala**
```scala
package com.wix

class Greeter(clock: Clock) {
  def greet(maybeName: Option[String]): String = {
    (isAwake, maybeName) match {
      case (true, None) ⇒ s"Hello"
      case (true, Some(name)) ⇒ s"Hello $name"
      case (false, _) ⇒ "I'm Sleeping"
    }
  }

  private def isAwake: Boolean = {
    clock.hour < 14 || clock.hour > 15
  }
}
```

**/src/main/scala/com/wix/Clock.scala**
```scala
package com.wix

import java.util.{Calendar, TimeZone}

trait Clock {
  def hour: Int
}

class SystemTimeClock extends Clock {
  override def hour: Int = {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.get(Calendar.HOUR_OF_DAY)
  }
}
```
Run the tests and see that they all pass.  

We have shown the implementation of the real clock `SystemTimeClock`. The class name `com.wix.SystemTimeClock` will be passed into the system in the production environment. Notice that the `SystemTimeClock` class is not tested. This is ok since we consider the `java.util` library to be well tested. But nonetheless, as with any integration, we will have to check that it works well when we deploy it to production.  

You might be a little worried about the edge cases of the nap time in the `Greeter` class, so let's add some unit tests with a mock clock to feel more secure. This is our core logic after all.  
We will use the `JMock` mocking library.  

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
    <!-- https://mvnrepository.com/artifact/com.softwaremill.sttp.client/core -->
    <dependency>
        <groupId>com.softwaremill.sttp.client</groupId>
        <artifactId>core_2.12</artifactId>
        <version>2.0.0-RC3</version>
        <scope>test</scope>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.eclipse.jetty/jetty-server -->
    <dependency>
        <groupId>org.eclipse.jetty</groupId>
        <artifactId>jetty-server</artifactId>
        <version>9.4.24.v20191120</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.jmock/jmock -->
    <dependency>
        <groupId>org.jmock</groupId>
        <artifactId>jmock</artifactId>
        <version>2.12.0</version>
        <scope>test</scope>
    </dependency>
    <!-- https://mvnrepository.com/artifact/org.jmock/jmock-junit4 -->
    <dependency>
        <groupId>org.jmock</groupId>
        <artifactId>jmock-junit4</artifactId>
        <version>2.12.0</version>
        <scope>test</scope>
    </dependency>
    <!-- https://mvnrepository.com/artifact/com.wix/specs2-jmock -->
    <dependency>
        <groupId>com.wix</groupId>
        <artifactId>specs2-jmock_2.12</artifactId>
        <version>1.3.0</version>
        <scope>test</scope>
    </dependency>
</project>
```

**/src/test/scala/com/wix/GreeterTest.scala**
```scala
package com.wix

import com.wixpress.common.specs2.JMock
import org.specs2.specification.Scope
import org.specs2.mutable.SpecWithJUnit

class GreeterTest extends SpecWithJUnit with JMock {

  trait Context extends Scope {
    val mockClock = mock[Clock]
    val greeter = new Greeter(mockClock)
  }

  "Greeter" should {
    "be awake when the hour is 13" in new Context {
      checking {
        allowing(mockClock).hour willReturn 13
      }

      greeter.greet() must beEqualTo("Hello")
    }

    "be asleep when the hour is 14" in new Context {
      checking {
        allowing(mockClock).hour willReturn 14
      }

      greeter.greet() must beEqualTo("I'm Sleeping")
    }

    "be asleep when the hour is 15" in new Context {
      checking {
        allowing(mockClock).hour willReturn 15
      }

      greeter.greet() must beEqualTo("I'm Sleeping")
    }

    "be awake when the hour is 16" in new Context {
      checking {
        allowing(mockClock).hour willReturn 16
      }

      greeter.greet() must beEqualTo("Hello")
    }
  }
}
```

**/src/main/scala/com/wix/Greeter.scala**
```scala
package com.wix

class Greeter(clock: Clock) {
  def greet(maybeName: Option[String] = None): String = {
    (isAwake, maybeName) match {
      case (true, None) ⇒ s"Hello"
      case (true, Some(name)) ⇒ s"Hello $name"
      case (false, _) ⇒ "I'm Sleeping"
    }
  }

  private def isAwake: Boolean = {
    clock.hour < 14 || clock.hour > 15
  }
}
```
The tests all pass.

##### Summary
1. Perhaps the most important note about TDD, is that makes us think about the design of our system and hence it is said that TDD drives the design.
