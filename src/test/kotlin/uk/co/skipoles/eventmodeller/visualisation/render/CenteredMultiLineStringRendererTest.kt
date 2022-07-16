package uk.co.skipoles.eventmodeller.visualisation.render

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class CenteredMultiLineStringRendererTest {

  private val mockGraphics = ImagedBackedGraphics2DMock()
  private val graphics = mockGraphics.graphics

  private val captureStrings = mutableListOf<String>()
  private val captureX = mutableListOf<Int>()
  private val captureY = mutableListOf<Int>()

  @BeforeEach
  fun setup() {
    clearAllMocks()
    mockGraphics.initialiseMock()
    captureStrings.clear()
    captureX.clear()
    captureY.clear()

    every {
      graphics.drawString(capture(captureStrings), capture(captureX), capture(captureY))
    } returns Unit
  }

  @Test
  fun `draw string that fits on a single line`() {
    graphics.drawCentredMultiLineString("Test", 50, 50, 150, 150)
    captureStrings shouldContainExactly listOf("Test")
    captureX shouldHaveSize 1
    captureX[0] shouldBeInRange 105..120
    captureY shouldHaveSize 1
    captureY[0] shouldBeInRange 110..125
  }

  @Test
  fun `draw string that spans multiple lines`() {
    graphics.drawCentredMultiLineString(
        "This is a longer test string that should span across multiple lines", 50, 50, 150, 150)
    captureStrings shouldContainExactly
        listOf("This is a longer test", "string that should span", "across multiple lines")
    captureX shouldHaveSize 3
    captureX[0] shouldBeInRange 55..70
    captureX[1] shouldBeInRange 45..60
    captureX[2] shouldBeInRange 55..70
    captureY shouldHaveSize 3
    captureY[0] shouldBeInRange 95..110
    captureY[1] shouldBeInRange 110..125
    captureY[2] shouldBeInRange 125..140
  }

  @Test
  fun `draw string with long word that must be broken up`() {
    graphics.drawCentredMultiLineString(
        "This_is_a_longer_string_as_a_single_word", 50, 50, 150, 150)
    captureStrings shouldHaveSize 2
    captureStrings[0].length shouldBeInRange 15..25
    captureStrings[1].length shouldBeInRange 15..25
    captureStrings.joinToString("") shouldBe "This_is_a_longer_string_as_a_single_word"
    captureX shouldHaveSize 2
    captureX[0] shouldBeInRange 45..60
    captureX[1] shouldBeInRange 50..70
    captureY shouldHaveSize 2
    captureY[0] shouldBeInRange 100..120
    captureY[1] shouldBeInRange 115..135
  }

  @Test
  fun `draw string that splits to too many lines`() {
    graphics.drawCentredMultiLineString(
        "This is a longer test string that should span across multiple lines", 50, 50, 150, 35)
    captureStrings shouldContainExactly listOf("This is a longer test", "string that should span")
    captureX shouldHaveSize 2
    captureX[0] shouldBeInRange 55..70
    captureX[1] shouldBeInRange 45..60
    captureY shouldHaveSize 2
    captureY[0] shouldBeInRange 45..60
    captureY[1] shouldBeInRange 60..75
  }
}
