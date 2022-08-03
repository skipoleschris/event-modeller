package uk.co.skipoles.eventmodeller.visualisation.image

import io.kotest.matchers.collections.shouldContainExactly
import java.util.stream.Stream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AvoidBoxesPathRouterTest {

  private val box = AvoidanceBox(100, 100, 100, 100)
  private val boxes = listOf(box)

  private val topLeft = box.topLeftLinePoint()
  private val topRight = box.topRightLinePoint()
  private val bottomLeft = box.bottomLeftLinePoint()
  private val bottomRight = box.bottomRightLinePoint()

  private val topLtoR = box.topLinePoints(true)
  private val bottomLtoR = box.bottomLinePoints(true)
  private val leftTtoB = box.leftLinePoints(true)
  private val rightTtoB = box.rightLinePoints(true)
  private val topRtoL = box.topLinePoints(false)
  private val bottomRtoL = box.bottomLinePoints(false)
  private val leftBtoT = box.leftLinePoints(false)
  private val rightBtoT = box.rightLinePoints(false)

  private val leftUpper = Point(99, 125)
  private val leftTop = Point(125, 99)
  private val rightTop = Point(175, 99)
  private val rightUpper = Point(201, 125)
  private val rightLower = Point(201, 175)
  private val rightBottom = Point(175, 201)
  private val leftBottom = Point(125, 201)
  private val leftLower = Point(99, 175)

  @Test
  fun `paths not needing routing are not changed`() {
    val path1 = Path(Point(50, 50), Point(250, 50))
    val path2 = Path(Point(50, 50), Point(50, 250))
    AvoidBoxesPathRouter.routeAroundBoxes(listOf(path1, path2), boxes) shouldContainExactly
        listOf(path1, path2)
  }

  @ParameterizedTest
  @MethodSource("leftUpperEntries")
  fun `paths entering via the left upper should be rerouted correctly`(data: ExpectedReroute) {
    val outputPath =
        data.inputPath.insertAfter(data.inputPath.elements.first(), data.expectedAddition)
    AvoidBoxesPathRouter.routeAroundBoxes(listOf(data.inputPath), boxes) shouldContainExactly
        listOf(outputPath)
  }

  private fun leftUpperEntries() =
      Stream.of(
          ExpectedReroute(Path(leftUpper, leftTop), topLeft),
          ExpectedReroute(Path(leftUpper, rightTop), topLeft),
          ExpectedReroute(Path(leftUpper, rightUpper), topLtoR),
          ExpectedReroute(Path(leftUpper, rightLower), topLtoR),
          ExpectedReroute(Path(leftUpper, rightBottom), bottomLeft),
          ExpectedReroute(Path(leftUpper, leftBottom), bottomLeft))

  @ParameterizedTest
  @MethodSource("leftTopEntries")
  fun `paths entering via the left top should be rerouted correctly`(data: ExpectedReroute) {
    val outputPath =
        data.inputPath.insertAfter(data.inputPath.elements.first(), data.expectedAddition)
    AvoidBoxesPathRouter.routeAroundBoxes(listOf(data.inputPath), boxes) shouldContainExactly
        listOf(outputPath)
  }

  private fun leftTopEntries() =
      Stream.of(
          ExpectedReroute(Path(leftTop, rightUpper), topRight),
          ExpectedReroute(Path(leftTop, rightLower), topRight),
          ExpectedReroute(Path(leftTop, rightBottom), leftTtoB),
          ExpectedReroute(Path(leftTop, leftBottom), leftTtoB),
          ExpectedReroute(Path(leftTop, leftLower), topLeft),
          ExpectedReroute(Path(leftTop, leftUpper), topLeft))

  @ParameterizedTest
  @MethodSource("rightTopEntries")
  fun `paths entering via the right top should be rerouted correctly`(data: ExpectedReroute) {
    val outputPath =
        data.inputPath.insertAfter(data.inputPath.elements.first(), data.expectedAddition)
    AvoidBoxesPathRouter.routeAroundBoxes(listOf(data.inputPath), boxes) shouldContainExactly
        listOf(outputPath)
  }

  private fun rightTopEntries() =
      Stream.of(
          ExpectedReroute(Path(rightTop, rightUpper), topRight),
          ExpectedReroute(Path(rightTop, rightLower), topRight),
          ExpectedReroute(Path(rightTop, rightBottom), rightTtoB),
          ExpectedReroute(Path(rightTop, leftBottom), rightTtoB),
          ExpectedReroute(Path(rightTop, leftLower), topLeft),
          ExpectedReroute(Path(rightTop, leftUpper), topLeft))

  @ParameterizedTest
  @MethodSource("rightUpperEntries")
  fun `paths entering via the right upper should be rerouted correctly`(data: ExpectedReroute) {
    val outputPath =
        data.inputPath.insertAfter(data.inputPath.elements.first(), data.expectedAddition)
    AvoidBoxesPathRouter.routeAroundBoxes(listOf(data.inputPath), boxes) shouldContainExactly
        listOf(outputPath)
  }

  private fun rightUpperEntries() =
      Stream.of(
          ExpectedReroute(Path(rightUpper, rightBottom), bottomRight),
          ExpectedReroute(Path(rightUpper, leftBottom), bottomRight),
          ExpectedReroute(Path(rightUpper, leftLower), topRtoL),
          ExpectedReroute(Path(rightUpper, leftUpper), topRtoL),
          ExpectedReroute(Path(rightUpper, leftTop), topRight),
          ExpectedReroute(Path(rightUpper, rightTop), topRight))

  @ParameterizedTest
  @MethodSource("rightLowerEntries")
  fun `paths entering via the right lower should be rerouted correctly`(data: ExpectedReroute) {
    val outputPath =
        data.inputPath.insertAfter(data.inputPath.elements.first(), data.expectedAddition)
    AvoidBoxesPathRouter.routeAroundBoxes(listOf(data.inputPath), boxes) shouldContainExactly
        listOf(outputPath)
  }

  private fun rightLowerEntries() =
      Stream.of(
          ExpectedReroute(Path(rightLower, rightBottom), bottomRight),
          ExpectedReroute(Path(rightLower, leftBottom), bottomRight),
          ExpectedReroute(Path(rightLower, leftLower), bottomRtoL),
          ExpectedReroute(Path(rightLower, leftUpper), bottomRtoL),
          ExpectedReroute(Path(rightLower, leftTop), topRight),
          ExpectedReroute(Path(rightLower, rightTop), topRight))

  @ParameterizedTest
  @MethodSource("rightBottomEntries")
  fun `paths entering via the right bottom should be rerouted correctly`(data: ExpectedReroute) {
    val outputPath =
        data.inputPath.insertAfter(data.inputPath.elements.first(), data.expectedAddition)
    AvoidBoxesPathRouter.routeAroundBoxes(listOf(data.inputPath), boxes) shouldContainExactly
        listOf(outputPath)
  }

  private fun rightBottomEntries() =
      Stream.of(
          ExpectedReroute(Path(rightBottom, leftLower), bottomLeft),
          ExpectedReroute(Path(rightBottom, leftUpper), bottomLeft),
          ExpectedReroute(Path(rightBottom, leftTop), rightBtoT),
          ExpectedReroute(Path(rightBottom, rightTop), rightBtoT),
          ExpectedReroute(Path(rightBottom, rightUpper), bottomRight),
          ExpectedReroute(Path(rightBottom, rightLower), bottomRight))

  @ParameterizedTest
  @MethodSource("leftBottomEntries")
  fun `paths entering via the left bottom should be rerouted correctly`(data: ExpectedReroute) {
    val outputPath =
        data.inputPath.insertAfter(data.inputPath.elements.first(), data.expectedAddition)
    AvoidBoxesPathRouter.routeAroundBoxes(listOf(data.inputPath), boxes) shouldContainExactly
        listOf(outputPath)
  }

  private fun leftBottomEntries() =
      Stream.of(
          ExpectedReroute(Path(leftBottom, leftLower), bottomLeft),
          ExpectedReroute(Path(leftBottom, leftUpper), bottomLeft),
          ExpectedReroute(Path(leftBottom, leftTop), leftBtoT),
          ExpectedReroute(Path(leftBottom, rightTop), leftBtoT),
          ExpectedReroute(Path(leftBottom, rightUpper), bottomRight),
          ExpectedReroute(Path(leftBottom, rightLower), bottomRight))

  @ParameterizedTest
  @MethodSource("leftLowerEntries")
  fun `paths entering via the left lower should be rerouted correctly`(data: ExpectedReroute) {
    val outputPath =
        data.inputPath.insertAfter(data.inputPath.elements.first(), data.expectedAddition)
    AvoidBoxesPathRouter.routeAroundBoxes(listOf(data.inputPath), boxes) shouldContainExactly
        listOf(outputPath)
  }

  private fun leftLowerEntries() =
      Stream.of(
          ExpectedReroute(Path(leftLower, leftTop), topLeft),
          ExpectedReroute(Path(leftLower, rightTop), topLeft),
          ExpectedReroute(Path(leftLower, rightUpper), bottomLtoR),
          ExpectedReroute(Path(leftLower, rightLower), bottomLtoR),
          ExpectedReroute(Path(leftLower, rightBottom), bottomLeft),
          ExpectedReroute(Path(leftLower, leftBottom), bottomLeft))

  data class ExpectedReroute(val inputPath: Path, val expectedAddition: PathElement)

  @Test
  fun `paths can be routed around multiple boxes`() {
    val box1 = AvoidanceBox(100, 100, 100, 100)
    val box2 = AvoidanceBox(300, 300, 100, 100)
    val multipleBoxes = listOf(box1, box2)

    val path = Path(Point(0, 150), Point(500, 350))

    val expectedPoint1 = box1.bottomLeftLinePoint()
    val expectedPoint2 = box2.topRightLinePoint()
    val expectedPath =
        path
            .insertAfter(path.elements[0], expectedPoint1)
            .insertAfter(expectedPoint1, expectedPoint2)
    AvoidBoxesPathRouter.routeAroundBoxes(listOf(path), multipleBoxes) shouldContainExactly
        listOf(expectedPath)
  }
}
