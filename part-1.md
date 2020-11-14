## Greeter server
Let's create a web server that greets people but also likes to take an afternoon nap.  
This tutorial should take around 3-4 hours.

The tutorial is written in [Scala](https://www.scala-lang.org/) since most of the backend at Wix is written in Scala.  
The testing framework is [Specs2](http://etorreborre.github.io/specs2/).  
The dependency management tool is [Maven](https://maven.apache.org/).  
The web server is [Jetty](https://www.eclipse.org/jetty/).  
The HTTP client is [sttp](https://sttp.readthedocs.io/en/latest/).  
The IDE I use is [Intellij](https://www.jetbrains.com/idea/), but the tutorial is agnostic to IDE. The tutorial assumes you have an IDE that supports Scala.  

## Requirement 1 - Respond to a GET /greeting with 200 HTTP status code
### Create your first E2E
The first thing we want to do is create an _end to end_ test (E2E).  
> _Take a moment_  
> Think about what you expect the test to do.  
> Try to describe the test as a "Given, When, Then" sentance.  

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
    <artifactId>greeter-server</artifactId>
    <version>1.0-SNAPSHOT</version>
</project>
```

We want our test to read:  
_Given a running web server, when a GET request is made to the `/greeting` route, the web server should respond with a 200 HTTP status code._  

### Given
Let's create a Specs2 test class in our E2E test file.  

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
    <artifactId>greeter-server</artifactId>
    <version>1.0-SNAPSHOT</version>
    
    <dependencies>
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
    </dependencies>
</project>
```
Allow your IDE to import the dependencies and `SpecWithJUnit` will be recognized.  
> _Side note_  
> When using Specs2 you can inherit from `SpecificationWithJUnit` or `SpecWithJUnit`.  
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
It fails because `start()` is implemented with `???`, which is a special Scala placeholder that allows a method to compile but throws an `an implementation is missing` exception when invoked. All new methods can be created with the placeholder.  

**I cannot stress enough the importance of seeing the test fail.**
**You only learn from a failing test.**  

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

### When
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
We do not know where to send the request because we have not created and started the Jetty web server. So let's do it.  

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

### Then
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

## Summary
In this section we learned:  
1. How working _outside-in_ integrates the parts of the system and shows how the API will be used.
2. The 3 rules of TDD.
3. How important it is to see the failing test and that the error message is clear.
4. The Red-Green-Refactor cycle.

> _Take a moment_  
> How do you feel about writing code like this?  
> How do you feel about the stability and correctness of the system you have created so far?  
> Do you think TDD helped to keep you focused?  
> Did you write better production code than usual?  
> Perhaps less production code than usual?  
> Or perhaps it felt slow and painful?  

We are ready for the next test.  
[Part 2](./part-2.md)
