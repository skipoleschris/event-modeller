package uk.co.skipoles.eventmodeller.visualisation

import java.awt.Color
import uk.co.skipoles.eventmodeller.util.makeShortName

data class VisualisationModel(
    val swimLanes: List<SwimLane>,
    val postIts: List<PostIt>,
    val links: Map<PostIt, Set<PostItLink>>
) {
  fun rows() = swimLanes.size
  fun columns() = postIts.maxOf { it.columnIndex } + 1
}

enum class SwimLaneType {
  Saga,
  Timeline,
  Events,
  Aggregate
}

data class SwimLane(
    val type: SwimLaneType,
    val rowIndex: Int,
    val requiresLabel: Boolean,
    val name: String,
    val shortName: String = makeShortName(name)
)

interface PostIt {
  val swimLane: SwimLane
  val text: String
  val columnIndex: Int
  val color: Color

  companion object {
    val COMMAND_COLOR = Color(0x56, 0xC4, 0xE8)
    val EVENT_COLOR = Color(0xFA, 0xA4, 0x57)
    val VIEW_COLOR = Color(0xD0, 0xE0, 0x68)
    val SAGA_COLOR = Color(0xCC, 0xCC, 0xCC)
    val LABEL_COLOR = Color(0xFF, 0xE4, 0x76)
  }
}

data class CommandPostIt(
    override val swimLane: SwimLane,
    override val text: String,
    override val columnIndex: Int,
    override val color: Color = PostIt.COMMAND_COLOR
) : PostIt

data class EventPostIt(
    override val swimLane: SwimLane,
    override val text: String,
    override val columnIndex: Int,
    override val color: Color = PostIt.EVENT_COLOR
) : PostIt

data class ViewPostIt(
    override val swimLane: SwimLane,
    override val text: String,
    override val columnIndex: Int,
    override val color: Color = PostIt.VIEW_COLOR
) : PostIt

data class SagaPostIt(
    override val swimLane: SwimLane,
    override val text: String,
    override val columnIndex: Int,
    override val color: Color = PostIt.SAGA_COLOR
) : PostIt

data class LabelPostIt(
    override val swimLane: SwimLane,
    override val text: String,
    override val columnIndex: Int = 0,
    override val color: Color = PostIt.LABEL_COLOR
) : PostIt

data class PostItLink(val postIt: PostIt, val bidirectional: Boolean = false)
