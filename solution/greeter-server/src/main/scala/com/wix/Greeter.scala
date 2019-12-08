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
