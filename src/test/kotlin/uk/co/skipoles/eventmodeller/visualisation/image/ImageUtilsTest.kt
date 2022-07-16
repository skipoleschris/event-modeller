package uk.co.skipoles.eventmodeller.visualisation.image

import io.kotest.matchers.shouldBe
import java.util.stream.Stream
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.co.skipoles.eventmodeller.visualisation.*
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.horizontalSize
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.horizontalSpace
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.postItSize
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.verticalSpace

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageUtilsTest {

  private val swimLane = SwimLane(SwimLaneType.Timeline, 1, false, "Timeline")
  private val postIt = CommandPostIt(swimLane, "Test", 1)
  private val model = VisualisationModel(listOf(swimLane), listOf(postIt), mapOf())

  @ParameterizedTest
  @MethodSource("positions100Percent")
  fun `identify post it at given position on non-scaled image`(data: PositionData) {
    postItAtPosition(model, data.x, data.y) shouldBe data.expected
  }

  @ParameterizedTest
  @MethodSource("positions50Percent")
  fun `identify post it at given position on scaled image`(data: PositionData) {
    postItAtPosition(model, data.x, data.y, 0.5) shouldBe data.expected
  }

  private fun positions100Percent(): Stream<PositionData> {
    val topLeftX = horizontalSize + horizontalSpace
    val topLeftY = verticalSpace
    return Stream.of(
        PositionData(topLeftX, topLeftY, postIt),
        PositionData(topLeftX + postItSize, topLeftY, postIt),
        PositionData(topLeftX + postItSize, topLeftY + postItSize, postIt),
        PositionData(topLeftX, topLeftY + postItSize, postIt),
        PositionData(topLeftX + (postItSize / 2), topLeftY + (postItSize / 2), postIt),
        PositionData(topLeftX - 1, topLeftY - 1, null),
        PositionData(topLeftX + postItSize + 1, topLeftY + postItSize + 1, null))
  }

  private fun positions50Percent(): Stream<PositionData> {
    val topLeftX = (horizontalSize + horizontalSpace) / 2
    val topLeftY = verticalSpace / 2
    val size = postItSize / 2
    return Stream.of(
        PositionData(topLeftX + 1, topLeftY + 1, postIt),
        PositionData(topLeftX + size, topLeftY + 1, postIt),
        PositionData(topLeftX + size, topLeftY + size, postIt),
        PositionData(topLeftX, topLeftY + size, postIt),
        PositionData(topLeftX + (size / 2), topLeftY + (size / 2), postIt),
        PositionData(topLeftX - 1, topLeftY - 1, null),
        PositionData(topLeftX + size + 1, topLeftY + size + 1, null))
  }

  data class PositionData(val x: Int, val y: Int, val expected: PostIt?)
}
