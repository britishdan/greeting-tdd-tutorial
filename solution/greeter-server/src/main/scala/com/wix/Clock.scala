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
