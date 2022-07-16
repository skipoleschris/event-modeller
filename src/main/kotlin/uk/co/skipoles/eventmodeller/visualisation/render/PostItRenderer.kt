package uk.co.skipoles.eventmodeller.visualisation.render

import java.awt.Color
import java.awt.Graphics2D
import uk.co.skipoles.eventmodeller.visualisation.PostIt

internal fun Graphics2D.drawPostIt(postIt: PostIt, x: Int, y: Int, postItSize: Int) {
  color = postIt.color
  fillRect(x, y, postItSize, postItSize)
  color = Color.black
  drawCentredMultiLineString(postIt.text, x, y, postItSize, postItSize)
}
