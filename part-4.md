## Requirement 4 - Respond to any GET `/greeting` with “I’m Sleeping” between 14:00-16:00 (UTC)
### Red
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
Time to think... How can we write the test to make the server sleep (without waiting around until 14:00 ;))?  

Hmm... Well, how would we implement the production code?  
Usually, we would get the system time (`System.currentTimeMillis()`) or use a library (like joda-time) to get the time from the internal clock.  
But how can the test change the system's internal clock? Even if it is possible, it sounds destructive and probably not a good idea to manipulate the computer's clock.  
So we need a different way to get the time.  

Take a few minutes to think about it and then come back.  

Really... Stand up and walk away...  Think about it and come back in a few minutes.

Ready?

Welcome back :)

Some ideas that come to mind:
- We agree that the first idea of manipulating the system time is not a good idea.
- We could change the tests to assert "I'm Sleeping" instead of "Hello" if the tests are running during nap time. This is not desirable because we want all the features of the system to be tested at all times.
- We could add the time to the request in the same way that name is passed. Then in our server we could check that if the time param exists, use it instead of the system's clock. Adding this param to the user facing API makes the API untidy and opens up the system for hacking which makes the system vulnerable. So it is also not a good idea.
- We could add a configuration file that our server will read when it starts. In the configuration file we can add a "testing" flag. If the configuration is in test mode, we could say that the server is sleeping. The problem with this solution is that it adds a branch to the server (if (inTestMode) ... else ... ). That means that only the test side of the branch is tested and we never test the production branch. So it is also not a good idea.

Here are some better ideas:
- The server could request the time from an external time server by making a GET HTTP request. Then in our tests we can start a fake time server and set the time in it for each test. The server would get the url to the time server from a configuration file, which points to the fake time server in tests. You might not like this solution due to the operational cost in production of making an HTTP call to get the time. But still, it is a valid design.
- The nap hours could be put into a configuration file that the server reads when it starts. Then in our tests we can put hours in the configuration file that are inside or outside the current system time. But this requires us to start and stop the server with each test, which is undesirable due to the time it adds to the build. But still, it is a valid design.
- We could use reflection to load a fake clock class in tests and a real system clock class in production, as long as the classes are in the classpath. Then in the test we can manipulate the fake clock with a static mutable variable. The name of the clock implementation class will be passed into the system or put into a configuration file. This is a common method used by libraries (such as log4j and JDBC). This solution is advanced but it is a valid design.

With each of the better ideas, we gain a configurable system.  
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
  override def hour: Int = FakeClock.theHour
}

object FakeClock {
  private var theHour: Int = 0

  def setHour(hour: Int): Unit = theHour = hour
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
Run the tests and see that the new test is still failing.

### Green
We can now implement the feature using our new `Clock` class in the `greet()` method.  

**/src/main/scala/com/wix/Greeter.scala**
```scala
package com.wix

class Greeter(clock: Clock) {
  def greet(maybeName: Option[String] = None): String = 
    if (clock.hour != 15)
      maybeName match {
        case None ⇒ "Hello"
        case Some(name) ⇒ s"Hello $name"
      }
    else
      "I'm Sleeping"
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

### Refactor
I am not too fond of the `if` statement with a nested pattern match in the `Greeter` class.  
So I will refactor it to remove the nesting and only have a pattern match.  

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

  private def isAwake: Boolean = 
    clock.hour != 15
}
```

### Unit testing
You might have noticed that the implementation `clock.hour != 15` is not complete since the Greeter will only sleep at 15:00.  
So let's add some unit tests (UTs) with a mock clock. This is our core logic.  
E2Es are slow and heavy because they start processes. We prefer writing UTs because they are light weight and run faster than E2Es.  
We will use the `JMock` mocking library to mock the `Clock` trait.  

**/pom.xml**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.wix</groupId>
    <artifactId>greeter-server</artifactId>
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
The tests fail, so we fix the bug.  

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

The final directory structure:  
```
/greeter-server
  /src
    /e2e
      /scala
        /com.wix
          GreeterServerE2ETest.scala
    /main
      /scala
        /com.wix
          Clock.scala
          Greeter.scala
          GreeterServer.scala
          GreetingHandler.scala
    /test
      /scala
        /com.wix
          GreeterTest.scala
  pom.xml
```

## Summary
Perhaps the most important note about TDD, is that makes us think about the design of our system and hence it is said that TDD drives the design.  

There is more we could do but we have implemented the system to satisfy the customer's requirements. We can show what we have and have a discussion with the customer about the next step.  
