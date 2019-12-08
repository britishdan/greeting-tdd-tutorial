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
