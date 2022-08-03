package uk.co.skipoles.eventmodeller.visualisation.image

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import uk.co.skipoles.eventmodeller.visualisation.*
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.bottomOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.horizontalCenterOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.horizontalLeftQuarterOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.horizontalRightQuarterOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.leftOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.rightOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.topOfPostIt
import uk.co.skipoles.eventmodeller.visualisation.image.ImagePositions.verticalCenterOfPostIt

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PathGeneratorTest {

  private val timeline = SwimLane(SwimLaneType.Timeline, 1, false, "timeline")
  private val eventHandlers = SwimLane(SwimLaneType.Events, 2, false, "events")

  private val command1 = CommandPostIt(timeline, "Command1", 1)
  private val event1 = EventPostIt(eventHandlers, "Event1", 1)
  private val view1 = ViewPostIt(timeline, "View1", 2)
  private val command2 = CommandPostIt(timeline, "Command2", 3)
  private val event2 = EventPostIt(eventHandlers, "Event2", 3)
  private val event3 = EventPostIt(eventHandlers, "Event3", 4)

  private val model =
      VisualisationModel(
          listOf(timeline, eventHandlers),
          listOf(command1, event1, view1, command2, event2, event3),
          mapOf(
              Pair(command1, setOf(PostItLink(event1, false))),
              Pair(event1, setOf(PostItLink(view1, false), PostItLink(command2, false))),
              Pair(command2, setOf(PostItLink(event2, true))),
              Pair(event2, setOf(PostItLink(event3, false))),
              Pair(event3, setOf(PostItLink(command1, false), PostItLink(event1, false)))))

  @Test
  fun `generates a set of paths for the model`() {
    val paths = PathGenerator.from(model).sortedWith(PathComparator())
    paths.shouldHaveSize(7)
    paths shouldContainExactly
        listOf(
            Path(
                Point(horizontalCenterOfPostIt(command1), bottomOfPostIt(command1)),
                Point(horizontalCenterOfPostIt(event1), topOfPostIt(event1)),
                hasArrowFrom = false,
                hasArrowTo = true,
                command1.color),
            Path(
                Point(horizontalRightQuarterOfPostIt(event1), topOfPostIt(event1)),
                Point(horizontalLeftQuarterOfPostIt(view1), bottomOfPostIt(view1)),
                hasArrowFrom = false,
                hasArrowTo = true,
                event1.color),
            Path(
                Point(horizontalRightQuarterOfPostIt(event1), topOfPostIt(event1)),
                Point(horizontalLeftQuarterOfPostIt(command2), bottomOfPostIt(command2)),
                hasArrowFrom = false,
                hasArrowTo = true,
                event1.color),
            Path(
                Point(horizontalCenterOfPostIt(command2), bottomOfPostIt(command2)),
                Point(horizontalCenterOfPostIt(event2), topOfPostIt(event2)),
                hasArrowFrom = true,
                hasArrowTo = true,
                command2.color),
            Path(
                Point(rightOfPostIt(event2), verticalCenterOfPostIt(event2)),
                Point(leftOfPostIt(event3), verticalCenterOfPostIt(event3)),
                hasArrowFrom = false,
                hasArrowTo = true,
                event2.color),
            Path(
                Point(leftOfPostIt(event3), verticalCenterOfPostIt(event3)),
                Point(rightOfPostIt(event1), verticalCenterOfPostIt(event1)),
                hasArrowFrom = false,
                hasArrowTo = true,
                event3.color),
            Path(
                Point(horizontalLeftQuarterOfPostIt(event3), topOfPostIt(event3)),
                Point(horizontalRightQuarterOfPostIt(command1), bottomOfPostIt(command1)),
                hasArrowFrom = false,
                hasArrowTo = true,
                event3.color),
        )
  }

  private class PathComparator : Comparator<Path> {
    override fun compare(path1: Path, path2: Path): Int {
      val p1 = path1.points().first()
      val p2 = path2.points().first()
      val x = p1.x.compareTo(p2.x)
      return if (x == 0) p1.y.compareTo(p2.y) else x
    }
  }
}
