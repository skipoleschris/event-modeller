package uk.co.skipoles.eventmodeller.visualisation.image

import java.awt.Color
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.lineDistanceFromBox

internal object ImageSettings {
  const val postItSize: Int = 150
  const val horizontalSpace: Int = 75
  const val verticalSpace: Int = 75
  const val horizontalSize: Int = postItSize + horizontalSpace
  const val verticalSize: Int = postItSize + verticalSpace
  const val lineDistanceFromBox: Int = 7
  const val lineShiftAmount: Int = 4
}

data class Point(val x: Int, val y: Int)

internal interface PathElement {
  fun points(): List<Point>
}

internal data class ConnectedPathElement(val point: Point) : PathElement {
  override fun points() = listOf(point)
}

internal enum class SinglePointRerouteDirection {
  TopLeft,
  TopRight,
  BottomLeft,
  BottomRight
}

internal data class ReroutedPathElement(
    val point: Point,
    val direction: SinglePointRerouteDirection
) : PathElement {
  override fun points() = listOf(point)
}

internal enum class DoublePointRerouteDirection {
  Top,
  Bottom,
  Left,
  Right
}

internal data class DoubleReroutedPathElement(
    val point1: Point,
    val point2: Point,
    val direction: DoublePointRerouteDirection
) : PathElement {
  override fun points() = listOf(point1, point2)
}

internal data class Path(
    val elements: List<PathElement>,
    val hasArrowFrom: Boolean,
    val hasArrowTo: Boolean,
    val color: Color
) {
  constructor(
      from: Point,
      to: Point,
      hasArrowFrom: Boolean,
      hasArrowTo: Boolean,
      color: Color
  ) : this(
      listOf(ConnectedPathElement(from), ConnectedPathElement(to)), hasArrowFrom, hasArrowTo, color)
}

internal fun Path.insertAfter(element: PathElement, newElement: PathElement): Path {
  val insertIndex = elements.indexOf(element) + 1
  return copy(elements = elements.take(insertIndex) + newElement + elements.drop(insertIndex))
}

internal fun Path.points(): List<Point> = elements.flatMap { it.points() }

internal data class AvoidanceBox(val x: Int, val y: Int, val width: Int, val height: Int)

internal fun AvoidanceBox.contains(point: Point): Boolean =
    (point.x >= x && point.x <= (x + width) && point.y >= y && point.y <= (y + height))

internal fun AvoidanceBox.sectorFor(point: Point): BoxSector {
  val (midX, midY) = Pair(x + (width / 2), y + (height / 2))
  return if (point.x == x) {
    if (point.y < midY) BoxSector.LeftUpper else BoxSector.LeftLower
  } else if (point.x == (x + width)) {
    if (point.y < midY) BoxSector.RightUpper else BoxSector.RightLower
  } else if (point.x < midX) {
    if (point.y == y) BoxSector.TopLeft else BoxSector.BottomLeft
  } else { // point.x >= midX && point.x < x + width
    if (point.y == y) BoxSector.TopRight else BoxSector.BottomRight
  }
}

enum class BoxSector {
  LeftUpper,
  RightUpper,
  TopLeft,
  TopRight,
  LeftLower,
  RightLower,
  BottomLeft,
  BottomRight
}

internal fun AvoidanceBox.topLeftLinePoint() =
    ReroutedPathElement(
        Point(x - lineDistanceFromBox, y - lineDistanceFromBox),
        SinglePointRerouteDirection.TopLeft)

internal fun AvoidanceBox.topRightLinePoint() =
    ReroutedPathElement(
        Point(x + width + lineDistanceFromBox, y - lineDistanceFromBox),
        SinglePointRerouteDirection.TopRight)

internal fun AvoidanceBox.bottomLeftLinePoint() =
    ReroutedPathElement(
        Point(x - lineDistanceFromBox, y + height + lineDistanceFromBox),
        SinglePointRerouteDirection.BottomLeft)

internal fun AvoidanceBox.bottomRightLinePoint() =
    ReroutedPathElement(
        Point(x + width + lineDistanceFromBox, y + height + lineDistanceFromBox),
        SinglePointRerouteDirection.BottomRight)

internal fun AvoidanceBox.topLinePoints(leftToRight: Boolean) =
    if (leftToRight)
        DoubleReroutedPathElement(
            topLeftLinePoint().point, topRightLinePoint().point, DoublePointRerouteDirection.Top)
    else
        DoubleReroutedPathElement(
            topRightLinePoint().point, topLeftLinePoint().point, DoublePointRerouteDirection.Top)

internal fun AvoidanceBox.bottomLinePoints(leftToRight: Boolean) =
    if (leftToRight)
        DoubleReroutedPathElement(
            bottomLeftLinePoint().point,
            bottomRightLinePoint().point,
            DoublePointRerouteDirection.Bottom)
    else
        DoubleReroutedPathElement(
            bottomRightLinePoint().point,
            bottomLeftLinePoint().point,
            DoublePointRerouteDirection.Bottom)

internal fun AvoidanceBox.leftLinePoints(topToBottom: Boolean) =
    if (topToBottom)
        DoubleReroutedPathElement(
            topLeftLinePoint().point, bottomLeftLinePoint().point, DoublePointRerouteDirection.Left)
    else
        DoubleReroutedPathElement(
            bottomLeftLinePoint().point, topLeftLinePoint().point, DoublePointRerouteDirection.Left)

internal fun AvoidanceBox.rightLinePoints(topToBottom: Boolean) =
    if (topToBottom)
        DoubleReroutedPathElement(
            topRightLinePoint().point,
            bottomRightLinePoint().point,
            DoublePointRerouteDirection.Right)
    else
        DoubleReroutedPathElement(
            bottomRightLinePoint().point,
            topRightLinePoint().point,
            DoublePointRerouteDirection.Right)

internal data class Intersection(
    val path: Path,
    val from: PathElement,
    val to: PathElement,
    val box: AvoidanceBox,
    val entryPoint: Point,
    val exitPoint: Point
)
