package uk.co.skipoles.eventmodeller.visualisation

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.util.stream.Stream
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.co.skipoles.eventmodeller.definition.*
import uk.co.skipoles.eventmodeller.definition.ModelDefinitionContext.Companion.EVENT_HANDLER_CONTEXT
import uk.co.skipoles.eventmodeller.definition.ModelDefinitionContext.Companion.PROCESS_CONTEXT
import uk.co.skipoles.eventmodeller.definition.ModelDefinitionContext.Companion.TIME_LINE_CONTEXT
import uk.co.skipoles.eventmodeller.util.makeShortName

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class VisualisationModelGeneratorTest {

  private val aggregateContext =
      ModelDefinitionContext(ModelDefinitionContextType.Aggregate, "test.Aggregate")

  private val command1 = ModelDefinitionItem(ModelDefinitionItemType.Command, "test.Command1")
  private val event1 = ModelDefinitionItem(ModelDefinitionItemType.Event, "test.Event1")
  private val saga1 = ModelDefinitionItem(ModelDefinitionItemType.Saga, "test.Saga1")
  private val command2 = ModelDefinitionItem(ModelDefinitionItemType.Command, "test.Command2")
  private val event2 =
      ModelDefinitionItem(ModelDefinitionItemType.Event, "test.Event2", aggregateContext)
  private val view = ModelDefinitionItem(ModelDefinitionItemType.View, "test.View")
  private val saga2 = ModelDefinitionItem(ModelDefinitionItemType.Saga, "test.Saga2")
  private val command3 = ModelDefinitionItem(ModelDefinitionItemType.Command, "test.Command3")
  private val event3 =
      ModelDefinitionItem(ModelDefinitionItemType.Event, "test.Event3", aggregateContext)

  private val definition =
      EventModelDefinition(
          listOf(
              ModelDefinitionEntry(command1, event1),
              ModelDefinitionEntry(event1, saga1),
              ModelDefinitionEntry(saga1, command2),
              ModelDefinitionEntry(command2, event2),
              ModelDefinitionEntry(event2, view),
              ModelDefinitionEntry(event2, saga2),
              ModelDefinitionEntry(saga2, command3),
              ModelDefinitionEntry(command3, event3),
              ModelDefinitionEntry(event3, view)))

  private val visualisation = VisualisationModelGenerator.generate(definition)

  private val processSwimLane =
      SwimLane(SwimLaneType.Saga, 1, false, PROCESS_CONTEXT.name, PROCESS_CONTEXT.name)
  private val timelineSwimLane =
      SwimLane(SwimLaneType.Timeline, 2, false, TIME_LINE_CONTEXT.name, TIME_LINE_CONTEXT.name)
  private val eventsSwimLane =
      SwimLane(
          SwimLaneType.Events, 3, false, EVENT_HANDLER_CONTEXT.name, EVENT_HANDLER_CONTEXT.name)
  private val aggregateSwimLane =
      SwimLane(SwimLaneType.Aggregate, 4, true, "test.Aggregate", "Aggregate")

  val aggregatePostIt = LabelPostIt(aggregateSwimLane, aggregateSwimLane.shortName, 0)
  val command1PostIt = CommandPostIt(timelineSwimLane, makeShortName(command1.name), 1)
  val event1PostIt = EventPostIt(eventsSwimLane, makeShortName(event1.name), 1)
  val saga1PostIt = SagaPostIt(processSwimLane, makeShortName(saga1.name), 2)
  val command2PostIt = CommandPostIt(timelineSwimLane, makeShortName(command2.name), 3)
  val event2PostIt = EventPostIt(aggregateSwimLane, makeShortName(event2.name), 3)
  val viewPostIt = ViewPostIt(timelineSwimLane, makeShortName(view.name), 4)
  val saga2PostIt = SagaPostIt(processSwimLane, makeShortName(saga2.name), 5)
  val command3PostIt = CommandPostIt(timelineSwimLane, makeShortName(command3.name), 6)
  val event3PostIt = EventPostIt(aggregateSwimLane, makeShortName(event3.name), 6)

  @Test
  fun `visualisation should be of the expected size`() {
    visualisation.rows() shouldBe 4
    visualisation.columns() shouldBe 7
  }

  @Test
  fun `only the expected number of swim lanes are present`() {
    visualisation.swimLanes.size shouldBe 4
  }

  @ParameterizedTest
  @MethodSource("expectedSwimLanes")
  fun `expected swim lanes are present`(data: ExpectedSwimLane) {
    visualisation.swimLanes[data.index] shouldBe data.swimLane
  }

  data class ExpectedSwimLane(val index: Int, val swimLane: SwimLane)

  private fun expectedSwimLanes() =
      Stream.of(
          ExpectedSwimLane(0, processSwimLane),
          ExpectedSwimLane(1, timelineSwimLane),
          ExpectedSwimLane(2, eventsSwimLane),
          ExpectedSwimLane(3, aggregateSwimLane),
      )

  @Test
  fun `only the expected number of postIts are present`() {
    visualisation.postIts.size shouldBe 10
  }

  @ParameterizedTest
  @MethodSource("expectedPostIts")
  fun `expected postIts are present in the correct positions`(data: ExpectedPostIt) {
    visualisation.postIts[data.index] shouldBe data.postIt
  }

  data class ExpectedPostIt(val index: Int, val postIt: PostIt)

  private fun expectedPostIts() =
      Stream.of(
          ExpectedPostIt(0, aggregatePostIt),
          ExpectedPostIt(1, command1PostIt),
          ExpectedPostIt(2, event1PostIt),
          ExpectedPostIt(3, saga1PostIt),
          ExpectedPostIt(4, command2PostIt),
          ExpectedPostIt(5, event2PostIt),
          ExpectedPostIt(6, viewPostIt),
          ExpectedPostIt(7, saga2PostIt),
          ExpectedPostIt(8, command3PostIt),
          ExpectedPostIt(9, event3PostIt))

  @Test
  fun `only the expected number of postIts are linked`() {
    visualisation.links.size shouldBe 8
  }

  @ParameterizedTest
  @MethodSource("expectedLinks")
  fun `expected links are present`(data: ExpectedLink) {
    visualisation.links.containsKey(data.from).shouldBeTrue()
    val links = visualisation.links[data.from]
    links shouldContainExactly data.links
  }

  data class ExpectedLink(val from: PostIt, val links: Set<PostItLink>)

  private fun expectedLinks() =
      Stream.of(
          ExpectedLink(command1PostIt, setOf(PostItLink(event1PostIt, false))),
          ExpectedLink(event1PostIt, setOf(PostItLink(saga1PostIt, false))),
          ExpectedLink(saga1PostIt, setOf(PostItLink(command2PostIt, false))),
          ExpectedLink(command2PostIt, setOf(PostItLink(event2PostIt, false))),
          ExpectedLink(
              event2PostIt, setOf(PostItLink(saga2PostIt, false), PostItLink(viewPostIt, false))),
          ExpectedLink(saga2PostIt, setOf(PostItLink(command3PostIt, false))),
          ExpectedLink(command3PostIt, setOf(PostItLink(event3PostIt, false))),
          ExpectedLink(event3PostIt, setOf(PostItLink(viewPostIt, false))))

  @Test
  fun `postIts that point link to each other should have a bidirectional link`() {
    val def =
        EventModelDefinition(
            listOf(ModelDefinitionEntry(event1, event2), ModelDefinitionEntry(event2, event1)))
    val model = VisualisationModelGenerator.generate(def)

    val fromPostIt =
        EventPostIt(
            SwimLane(
                SwimLaneType.Events,
                1,
                false,
                EVENT_HANDLER_CONTEXT.name,
                EVENT_HANDLER_CONTEXT.name),
            makeShortName(event1.name),
            1)
    val toPostIt =
        EventPostIt(
            SwimLane(SwimLaneType.Aggregate, 2, true, "test.Aggregate", "Aggregate"),
            makeShortName(event2.name),
            2)

    model.links.size shouldBe 1
    model.links[fromPostIt] shouldContainExactly setOf(PostItLink(toPostIt, true))
  }
}
