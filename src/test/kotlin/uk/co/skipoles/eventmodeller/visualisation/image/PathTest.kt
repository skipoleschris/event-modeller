package uk.co.skipoles.eventmodeller.visualisation.image

import io.kotest.matchers.shouldBe
import java.awt.Color
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PathTest {

  private val startPoint = Point(0, 0)
  private val endPoint = Point(100, 100)
  private val midPoint = Point(50, 50)
  private val point1 = Point(25, 50)
  private val point2 = Point(50, 75)
  private val basicPath =
      Path(startPoint, endPoint, hasArrowFrom = false, hasArrowTo = true, Color.red)

  @Test
  fun `create a simple direct Path`() {
    val expected =
        Path(
            listOf(ConnectedPathElement(startPoint), ConnectedPathElement(endPoint)),
            hasArrowFrom = false,
            hasArrowTo = true,
            Color.red)
    basicPath shouldBe expected
    basicPath.points() shouldBe listOf(startPoint, endPoint)
  }

  @Test
  fun `reroute via a single point`() {
    val reroute = ReroutedPathElement(midPoint, SinglePointRerouteDirection.BottomRight)
    val routedPath = basicPath.insertAfter(basicPath.elements.first(), reroute)

    val expected =
        Path(
            listOf(
                ConnectedPathElement(startPoint),
                ReroutedPathElement(midPoint, SinglePointRerouteDirection.BottomRight),
                ConnectedPathElement(endPoint)),
            hasArrowFrom = false,
            hasArrowTo = true,
            Color.red)
    routedPath shouldBe expected
    routedPath.points() shouldBe listOf(startPoint, midPoint, endPoint)
  }

  @Test
  fun `reroute via a double point`() {
    val reroute = DoubleReroutedPathElement(point1, point2, DoublePointRerouteDirection.Right)
    val routedPath = basicPath.insertAfter(basicPath.elements.first(), reroute)

    val expected =
        Path(
            listOf(
                ConnectedPathElement(startPoint),
                DoubleReroutedPathElement(point1, point2, DoublePointRerouteDirection.Right),
                ConnectedPathElement(endPoint)),
            hasArrowFrom = false,
            hasArrowTo = true,
            Color.red)
    routedPath shouldBe expected
    routedPath.points() shouldBe listOf(startPoint, point1, point2, endPoint)
  }
}
