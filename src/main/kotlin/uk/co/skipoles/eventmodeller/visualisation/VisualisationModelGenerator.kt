package uk.co.skipoles.eventmodeller.visualisation

import uk.co.skipoles.eventmodeller.definition.EventModelDefinition
import uk.co.skipoles.eventmodeller.definition.ModelDefinitionItem
import uk.co.skipoles.eventmodeller.definition.ModelDefinitionItemType
import uk.co.skipoles.eventmodeller.util.makeShortName

object VisualisationModelGenerator {

  fun generate(definition: EventModelDefinition): VisualisationModel {
    val state = VisualisationState(definition)

    definition.entries.forEach { entry ->
      val fromPostIt = state.addItem(entry.item)
      val toPostIt = state.addItem(entry.target)
      state.addLink(fromPostIt, toPostIt)
    }

    return VisualisationModel(state.getSwimLanes(), state.getPostIts(), state.getLinks())
  }

  private class VisualisationState(definition: EventModelDefinition) {
    private val swimLanes = SwimLaneFactory.determineOrderedSwimLanes(definition.entries)
    private val postIts = mutableListOf<PostIt>()
    private val links = mutableMapOf<PostIt, Set<PostItLink>>()
    private var currentColumn: Int = 1

    fun getSwimLanes(): List<SwimLane> = swimLanes.map { it.second }
    fun getPostIts(): List<PostIt> = swimLanePostIts() + postIts
    fun getLinks(): Map<PostIt, Set<PostItLink>> = links.toMap()

    private fun swimLanePostIts(): List<PostIt> =
        swimLanes
            .map { it.second }
            .filter { it.requiresLabel }
            .map { LabelPostIt(it, it.shortName) }

    fun addItem(item: ModelDefinitionItem): PostIt {
      val swimLane = swimLaneFor(item)
      val text = makeShortName(item.name)
      return when (item.type) {
        ModelDefinitionItemType.Command -> addCommandPostIt(swimLane, text)
        ModelDefinitionItemType.Event -> addEventPostIt(swimLane, text)
        ModelDefinitionItemType.View -> addViewPostIt(swimLane, text)
        ModelDefinitionItemType.Saga -> addSagaPostIt(swimLane, text)
      }
    }

    private fun addCommandPostIt(swimLane: SwimLane, text: String): PostIt {
      val existing =
          postIts.find { it is CommandPostIt && it.text == text && it.swimLane == swimLane }
      return if (existing != null) existing
      else {
        if (hasNonEventPostItInCurrentColumn()) currentColumn++
        val newPostIt = CommandPostIt(swimLane, text, currentColumn)
        postIts.add(newPostIt)
        newPostIt
      }
    }

    private fun addEventPostIt(swimLane: SwimLane, text: String): PostIt {
      val existing =
          postIts.find { it is EventPostIt && it.text == text && it.swimLane == swimLane }
      return if (existing != null) existing
      else {
        val newPostIt = EventPostIt(swimLane, text, currentColumn)
        currentColumn++
        postIts.add(newPostIt)
        newPostIt
      }
    }

    private fun addViewPostIt(swimLane: SwimLane, text: String): PostIt {
      val previous =
          postIts.findLast { it is ViewPostIt && it.text == text && it.swimLane == swimLane }
      return if (previous != null && previous.columnIndex > (currentColumn - 5)) previous
      else {
        if (hasNonEventPostItInCurrentColumn()) currentColumn++
        val newPostIt = ViewPostIt(swimLane, text, currentColumn)
        postIts.add(newPostIt)
        newPostIt
      }
    }

    private fun addSagaPostIt(swimLane: SwimLane, text: String): PostIt {
      val previous =
          postIts.findLast { it is SagaPostIt && it.text == text && it.swimLane == swimLane }
      return if (previous != null && previous.columnIndex > (currentColumn - 5)) previous
      else {
        if (hasNonEventPostItInCurrentColumn()) currentColumn++
        val newPostIt = SagaPostIt(swimLane, text, currentColumn)
        postIts.add(newPostIt)
        newPostIt
      }
    }

    private fun swimLaneFor(item: ModelDefinitionItem) =
        swimLanes.find { it.first == item.getOrDeriveContext() }!!.second

    private fun hasNonEventPostItInCurrentColumn() =
        postIts.find { it !is EventPostIt && it.columnIndex == currentColumn } != null

    fun addLink(fromPostIt: PostIt, toPostIt: PostIt) {
      val reverse = links[toPostIt]?.find { it.postIt == fromPostIt }
      if (reverse != null) {
        links[toPostIt] = links[toPostIt]!! - reverse + PostItLink(fromPostIt, true)
      } else {
        val targets = links.getOrDefault(fromPostIt, setOf())
        links[fromPostIt] = targets + PostItLink(toPostIt)
      }
    }
  }
}
