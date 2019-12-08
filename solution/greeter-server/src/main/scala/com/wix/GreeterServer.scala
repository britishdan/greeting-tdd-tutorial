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



