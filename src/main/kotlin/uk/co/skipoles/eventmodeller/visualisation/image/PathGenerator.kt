package uk.co.skipoles.eventmodeller.visualisation.image

import uk.co.skipoles.eventmodeller.visualisation.PostIt
import uk.co.skipoles.eventmodeller.visualisation.VisualisationModel
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.bottomOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.horizontalCenterOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.horizontalLeftQuarterOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.horizontalRightQuarterOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.leftOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.rightOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.topOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.verticalCenterOfPostIt

internal object PathGenerator {

  fun from(model: VisualisationModel): List<Path> =
      model.postIts.flatMap { generateDirectPaths(model, it) }

  private fun generateDirectPaths(model: VisualisationModel, postIt: PostIt): List<Path> =
      model.links[postIt]?.map { generateDirectPath(it.postIt, postIt, it.bidirectional) }
          ?: emptyList()

  private fun generateDirectPath(from: PostIt, to: PostIt, bidirectional: Boolean): Path {
    val (fromX, toX) = xPositionsRelativeToPostIts(from, to)
    val (fromY, toY) = yPositionsRelativeToPostIts(from, to)
    return Path(Point(fromX, fromY), Point(toX, toY), bidirectional, true, to.color)
  }

  private fun xPositionsRelativeToPostIts(from: PostIt, to: PostIt): Pair<Int, Int> {
    val fromPosition = ImagePositions.of(from)
    val toPosition = ImagePositions.of(to)
    return if (fromPosition.column == toPosition.column) { // Same column
      Pair(horizontalCenterOfPostIt(from), horizontalCenterOfPostIt(to))
    } else if (fromPosition.column < toPosition.column) { // Left to right
      if (fromPosition.row == toPosition.row) // Same row
       Pair(rightOfPostIt(from), leftOfPostIt(to))
      else Pair(horizontalRightQuarterOfPostIt(from), horizontalLeftQuarterOfPostIt(to))
    } else { // Right to left
      if (fromPosition.row == toPosition.row) // Same row
       Pair(leftOfPostIt(from), rightOfPostIt(to))
      else Pair(horizontalLeftQuarterOfPostIt(from), horizontalRightQuarterOfPostIt(to))
    }
  }

  private fun yPositionsRelativeToPostIts(from: PostIt, to: PostIt): Pair<Int, Int> {
    val fromPosition = ImagePositions.of(from)
    val toPosition = ImagePositions.of(to)
    return if (fromPosition.row == toPosition.row) { // Same row
      Pair(verticalCenterOfPostIt(from), verticalCenterOfPostIt(to))
    } else if (fromPosition.row < toPosition.row) { // Top to bottom
      Pair(bottomOfPostIt(from), topOfPostIt(to))
    } else { // Bottom to top
      Pair(topOfPostIt(from), bottomOfPostIt(to))
    }
  }
}
