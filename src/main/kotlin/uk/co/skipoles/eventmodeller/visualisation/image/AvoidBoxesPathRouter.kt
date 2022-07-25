package uk.co.skipoles.eventmodeller.visualisation.image

internal object AvoidBoxesPathRouter {

  fun routeAroundBoxes(paths: List<Path>, boxes: List<AvoidanceBox>): List<Path> {
    val intersections = IntersectionFinder.between(paths, boxes)
    return if (intersections.isEmpty()) paths
    else {
      val routedPaths = intersections.map(::splitPath)
      val pathsNotNeedingRouting = paths - (intersections.map { it.path }).toSet()
      pathsNotNeedingRouting + routeAroundBoxes(routedPaths, boxes)
    }
  }

  private fun splitPath(intersection: Intersection): Path {
    val splitPoints = moveLineOutside(intersection)
    return intersection.path.insertAfter(intersection.from, splitPoints)
  }

  private fun moveLineOutside(
      intersection: Intersection,
  ): PathElement {
    val entryPoint = intersection.entryPoint
    val exitPoint = intersection.exitPoint
    val box = intersection.box
    return when (box.sectorFor(entryPoint)) {
      BoxSector.LeftUpper ->
          when (box.sectorFor(exitPoint)) {
            BoxSector.RightUpper -> box.topLinePoints(true)
            BoxSector.RightLower -> box.topLinePoints(true)
            BoxSector.BottomLeft -> box.bottomLeftLinePoint()
            BoxSector.BottomRight -> box.bottomLeftLinePoint()
            else -> box.topLeftLinePoint()
          }
      BoxSector.RightUpper ->
          when (box.sectorFor(exitPoint)) {
            BoxSector.LeftUpper -> box.topLinePoints(false)
            BoxSector.LeftLower -> box.topLinePoints(false)
            BoxSector.BottomLeft -> box.bottomRightLinePoint()
            BoxSector.BottomRight -> box.bottomRightLinePoint()
            else -> box.topRightLinePoint()
          }
      BoxSector.TopLeft ->
          when (box.sectorFor(exitPoint)) {
            BoxSector.RightUpper -> box.topRightLinePoint()
            BoxSector.RightLower -> box.topRightLinePoint()
            BoxSector.BottomLeft -> box.leftLinePoints(true)
            BoxSector.BottomRight -> box.leftLinePoints(true)
            else -> box.topLeftLinePoint()
          }
      BoxSector.TopRight ->
          when (box.sectorFor(exitPoint)) {
            BoxSector.LeftUpper -> box.topLeftLinePoint()
            BoxSector.LeftLower -> box.topLeftLinePoint()
            BoxSector.BottomLeft -> box.rightLinePoints(true)
            BoxSector.BottomRight -> box.rightLinePoints(true)
            else -> box.topRightLinePoint()
          }
      BoxSector.LeftLower ->
          when (box.sectorFor(exitPoint)) {
            BoxSector.RightUpper -> box.bottomLinePoints(true)
            BoxSector.TopLeft -> box.topLeftLinePoint()
            BoxSector.TopRight -> box.topLeftLinePoint()
            BoxSector.RightLower -> box.bottomLinePoints(true)
            else -> box.bottomLeftLinePoint()
          }
      BoxSector.RightLower ->
          when (box.sectorFor(exitPoint)) {
            BoxSector.LeftUpper -> box.bottomLinePoints(false)
            BoxSector.TopLeft -> box.topRightLinePoint()
            BoxSector.TopRight -> box.topRightLinePoint()
            BoxSector.LeftLower -> box.bottomLinePoints(false)
            else -> box.bottomRightLinePoint()
          }
      BoxSector.BottomLeft ->
          when (box.sectorFor(exitPoint)) {
            BoxSector.RightUpper -> box.bottomRightLinePoint()
            BoxSector.TopLeft -> box.leftLinePoints(false)
            BoxSector.TopRight -> box.leftLinePoints(false)
            BoxSector.RightLower -> box.bottomRightLinePoint()
            else -> box.bottomLeftLinePoint()
          }
      BoxSector.BottomRight ->
          when (box.sectorFor(exitPoint)) {
            BoxSector.LeftUpper -> box.bottomLeftLinePoint()
            BoxSector.TopLeft -> box.rightLinePoints(false)
            BoxSector.TopRight -> box.rightLinePoints(false)
            BoxSector.LeftLower -> box.bottomLeftLinePoint()
            else -> box.bottomRightLinePoint()
          }
    }
  }
}
