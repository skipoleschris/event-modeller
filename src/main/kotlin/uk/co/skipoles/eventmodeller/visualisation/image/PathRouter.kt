package uk.co.skipoles.eventmodeller.visualisation.image

internal object PathRouter {

  fun route(paths: List<Path>, boxes: List<AvoidanceBox>): List<Path> =
      ShiftDuplicateIntermediatesPathRouter.adjustDuplicatedPathPoints(
          AvoidBoxesPathRouter.routeAroundBoxes(paths, boxes))
}
