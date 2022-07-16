package uk.co.skipoles.eventmodeller.visualisation.render

import io.kotest.matchers.ints.shouldBeInRange
import io.mockk.*
import java.awt.Color
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import uk.co.skipoles.eventmodeller.visualisation.CommandPostIt
import uk.co.skipoles.eventmodeller.visualisation.SwimLane
import uk.co.skipoles.eventmodeller.visualisation.SwimLaneType

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class PostItRenderTest {

  private val mockGraphics = ImagedBackedGraphics2DMock()
  private val graphics = mockGraphics.graphics

  private val captureX = slot<Int>()
  private val captureY = slot<Int>()

  @BeforeEach
  fun setup() {
    clearAllMocks()
    mockGraphics.initialiseMock()
    captureX.clear()
    captureY.clear()

    every { graphics.fillRect(0, 10, 150, 150) } returns Unit
    every { graphics.drawString("Test", capture(captureX), capture(captureY)) } returns Unit
  }

  @Test
  fun `the postIt should be correctly rendered`() {
    val swimLane = SwimLane(SwimLaneType.Timeline, 1, false, "Timeline")
    val postIt = CommandPostIt(swimLane, "Test", 1)

    graphics.drawPostIt(postIt, 0, 10, 150)

    captureX.captured shouldBeInRange 55..70
    captureY.captured shouldBeInRange 70..85

    verifyOrder {
      graphics.color = postIt.color
      graphics.fillRect(0, 10, 150, 150)
      graphics.color = Color.black
      graphics.font
      graphics.fontRenderContext
      graphics.font
      graphics.fontRenderContext
      graphics.drawString(postIt.text, any<Int>(), any())
    }
    confirmVerified(graphics)
  }
}
