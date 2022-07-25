package uk.co.skipoles.eventmodeller.visualisation.image

import io.kotest.matchers.shouldBe
import java.util.stream.Stream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.lineDistanceFromBox

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AvoidanceBoxTest {

  private val box = AvoidanceBox(50, 100, 150, 200)

  @ParameterizedTest
  @MethodSource("positions")
  fun `knows if a point is inside the avoidance box`(data: ExpectedPositionData) {
    box.contains(data.point) shouldBe data.insideBox
  }

  private fun positions() =
      Stream.of(
          ExpectedPositionData(Point(50, 100), true),
          ExpectedPositionData(Point(200, 100), true),
          ExpectedPositionData(Point(50, 300), true),
          ExpectedPositionData(Point(200, 300), true),
          ExpectedPositionData(Point(150, 200), true),
          ExpectedPositionData(Point(49, 100), false),
          ExpectedPositionData(Point(201, 100), false),
          ExpectedPositionData(Point(50, 301), false),
          ExpectedPositionData(Point(200, 301), false),
          ExpectedPositionData(Point(0, 0), false))

  data class ExpectedPositionData(val point: Point, val insideBox: Boolean)

  @ParameterizedTest
  @MethodSource("sectors")
  fun `knows which sector of an avoidance box that a point falls within`(data: ExpectedSectorData) {
    box.sectorFor(data.point) shouldBe data.sector
  }

  private fun sectors() =
      Stream.of(
          ExpectedSectorData(Point(50, 100), BoxSector.LeftUpper),
          ExpectedSectorData(Point(200, 100), BoxSector.RightUpper),
          ExpectedSectorData(Point(50, 300), BoxSector.LeftLower),
          ExpectedSectorData(Point(200, 300), BoxSector.RightLower),
          ExpectedSectorData(Point(50, 110), BoxSector.LeftUpper),
          ExpectedSectorData(Point(200, 110), BoxSector.RightUpper),
          ExpectedSectorData(Point(50, 290), BoxSector.LeftLower),
          ExpectedSectorData(Point(200, 290), BoxSector.RightLower),
          ExpectedSectorData(Point(60, 100), BoxSector.TopLeft),
          ExpectedSectorData(Point(190, 100), BoxSector.TopRight),
          ExpectedSectorData(Point(60, 300), BoxSector.BottomLeft),
          ExpectedSectorData(Point(190, 300), BoxSector.BottomRight))

  data class ExpectedSectorData(val point: Point, val sector: BoxSector)

  @Test
  fun `knows the points required to reroute around an avoidance box`() {
    val topLeftCorner = Point(box.x - lineDistanceFromBox, box.y - lineDistanceFromBox)
    val topRightCorer = Point(box.x + box.width + lineDistanceFromBox, box.y - lineDistanceFromBox)
    val bottomLeftCorner =
        Point(box.x - lineDistanceFromBox, box.y + box.height + lineDistanceFromBox)
    val bottomRightCorner =
        Point(box.x + box.width + lineDistanceFromBox, box.y + box.height + lineDistanceFromBox)

    box.topLeftLinePoint() shouldBe
        ReroutedPathElement(topLeftCorner, SinglePointRerouteDirection.TopLeft)
    box.topRightLinePoint() shouldBe
        ReroutedPathElement(topRightCorer, SinglePointRerouteDirection.TopRight)
    box.bottomLeftLinePoint() shouldBe
        ReroutedPathElement(bottomLeftCorner, SinglePointRerouteDirection.BottomLeft)
    box.bottomRightLinePoint() shouldBe
        ReroutedPathElement(bottomRightCorner, SinglePointRerouteDirection.BottomRight)

    box.topLinePoints(true) shouldBe
        DoubleReroutedPathElement(topLeftCorner, topRightCorer, DoublePointRerouteDirection.Top)
    box.topLinePoints(false) shouldBe
        DoubleReroutedPathElement(topRightCorer, topLeftCorner, DoublePointRerouteDirection.Top)
    box.bottomLinePoints(true) shouldBe
        DoubleReroutedPathElement(
            bottomLeftCorner, bottomRightCorner, DoublePointRerouteDirection.Bottom)
    box.bottomLinePoints(false) shouldBe
        DoubleReroutedPathElement(
            bottomRightCorner, bottomLeftCorner, DoublePointRerouteDirection.Bottom)
    box.leftLinePoints(true) shouldBe
        DoubleReroutedPathElement(topLeftCorner, bottomLeftCorner, DoublePointRerouteDirection.Left)
    box.leftLinePoints(false) shouldBe
        DoubleReroutedPathElement(bottomLeftCorner, topLeftCorner, DoublePointRerouteDirection.Left)
    box.rightLinePoints(true) shouldBe
        DoubleReroutedPathElement(
            topRightCorer, bottomRightCorner, DoublePointRerouteDirection.Right)
    box.rightLinePoints(false) shouldBe
        DoubleReroutedPathElement(
            bottomRightCorner, topRightCorer, DoublePointRerouteDirection.Right)
  }
}
