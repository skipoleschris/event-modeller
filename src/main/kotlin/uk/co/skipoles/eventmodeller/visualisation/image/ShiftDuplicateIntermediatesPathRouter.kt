package uk.co.skipoles.eventmodeller.visualisation.image

internal object ShiftDuplicateIntermediatesPathRouter {

  fun adjustDuplicatedPathPoints(paths: List<Path>): List<Path> =
      paths
          .fold(AdjustmentResult()) { result, path ->
            if (path.elements.size == 2) result.withPath(path)
            else {
              val updatedPath = adjustDuplicatedIntermediatePoints(path, result.usedPoints)
              result.withPath(updatedPath)
            }
          }
          .paths

  data class AdjustmentResult(
      val usedPoints: Set<Point> = emptySet(),
      val paths: List<Path> = emptyList()
  ) {
    fun withPath(path: Path): AdjustmentResult {
      val intermediatePoints = path.points().drop(1).dropLast(1).toSet()
      return AdjustmentResult(usedPoints + intermediatePoints, paths + path)
    }
  }

  private fun adjustDuplicatedIntermediatePoints(path: Path, usedPoints: Set<Point>): Path =
      path.copy(
          elements =
              path.elements.map {
                when (it) {
                  is ReroutedPathElement -> moveOutwards(it, usedPoints)
                  is DoubleReroutedPathElement -> moveOutwards(it, usedPoints)
                  else -> it
                }
              })

  private fun moveOutwards(
      element: ReroutedPathElement,
      usedPoints: Set<Point>
  ): ReroutedPathElement {
    var point = element.point
    while (usedPoints.contains(point)) {
      point =
          when (element.direction) {
            SinglePointRerouteDirection.TopLeft ->
                Point(
                    point.x - ImageSettings.lineShiftAmount,
                    point.y - ImageSettings.lineShiftAmount)
            SinglePointRerouteDirection.TopRight ->
                Point(
                    point.x + ImageSettings.lineShiftAmount,
                    point.y - ImageSettings.lineShiftAmount)
            SinglePointRerouteDirection.BottomLeft ->
                Point(
                    point.x - ImageSettings.lineShiftAmount,
                    point.y + ImageSettings.lineShiftAmount)
            SinglePointRerouteDirection.BottomRight ->
                Point(
                    point.x + ImageSettings.lineShiftAmount,
                    point.y + ImageSettings.lineShiftAmount)
          }
    }
    return element.copy(point = point)
  }

  private fun moveOutwards(
      element: DoubleReroutedPathElement,
      usedPoints: Set<Point>
  ): DoubleReroutedPathElement {
    var point1 = element.point1
    var point2 = element.point2
    while (usedPoints.contains(point1) || usedPoints.contains(point2)) {
      when (element.direction) {
        DoublePointRerouteDirection.Top -> {
          point1 = Point(point1.x, point1.y - ImageSettings.lineShiftAmount)
          point2 = Point(point2.x, point2.y - ImageSettings.lineShiftAmount)
        }
        DoublePointRerouteDirection.Bottom -> {
          point1 = Point(point1.x, point1.y + ImageSettings.lineShiftAmount)
          point2 = Point(point2.x, point2.y + ImageSettings.lineShiftAmount)
        }
        DoublePointRerouteDirection.Left -> {
          point1 = Point(point1.x - ImageSettings.lineShiftAmount, point1.y)
          point2 = Point(point2.x - ImageSettings.lineShiftAmount, point2.y)
        }
        DoublePointRerouteDirection.Right -> {
          point1 = Point(point1.x + ImageSettings.lineShiftAmount, point1.y)
          point2 = Point(point2.x + ImageSettings.lineShiftAmount, point2.y)
        }
      }
    }
    return element.copy(point1 = point1, point2 = point2)
  }
}
