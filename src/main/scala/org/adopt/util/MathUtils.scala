// Copyright (C) Maxime MORGE 2019
package org.adopt.util

/**
  * Compare floating-point numbers in Scala
  *
  */
object MathUtils {
  implicit class MathUtils(x: Double) {
    val precision = 0.000001
    def ~=(y: Double): Boolean = {
      if ((x - y).abs < precision) true else false
    }
  }
}
