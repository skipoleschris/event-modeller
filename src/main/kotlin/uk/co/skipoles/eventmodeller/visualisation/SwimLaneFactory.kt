package uk.co.skipoles.eventmodeller.visualisation

import uk.co.skipoles.eventmodeller.definition.ModelDefinitionContext
import uk.co.skipoles.eventmodeller.definition.ModelDefinitionContextType
import uk.co.skipoles.eventmodeller.definition.ModelDefinitionEntry
import uk.co.skipoles.eventmodeller.util.makeShortName

internal object SwimLaneFactory {

  fun determineOrderedSwimLanes(
      entries: List<ModelDefinitionEntry>
  ): List<Pair<ModelDefinitionContext, SwimLane>> =
      entries
          .flatMap { listOf(it.item.getOrDeriveContext(), it.target.getOrDeriveContext()) }
          .toSet()
          .sortedWith(ContextComparator())
          .mapIndexed { index, context ->
            Pair(
                context,
                SwimLane(
                    context.type.toSwimLaneType(),
                    index + 1,
                    !context.isDefault(),
                    context.name,
                    makeShortName(context.name)))
          }

  private class ContextComparator : Comparator<ModelDefinitionContext> {
    override fun compare(left: ModelDefinitionContext, right: ModelDefinitionContext) =
        when (val result = left.type.compareTo(right.type)) {
          0 -> {
            if (left == right) 0
            else if (left.isDefault()) -1
            else if (right.isDefault()) 1 else left.name.compareTo(right.name)
          }
          else -> result
        }
  }

  private fun ModelDefinitionContextType.toSwimLaneType() =
      when (this) {
        ModelDefinitionContextType.Saga -> SwimLaneType.Saga
        ModelDefinitionContextType.Timeline -> SwimLaneType.Timeline
        ModelDefinitionContextType.EventHandler -> SwimLaneType.Events
        ModelDefinitionContextType.Aggregate -> SwimLaneType.Aggregate
      }
}
