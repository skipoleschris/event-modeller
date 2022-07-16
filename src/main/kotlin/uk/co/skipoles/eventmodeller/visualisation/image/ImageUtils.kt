package uk.co.skipoles.eventmodeller.visualisation.image

import uk.co.skipoles.eventmodeller.visualisation.PostIt
import uk.co.skipoles.eventmodeller.visualisation.VisualisationModel
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.horizontalSize
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.horizontalSpace
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.postItSize
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.verticalSize
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.verticalSpace

object ImageSettings {
  const val postItSize: Int = 150
  const val horizontalSpace: Int = 75
  const val verticalSpace: Int = 75
  const val horizontalSize = postItSize + horizontalSpace
  const val verticalSize = postItSize + verticalSpace
}

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
