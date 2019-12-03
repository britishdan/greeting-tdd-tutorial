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

## Greeter server
Let's create a web server that greets people but also likes to take an afternoon nap.

The tutorial is written in [Scala](https://www.scala-lang.org/) since most of the backend at Wix is written in Scala.  
The testing framework is [Specs2](http://etorreborre.github.io/specs2/).  
The dependency management tool is [Maven](https://maven.apache.org/).  
The web server is [Jetty](https://www.eclipse.org/jetty/).

### Requirements
1. [Respond to a GET `/greeting` with 200 HTTP status code](#requirement-1---respond-to-a-get-greeting-with-200-http-status-code)
2. Respond to a GET `/greeting` with “Hello”
3. Respond to a GET `/greeting?name=Dalia` with “Hello Dalia”
4. Respond to any GET `/greeting` with “I’m Sleeping” between 14:00-16:00 (UTC)

#### Requirement 1 - Respond to a GET /greeting with 200 HTTP status code
The first thing we want to do is create an _end to end_ test (E2E).  
> Take a moment  
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

We want our test to read like this:  
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

Let's continue our test by trying to create a GreeterServer class.  

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.mutable.SpecWithJUnit

class GreeterServerE2ETest extends SpecWithJUnit {
  "GreeterServer" should {
    "say Hello" >> {
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
    "say Hello" >> {
      val greeterServer = new GreeterServer
    }
  }
}

class GreeterServer {

}
```
Notice that I put the `GreeterServer` in the same file, to satisfy rule #3.  
The test can now ask the server to start.

**/src/e2e/scala/com/wix/GreeterServerE2ETest.scala**
```scala
package com.wix

import org.specs2.mutable.SpecWithJUnit

class GreeterServerE2ETest extends SpecWithJUnit {
  "GreeterServer" should {
    "say Hello" >> {
      val greeterServer = new GreeterServer
      greeterServer.start()
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
    "say Hello" >> {
      val greeterServer = new GreeterServer
      greeterServer.start()
    }
  }
}

class GreeterServer {
  def start(): Unit = ???
}
```
Notice that `start()` is not implemented but there is no more compilation failure, so we satisfy rule #3.
