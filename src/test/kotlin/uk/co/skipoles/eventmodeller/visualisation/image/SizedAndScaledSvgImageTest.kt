package uk.co.skipoles.eventmodeller.visualisation.image

import io.kotest.matchers.shouldBe
import org.jfree.graphics2d.svg.SVGGraphics2D
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SizedAndScaledSvgImageTest {

  private val width = 4
  private val height = 4
  private val black = -16777216
  private val white = -1

  @Test
  fun `renders a png imaged from the svg document`() {
    val scale = 1.0
    val graphics = SVGGraphics2D(width, height)
    graphics.scale(scale, scale)
    graphics.fillRect(0, 0, 2, 2)

    val svg = SizedAndScaledSvgImage(graphics.svgDocument, width, height, 1.0)
    val png = svg.asPNG()

    png.width shouldBe width
    png.height shouldBe height

    for (x in 0..3) {
      for (y in 0..3) {
        val expectedRGB = if (x < 2 && y < 2) black else white
        png.getRGB(x, y) shouldBe expectedRGB
      }
    }
  }
}
