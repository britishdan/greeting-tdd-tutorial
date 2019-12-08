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
