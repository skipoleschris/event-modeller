package uk.co.skipoles.eventmodeller.visualisation.render

import java.awt.Graphics2D
import java.awt.geom.Rectangle2D

internal fun Graphics2D.drawCentredMultiLineString(
    s: String,
    x: Int,
    y: Int,
    width: Int,
    height: Int
) {
  val lines = divideIntoLines("", s, width, areaForString(this))
  val linesThatFit = mostLinesThatFitHeight(lines, height)

  val totalHeight = linesThatFit.sumOf { it.area.height }.toInt()
  var yPosition = y + ((height - totalHeight) / 2)
  linesThatFit.forEach {
    drawString(it.s, (x + ((width - it.area.width.toInt()) / 2)), yPosition)
    yPosition += it.area.height.toInt()
  }
}

private data class LineWithArea(val s: String, val area: Rectangle2D)

private fun divideIntoLines(
    current: String,
    remainder: String,
    maxWidth: Int,
    findArea: (String) -> Rectangle2D
): List<LineWithArea> {
  if (remainder.isEmpty()) return listOf(LineWithArea(current, findArea(current)))

  val nextWord = remainder.takeWhile { it != ' ' }
  val line = if (current.isEmpty()) nextWord else "$current $nextWord"
  val lineArea = findArea(line)
  return if (lineArea.width < (maxWidth - 2)) { // Line is shorter than max width
    divideIntoLines(line, remainder.drop(nextWord.length + 1), maxWidth, findArea)
  } else if (current.isEmpty()) { // Line is longer than max width and is a single word
    val longestSubstring = longestSubstringThatFitsWidth(line, maxWidth, findArea)
    listOf(LineWithArea(longestSubstring, findArea(longestSubstring))) +
        divideIntoLines("", remainder.drop(longestSubstring.length), maxWidth, findArea)
  } else { // Line is longer than max width, so needs a line break
    listOf(LineWithArea(current, findArea(current))) +
        divideIntoLines("", remainder.trim(), maxWidth, findArea)
  }
}

private fun longestSubstringThatFitsWidth(
    word: String,
    maxWidth: Int,
    findArea: (String) -> Rectangle2D
) =
    word
        .fold(Pair("", false)) { res, ch ->
          val (s, state) = res
          if (state) res
          else {
            val test = s + ch
            val testArea = findArea(test)
            if (testArea.width < (maxWidth - 2)) Pair(test, false) else Pair(s, true)
          }
        }
        .first

private fun mostLinesThatFitHeight(lines: List<LineWithArea>, maxHeight: Int) =
    lines
        .fold(Pair(listOf<LineWithArea>(), false)) { result, line ->
          val (validLines, done) = result
          if (done) result
          else {
            val newHeight = validLines.sumOf { it.area.height } + line.area.height
            if (newHeight < (maxHeight - 2)) Pair(validLines + line, false)
            else Pair(validLines, true)
          }
        }
        .first

private fun areaForString(graphics: Graphics2D): (String) -> Rectangle2D =
    fun(s: String) = graphics.font.getStringBounds(s, graphics.fontRenderContext)
