package com.wix

import java.util.concurrent.atomic.AtomicInteger

import org.specs2.matcher.MatchResultImplicits
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.{BeforeAll, BeforeEach}
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
