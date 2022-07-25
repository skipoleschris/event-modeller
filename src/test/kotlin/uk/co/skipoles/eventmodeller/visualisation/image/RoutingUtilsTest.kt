package uk.co.skipoles.eventmodeller.visualisation.image

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.awt.Color
import java.util.stream.Stream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoutingUtilsTest {

  private val box = AvoidanceBox(100, 100, 150, 150)
  private val box2 = AvoidanceBox(350, 100, 150, 150)

  @ParameterizedTest
  @MethodSource("bresenhamLines")
  fun `bresenham routing algorithm should be correct`(data: ExpectedLineData) {
    RoutingUtils.pointsOnLineBetween(data.from, data.to) shouldBe data.expected
  }

  private fun bresenhamLines() =
      Stream.of(
          ExpectedLineData(p(0, 0), p(3, 0), listOf(p(0, 0), p(1, 0), p(2, 0), p(3, 0))),
          ExpectedLineData(p(3, 0), p(0, 0), listOf(p(3, 0), p(2, 0), p(1, 0), p(0, 0))),
          ExpectedLineData(p(0, 0), p(0, 3), listOf(p(0, 0), p(0, 1), p(0, 2), p(0, 3))),
          ExpectedLineData(p(0, 3), p(0, 0), listOf(p(0, 3), p(0, 2), p(0, 1), p(0, 0))),
          ExpectedLineData(p(0, 0), p(3, 3), listOf(p(0, 0), p(1, 1), p(2, 2), p(3, 3))),
          ExpectedLineData(p(3, 3), p(0, 0), listOf(p(3, 3), p(2, 2), p(1, 1), p(0, 0))),
          ExpectedLineData(p(0, 0), p(3, 1), listOf(p(0, 0), p(1, 0), p(2, 1), p(3, 1))))

  data class ExpectedLineData(val from: Point, val to: Point, val expected: List<Point>)

  private fun p(x: Int, y: Int) = Point(x, y)

  @Test
  fun `path not crossing a box should not intersect`() {
    val path = Path(p(50, 50), p(200, 50), hasArrowFrom = false, hasArrowTo = true, Color.black)
    RoutingUtils.intersectionsBetween(listOf(path), listOf(box)).shouldBeEmpty()
  }

  @Test
  fun `simple path crossing a box should intersect`() {
    val path = Path(p(50, 150), p(300, 150), hasArrowFrom = false, hasArrowTo = true, Color.black)
    RoutingUtils.intersectionsBetween(listOf(path), listOf(box)) shouldBe
        listOf(
            Intersection(
                path, path.elements.first(), path.elements.last(), box, p(100, 150), p(250, 150)))
  }

  @Test
  fun `single routed path crossing a box should intersect`() {
    val path =
        Path(
            listOf(
                ConnectedPathElement(p(0, 90)),
                ReroutedPathElement(p(50, 150), SinglePointRerouteDirection.TopLeft),
                ReroutedPathElement(p(300, 150), SinglePointRerouteDirection.TopRight),
                ConnectedPathElement(p(300, 90))),
            hasArrowFrom = false,
            hasArrowTo = true,
            Color.black)
    RoutingUtils.intersectionsBetween(listOf(path), listOf(box)) shouldBe
        listOf(
            Intersection(path, path.elements[1], path.elements[2], box, p(100, 150), p(250, 150)))
  }

  @Test
  fun `double routed path crossing a box should intersect`() {
    val path =
        Path(
            listOf(
                ConnectedPathElement(p(0, 90)),
                DoubleReroutedPathElement(p(50, 130), p(90, 150), DoublePointRerouteDirection.Top),
                DoubleReroutedPathElement(
                    p(260, 150), p(300, 100), DoublePointRerouteDirection.Right),
                ConnectedPathElement(p(300, 90))),
            hasArrowFrom = false,
            hasArrowTo = true,
            Color.black)
    RoutingUtils.intersectionsBetween(listOf(path), listOf(box)) shouldBe
        listOf(
            Intersection(path, path.elements[1], path.elements[2], box, p(100, 150), p(250, 150)))
  }

  @Test
  fun `path crossing multiple boxes should intersect only the first`() {
    val path = Path(p(50, 150), p(600, 150), hasArrowFrom = false, hasArrowTo = true, Color.black)
    RoutingUtils.intersectionsBetween(listOf(path), listOf(box, box2)) shouldBe
        listOf(
            Intersection(
                path, path.elements.first(), path.elements.last(), box, p(100, 150), p(250, 150)))
  }
}
