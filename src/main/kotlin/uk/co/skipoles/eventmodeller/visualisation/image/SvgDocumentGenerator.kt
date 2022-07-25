package uk.co.skipoles.eventmodeller.visualisation.image

import java.awt.Dimension
import java.awt.Graphics2D
import org.jfree.graphics2d.svg.SVGGraphics2D
import uk.co.skipoles.eventmodeller.visualisation.PostIt
import uk.co.skipoles.eventmodeller.visualisation.VisualisationModel
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.horizontalSize
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.horizontalSpace
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.postItSize
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.verticalSize
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.verticalSpace
import uk.co.skipoles.eventmodeller.visualisation.render.drawArrow
import uk.co.skipoles.eventmodeller.visualisation.render.drawPostIt

class SvgDocumentGenerator(private val model: VisualisationModel) {

  fun renderDocument(scale: Double = 1.0): SizedAndScaledSvgImage {
    val size = imageSize()
    val graphics = SVGGraphics2D(size.width, size.height)

    val boxes =
        model.postIts.fold(listOf<AvoidanceBox>()) { result, postIt ->
          result + drawPostIt(graphics, postIt)
        }

    val paths = PathRouter.route(PathGenerator.from(model), boxes)
    paths.forEach { it.draw(graphics) }

    if (scale != 1.0) graphics.scale(scale, scale)

    return SizedAndScaledSvgImage(
        graphics.svgDocument, (size.width * scale).toInt(), (size.height * scale).toInt(), scale)
  }
  private fun imageSize(): Dimension {
    val columns = model.columns()
    val rows = model.rows()
    return Dimension(
        ((horizontalSize * columns) + horizontalSpace), ((verticalSize * rows) + verticalSpace))
  }

  private fun drawPostIt(graphics: Graphics2D, postIt: PostIt): AvoidanceBox {
    val x = ((horizontalSpace + postItSize) * postIt.columnIndex) + horizontalSpace
    val y = ((verticalSpace + postItSize) * (postIt.swimLane.rowIndex - 1)) + verticalSpace
    graphics.drawPostIt(postIt, x, y, postItSize)
    return AvoidanceBox(x, y, postItSize, postItSize)
  }

  private fun Path.draw(graphics: Graphics2D) {
    graphics.color = color
    val pointPairs = points().windowed(2)
    pointPairs.forEachIndexed { index, points ->
      val start = points.first()
      val end = points.last()
      graphics.drawLine(start.x, start.y, end.x, end.y)
      if (hasArrowTo && index == 0) graphics.drawArrow(start.x, end.x, start.y, end.y)
      if (hasArrowFrom && index == pointPairs.lastIndex)
          graphics.drawArrow(end.x, start.x, end.y, start.y)
    }
  }
}
