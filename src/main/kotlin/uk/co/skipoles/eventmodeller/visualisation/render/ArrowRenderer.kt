package uk.co.skipoles.eventmodeller.visualisation.render

import java.awt.Graphics2D
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

internal fun Graphics2D.drawArrow(
    tipX: Int,
    tailX: Int,
    tipY: Int,
    tailY: Int,
    arrowLength: Int = 10,
    arrowAngle: Double = 45.0
) {
  val dx = tipX - tailX
  val dy = tipY - tailY
  val theta = atan2(dy.toDouble(), dx.toDouble())
  val rad = Math.toRadians(arrowAngle)
  val x = tipX - arrowLength * cos(theta + rad)
  val y = tipY - arrowLength * sin(theta + rad)
  val phi2 = Math.toRadians(-arrowAngle)
  val x2 = tipX - arrowLength * cos(theta + phi2)
  val y2 = tipY - arrowLength * sin(theta + phi2)
  val arrowYs = IntArray(3)
  arrowYs[0] = tipY
  arrowYs[1] = y.toInt()
  arrowYs[2] = y2.toInt()
  val arrowXs = IntArray(3)
  arrowXs[0] = tipX
  arrowXs[1] = x.toInt()
  arrowXs[2] = x2.toInt()
  fillPolygon(arrowXs, arrowYs, 3)
}
