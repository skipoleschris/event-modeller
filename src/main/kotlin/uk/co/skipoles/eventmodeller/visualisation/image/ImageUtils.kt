package uk.co.skipoles.eventmodeller.visualisation.image

import uk.co.skipoles.eventmodeller.visualisation.PostIt
import uk.co.skipoles.eventmodeller.visualisation.VisualisationModel
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.horizontalSize
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.horizontalSpace
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.postItSize
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.verticalSize
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.verticalSpace

internal object ImagePositions {
  fun postItAtPosition(model: VisualisationModel, x: Int, y: Int, scale: Double = 1.0): PostIt? {
    val scaledX = (x / scale).toInt()
    val scaledY = (y / scale).toInt()
    return model.postIts.find {
      val xStart = (horizontalSize * it.columnIndex) + horizontalSpace
      val yStart = (verticalSize * (it.swimLane.rowIndex - 1)) + verticalSpace

      scaledX >= xStart &&
          scaledX <= (xStart + postItSize) &&
          scaledY >= yStart &&
          scaledY <= (yStart + postItSize)
    }
  }

  fun topOfPostIt(postIt: PostIt): Int =
      verticalSpace + ((postIt.swimLane.rowIndex - 1) * verticalSize)

  fun bottomOfPostIt(postIt: PostIt): Int = topOfPostIt(postIt) + postItSize

  fun leftOfPostIt(postIt: PostIt): Int = horizontalSpace + (postIt.columnIndex * horizontalSize)

  fun rightOfPostIt(postIt: PostIt): Int = leftOfPostIt(postIt) + postItSize

  fun horizontalCenterOfPostIt(postIt: PostIt): Int = leftOfPostIt(postIt) + (postItSize / 2)

  fun horizontalLeftQuarterOfPostIt(postIt: PostIt): Int = leftOfPostIt(postIt) + (postItSize / 4)

  fun horizontalRightQuarterOfPostIt(postIt: PostIt): Int = rightOfPostIt(postIt) - (postItSize / 4)

  fun verticalCenterOfPostIt(postIt: PostIt): Int = topOfPostIt(postIt) + (postItSize / 2)

  data class Position(val column: Int, val row: Int)

  fun of(postIt: PostIt) = Position(postIt.columnIndex, postIt.swimLane.rowIndex)
}
