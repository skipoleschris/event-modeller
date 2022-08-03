package uk.co.skipoles.eventmodeller.visualisation.image

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.util.regex.Pattern
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import uk.co.skipoles.eventmodeller.visualisation.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SvgDocumentGeneratorTest {

  private val sagaSwimLane = SwimLane(SwimLaneType.Saga, 1, false, "Process")
  private val timelineSwimLane = SwimLane(SwimLaneType.Timeline, 2, false, "Timeline")
  private val eventsSwimLane = SwimLane(SwimLaneType.Events, 3, false, "Events")
  private val aggregateSwimLane = SwimLane(SwimLaneType.Aggregate, 4, false, "Aggregate")

  private val command1PostIt = CommandPostIt(timelineSwimLane, "Command 1", 1)
  private val event1PostIt = EventPostIt(eventsSwimLane, "Event 1", 1)
  private val saga1PostIt = SagaPostIt(sagaSwimLane, "Saga 1", 2)
  private val command2PostIt = CommandPostIt(timelineSwimLane, "Command 2", 3)
  private val event2PostIt = EventPostIt(aggregateSwimLane, "Event 2", 3)
  private val view1PostIt = ViewPostIt(timelineSwimLane, "View 1", 4)

  private val model =
      VisualisationModel(
          swimLanes = listOf(sagaSwimLane, timelineSwimLane, eventsSwimLane, aggregateSwimLane),
          postIts =
              listOf(
                  command1PostIt,
                  event1PostIt,
                  saga1PostIt,
                  command2PostIt,
                  event2PostIt,
                  view1PostIt),
          links =
              mapOf(
                  Pair(command1PostIt, setOf(PostItLink(event1PostIt))),
                  Pair(event1PostIt, setOf(PostItLink(saga1PostIt), PostItLink(view1PostIt))),
                  Pair(saga1PostIt, setOf(PostItLink(command2PostIt))),
                  Pair(command2PostIt, setOf(PostItLink(event2PostIt))),
                  Pair(event2PostIt, setOf(PostItLink(view1PostIt)))))

  private val documentGenerator = SvgDocumentGenerator(model)

  @Test
  fun `generates an svg document containing the expected elements`() {
    val document = documentGenerator.renderDocument()
    val xmlString = document.document

    xmlString shouldContain "Command 1"
    xmlString shouldContain "Event 1"
    xmlString shouldContain "Saga 1"
    xmlString shouldContain "Command 2"
    xmlString shouldContain "Event 2"
    xmlString shouldContain "View 1"

    xmlString.occurrences("<rect") shouldBe 6
    xmlString.occurrences("<line") shouldBe 7
  }

  private fun String.occurrences(substr: String): Int {
    val matcher = Pattern.compile(substr).matcher(this)
    var count = 0
    while (matcher.find()) count++
    return count
  }

  @Test
  fun `generates an svg document at 100 percent scale`() {
    val document = documentGenerator.renderDocument()
    document.scale shouldBe 1.0
    document.width shouldBe ((5 * ImageSettings.horizontalSize) + ImageSettings.horizontalSpace)
    document.height shouldBe ((4 * ImageSettings.verticalSize) + ImageSettings.verticalSpace)
  }

  @Test
  fun `generates an svg document at 50 percent scale`() {
    val document = documentGenerator.renderDocument(0.5)
    document.scale shouldBe 0.5
    document.width shouldBe
        (((5 * ImageSettings.horizontalSize) + ImageSettings.horizontalSpace) / 2)
    document.height shouldBe (((4 * ImageSettings.verticalSize) + ImageSettings.verticalSpace) / 2)
  }
}
