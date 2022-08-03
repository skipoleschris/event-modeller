package uk.co.skipoles.eventmodeller.visualisation.image

import io.kotest.matchers.collections.shouldContainExactly
import java.util.stream.Stream
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import uk.co.skipoles.eventmodeller.visualisation.image.ImageSettings.lineShiftAmount

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShiftDuplicateIntermediatePathRouterTest {

  private val simplePath1 = Path(Point(0, 0), Point(100, 100))
  private val simplePath2 = Path(Point(100, 0), Point(0, 100))

  @ParameterizedTest
  @MethodSource("singleReroutingPaths")
  fun `can route a single duplicate rerouting point further away from the avoidance point`(
      data: ReroutingData
  ) {
    val path1 = simplePath1.insertAfter(simplePath1.elements.first(), data.path1ReroutingElement)
    val path2 = simplePath2.insertAfter(simplePath2.elements.first(), data.path2ReroutingElement)
    val movedPath2 =
        simplePath2.insertAfter(simplePath2.elements.first(), data.path2MovedReroutingElement)
    val initialPaths = listOf(path1, path2)
    val expectedPaths = listOf(path1, movedPath2)
    val resultPaths = ShiftDuplicateIntermediatesPathRouter.adjustDuplicatedPathPoints(initialPaths)
    resultPaths shouldContainExactly expectedPaths
  }

  private fun singleReroutingPaths() =
      Stream.of(
          ReroutingData(
              ReroutedPathElement(Point(40, 60), SinglePointRerouteDirection.TopLeft),
              ReroutedPathElement(Point(40, 60), SinglePointRerouteDirection.TopLeft),
              ReroutedPathElement(
                  Point(40 - lineShiftAmount, 60 - lineShiftAmount),
                  SinglePointRerouteDirection.TopLeft),
          ),
          ReroutingData(
              ReroutedPathElement(Point(40, 60), SinglePointRerouteDirection.TopRight),
              ReroutedPathElement(Point(40, 60), SinglePointRerouteDirection.TopRight),
              ReroutedPathElement(
                  Point(40 + lineShiftAmount, 60 - lineShiftAmount),
                  SinglePointRerouteDirection.TopRight),
          ),
          ReroutingData(
              ReroutedPathElement(Point(40, 60), SinglePointRerouteDirection.BottomRight),
              ReroutedPathElement(Point(40, 60), SinglePointRerouteDirection.BottomRight),
              ReroutedPathElement(
                  Point(40 + lineShiftAmount, 60 + lineShiftAmount),
                  SinglePointRerouteDirection.BottomRight),
          ),
          ReroutingData(
              ReroutedPathElement(Point(40, 60), SinglePointRerouteDirection.BottomLeft),
              ReroutedPathElement(Point(40, 60), SinglePointRerouteDirection.BottomLeft),
              ReroutedPathElement(
                  Point(40 - lineShiftAmount, 60 + lineShiftAmount),
                  SinglePointRerouteDirection.BottomLeft),
          ))

  @ParameterizedTest
  @MethodSource("doubleReroutingPaths")
  fun `can route a double duplicate rerouting point further away from the avoidance point`(
      data: ReroutingData
  ) {
    val path1 = simplePath1.insertAfter(simplePath1.elements.first(), data.path1ReroutingElement)
    val path2 = simplePath2.insertAfter(simplePath2.elements.first(), data.path2ReroutingElement)
    val movedPath2 =
        simplePath2.insertAfter(simplePath2.elements.first(), data.path2MovedReroutingElement)
    val initialPaths = listOf(path1, path2)
    val expectedPaths = listOf(path1, movedPath2)
    val resultPaths = ShiftDuplicateIntermediatesPathRouter.adjustDuplicatedPathPoints(initialPaths)
    resultPaths shouldContainExactly expectedPaths
  }

  private fun doubleReroutingPaths() =
      Stream.of(
          ReroutingData(
              ReroutedPathElement(Point(40, 60), SinglePointRerouteDirection.TopLeft),
              DoubleReroutedPathElement(
                  Point(40, 60), Point(70, 60), DoublePointRerouteDirection.Top),
              DoubleReroutedPathElement(
                  Point(40, 60 - lineShiftAmount),
                  Point(70, 60 - lineShiftAmount),
                  DoublePointRerouteDirection.Top),
          ),
          ReroutingData(
              ReroutedPathElement(Point(40, 60), SinglePointRerouteDirection.TopRight),
              DoubleReroutedPathElement(
                  Point(40, 60), Point(40, 80), DoublePointRerouteDirection.Right),
              DoubleReroutedPathElement(
                  Point(40 + lineShiftAmount, 60),
                  Point(40 + lineShiftAmount, 80),
                  DoublePointRerouteDirection.Right),
          ),
          ReroutingData(
              ReroutedPathElement(Point(40, 60), SinglePointRerouteDirection.BottomRight),
              DoubleReroutedPathElement(
                  Point(40, 60), Point(70, 60), DoublePointRerouteDirection.Bottom),
              DoubleReroutedPathElement(
                  Point(40, 60 + lineShiftAmount),
                  Point(70, 60 + lineShiftAmount),
                  DoublePointRerouteDirection.Bottom),
          ),
          ReroutingData(
              ReroutedPathElement(Point(40, 60), SinglePointRerouteDirection.BottomLeft),
              DoubleReroutedPathElement(
                  Point(40, 60), Point(40, 80), DoublePointRerouteDirection.Left),
              DoubleReroutedPathElement(
                  Point(40 - lineShiftAmount, 60),
                  Point(40 - lineShiftAmount, 80),
                  DoublePointRerouteDirection.Left),
          ))

  data class ReroutingData(
      val path1ReroutingElement: PathElement,
      val path2ReroutingElement: PathElement,
      val path2MovedReroutingElement: PathElement
  )
}
