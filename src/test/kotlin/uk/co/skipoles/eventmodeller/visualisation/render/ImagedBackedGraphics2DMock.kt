package uk.co.skipoles.eventmodeller.visualisation.render

import io.mockk.every
import io.mockk.mockk
import java.awt.Font
import java.awt.Graphics2D
import java.awt.font.TextAttribute
import java.awt.image.BufferedImage

class ImagedBackedGraphics2DMock(width: Int = 250, height: Int = 250) {

  private val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
  private val underlyingGraphics = image.graphics as Graphics2D
  private val fontAttributes =
      mapOf(Pair(TextAttribute.FAMILY, "helvetica"), Pair(TextAttribute.SIZE, 14))
  private val font = Font.getFont(fontAttributes)
  val graphics = mockk<Graphics2D>()

  init {
    underlyingGraphics.font = font
  }

  fun initialiseMock() {
    every { graphics.color = any() } returns Unit
    every { graphics.font } returns underlyingGraphics.font
    every { graphics.fontRenderContext } returns underlyingGraphics.fontRenderContext
  }
}
