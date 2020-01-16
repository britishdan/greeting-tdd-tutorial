package com.wix

import java.time.{LocalTime, ZoneId}

trait Clock {
  def hour: Int
}

class SystemTimeClock extends Clock {
  override def hour: Int = {
    val localTime = LocalTime.now(ZoneId.of("UTC"))
    localTime.getHour
  }
}
