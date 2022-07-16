package uk.co.skipoles.eventmodeller.visualisation.image

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.StringReader
import javax.imageio.ImageIO
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder

data class SizedAndScaledSvgImage(
    val document: String,
    val width: Int,
    val height: Int,
    val scale: Double
)

fun SizedAndScaledSvgImage.asPNG(): BufferedImage {
  val transcoder = PNGTranscoder()
  transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width.toFloat())
  transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height.toFloat())
  transcoder.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.white)

  val input = TranscoderInput(StringReader(document))
  val stream = ByteArrayOutputStream()
  val output = TranscoderOutput(stream)
  transcoder.transcode(input, output)

  stream.flush()
  stream.close()

  val imageData = stream.toByteArray()
  return ImageIO.read(ByteArrayInputStream(imageData))
}
