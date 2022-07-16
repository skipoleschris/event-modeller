package uk.co.skipoles.eventmodeller.visualisation.render

import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.awt.Graphics2D
import java.util.stream.Stream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArrowRendererTest {

  private val graphics = mockk<Graphics2D>()

  @BeforeEach
  fun resetMocks() {
    clearAllMocks()
  }

  @ParameterizedTest
  @MethodSource("variousArrows")
  fun `correctly sized arrow is rendered`(data: ArrowData) {
    val xPositions = slot<IntArray>()
    val yPositions = slot<IntArray>()
    every { graphics.fillPolygon(capture(xPositions), capture(yPositions), 3) } returns Unit

    graphics.drawArrow(
        data.tipX, data.tailX, data.tipY, data.tailY, data.arrowLength, data.arrowAngle)

    xPositions.captured shouldBe IntArray(3, indexValueFrom(data.xPositions))
    yPositions.captured shouldBe IntArray(3, indexValueFrom(data.yPositions))
  }

  private fun indexValueFrom(items: List<Int>): (Int) -> Int = fun(index: Int) = items[index]

  data class ArrowData(
      val tipX: Int,
      val tailX: Int,
      val tipY: Int,
      val tailY: Int,
      val arrowLength: Int,
      val arrowAngle: Double,
      val xPositions: List<Int>,
      val yPositions: List<Int>
  )

  private fun variousArrows() =
      Stream.of(
          ArrowData(100, 100, 100, 120, 10, 45.0, listOf(100, 92, 107), listOf(100, 107, 107)),
          ArrowData(100, 120, 100, 100, 10, 45.0, listOf(100, 107, 107), listOf(100, 107, 92)),
          ArrowData(100, 120, 100, 120, 10, 45.0, listOf(100, 100, 110), listOf(100, 110, 100)))
}
