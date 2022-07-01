package uk.co.skipoles.eventmodeller.visualisation

import io.kotest.matchers.collections.shouldContainExactly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import uk.co.skipoles.eventmodeller.definition.*
import uk.co.skipoles.eventmodeller.definition.ModelDefinitionContext.Companion.EVENT_HANDLER_CONTEXT
import uk.co.skipoles.eventmodeller.definition.ModelDefinitionContext.Companion.PROCESS_CONTEXT
import uk.co.skipoles.eventmodeller.definition.ModelDefinitionContext.Companion.TIME_LINE_CONTEXT

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SwimLaneFactoryTest {

  private val customTimeline =
      ModelDefinitionContext(ModelDefinitionContextType.Timeline, "test.CustomTimeline")
  private val customEventHandler =
      ModelDefinitionContext(ModelDefinitionContextType.EventHandler, "test.CustomEventHandler")
  private val aggregate1 =
      ModelDefinitionContext(ModelDefinitionContextType.Aggregate, "test.Aggregate1")
  private val aggregate2 =
      ModelDefinitionContext(ModelDefinitionContextType.Aggregate, "test.Aggregate2")
  private val customProcess =
      ModelDefinitionContext(ModelDefinitionContextType.Saga, "test.CustomProcess")

  private val commandInDefaultContext =
      ModelDefinitionItem(ModelDefinitionItemType.Command, "Command 1", null)
  private val commandInCustomContext =
      ModelDefinitionItem(ModelDefinitionItemType.Command, "Command 2", customTimeline)

  private val eventInDefaultContext =
      ModelDefinitionItem(ModelDefinitionItemType.Event, "Event 1", null)
  private val eventInAggregate1Context =
      ModelDefinitionItem(ModelDefinitionItemType.Event, "Event 2", aggregate1)
  private val eventInAggregate2Context =
      ModelDefinitionItem(ModelDefinitionItemType.Event, "Event 3", aggregate2)
  private val eventInCustomContext =
      ModelDefinitionItem(ModelDefinitionItemType.Event, "Event 4", customEventHandler)

  private val viewInDefaultContext =
      ModelDefinitionItem(ModelDefinitionItemType.View, "View 1", null)
  private val viewInCustomContext =
      ModelDefinitionItem(ModelDefinitionItemType.View, "View 2", customTimeline)

  private val sagaInDefaultContext =
      ModelDefinitionItem(ModelDefinitionItemType.Saga, "Saga 1", null)
  private val sagaInCustomContext =
      ModelDefinitionItem(ModelDefinitionItemType.Saga, "Saga 2", customProcess)

  @Test
  fun `produces a correctly order list of swim lanes`() {
    val definitionEntries =
        listOf(
            ModelDefinitionEntry(commandInDefaultContext, eventInDefaultContext),
            ModelDefinitionEntry(eventInDefaultContext, sagaInDefaultContext),
            ModelDefinitionEntry(sagaInDefaultContext, commandInCustomContext),
            ModelDefinitionEntry(eventInAggregate2Context, viewInDefaultContext),
            ModelDefinitionEntry(eventInAggregate2Context, sagaInCustomContext),
            ModelDefinitionEntry(sagaInCustomContext, commandInDefaultContext),
            ModelDefinitionEntry(commandInDefaultContext, eventInCustomContext),
            ModelDefinitionEntry(eventInCustomContext, viewInCustomContext),
            ModelDefinitionEntry(eventInAggregate1Context, viewInCustomContext))

    val swimLanes = SwimLaneFactory.determineOrderedSwimLanes(definitionEntries)

    swimLanes.shouldContainExactly(
        Pair(
            PROCESS_CONTEXT,
            SwimLane(SwimLaneType.Saga, 1, false, PROCESS_CONTEXT.name, PROCESS_CONTEXT.name)),
        Pair(
            customProcess,
            SwimLane(SwimLaneType.Saga, 2, true, customProcess.name, "Custom Process")),
        Pair(
            TIME_LINE_CONTEXT,
            SwimLane(
                SwimLaneType.Timeline, 3, false, TIME_LINE_CONTEXT.name, TIME_LINE_CONTEXT.name)),
        Pair(
            customTimeline,
            SwimLane(SwimLaneType.Timeline, 4, true, customTimeline.name, "Custom Timeline")),
        Pair(
            EVENT_HANDLER_CONTEXT,
            SwimLane(
                SwimLaneType.Events,
                5,
                false,
                EVENT_HANDLER_CONTEXT.name,
                EVENT_HANDLER_CONTEXT.name)),
        Pair(
            customEventHandler,
            SwimLane(
                SwimLaneType.Events, 6, true, customEventHandler.name, "Custom Event Handler")),
        Pair(aggregate1, SwimLane(SwimLaneType.Aggregate, 7, true, aggregate1.name, "Aggregate1")),
        Pair(aggregate2, SwimLane(SwimLaneType.Aggregate, 8, true, aggregate2.name, "Aggregate2")))
  }
}
