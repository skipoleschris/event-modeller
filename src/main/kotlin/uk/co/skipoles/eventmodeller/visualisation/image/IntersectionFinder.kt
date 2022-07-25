package uk.co.skipoles.eventmodeller.visualisation.image

import kotlin.math.abs

internal object IntersectionFinder {

  fun pointsOnLineBetween(start: Point, end: Point): List<Point> {
    // Bresenham algorithm
    val points = mutableListOf<Point>()

    var x = start.x
    var y = start.y

    val dx = abs(end.x - start.x)
    val dy = abs(end.y - start.y)

    val sx = if (start.x < end.x) 1 else -1
    val sy = if (start.y < end.y) 1 else -1

    var err = dx - dy
    var e2: Int

    while (true) {
      points.add(Point(x, y))
      if (x == end.x && y == end.y) break

      e2 = 2 * err
      if (e2 > -dy) {
        err -= dy
        x += sx
      }

      if (e2 < dx) {
        err += dx
        y += sy
      }
    }

    return points.toList()
  }

  fun between(paths: List<Path>, boxes: List<AvoidanceBox>): List<Intersection> =
      paths.mapNotNull { firstIntersectionBetween(it, boxes) }

  private fun firstIntersectionBetween(path: Path, boxes: List<AvoidanceBox>): Intersection? =
      path.elements.windowed(2).firstNotNullOfOrNull {
        intersectsAny(path, it.first(), it.last(), boxes)
      }

  private fun intersectsAny(
      path: Path,
      fromElement: PathElement,
      toElement: PathElement,
      boxes: List<AvoidanceBox>
  ): Intersection? {
    val from =
        when (fromElement) {
          is ConnectedPathElement -> fromElement.point
          is ReroutedPathElement -> fromElement.point
          is DoubleReroutedPathElement -> fromElement.point2
          else -> throw IllegalStateException()
        }
    val to =
        when (toElement) {
          is ConnectedPathElement -> toElement.point
          is ReroutedPathElement -> toElement.point
          is DoubleReroutedPathElement -> toElement.point1
          else -> throw IllegalStateException()
        }

    val notConnectedBoxes = boxes.filterNot { it.contains(from) || it.contains(to) }
    val pointsInsideBoxes =
        pointsOnLineBetween(from, to).mapNotNull { point ->
          val box = notConnectedBoxes.find { it.contains(point) }
          if (box != null) Pair(box, point) else null
        }
    return if (pointsInsideBoxes.isEmpty()) null
    else {
      val firstBoxCrossed = pointsInsideBoxes.first().first
      val allPointsInsideBox =
          pointsInsideBoxes.filter { it.first == firstBoxCrossed }.map { it.second }
      Intersection(
          path,
          fromElement,
          toElement,
          firstBoxCrossed,
          allPointsInsideBox.first(),
          allPointsInsideBox.last())
    }
  }
}
