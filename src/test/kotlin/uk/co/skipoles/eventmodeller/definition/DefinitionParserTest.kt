package uk.co.skipoles.eventmodeller.definition

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.util.stream.Stream
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefinitionParserTest {

  private val sagaContext =
      ModelDefinitionContext(ModelDefinitionContextType.Saga, "Custom Process")
  private val timelineContext =
      ModelDefinitionContext(ModelDefinitionContextType.Timeline, "Custom Timeline")
  private val eventHandlerContext =
      ModelDefinitionContext(ModelDefinitionContextType.EventHandler, "Custom Event Handler")
  private val aggregateContext =
      ModelDefinitionContext(ModelDefinitionContextType.Aggregate, "Custom Aggregate")

  private val testCommand = ModelDefinitionItem(ModelDefinitionItemType.Command, "Test Command")
  private val testEvent = ModelDefinitionItem(ModelDefinitionItemType.Event, "Test Event")
  private val testView = ModelDefinitionItem(ModelDefinitionItemType.View, "Test View")
  private val testSaga = ModelDefinitionItem(ModelDefinitionItemType.Saga, "Test Saga")
  private val testCommandWithContext =
      ModelDefinitionItem(
          ModelDefinitionItemType.Command, "Test Command", context = timelineContext)
  private val testEventWithContext =
      ModelDefinitionItem(
          ModelDefinitionItemType.Event, "Test Event", context = eventHandlerContext)
  private val testEventWithAggregateContext =
      ModelDefinitionItem(ModelDefinitionItemType.Event, "Test Event", aggregateContext)
  private val testSagaWithContext =
      ModelDefinitionItem(ModelDefinitionItemType.Saga, "Test Saga", context = sagaContext)

  @ParameterizedTest
  @MethodSource("validDefinitions")
  fun `parses valid definitions of all combinations`(data: ValidDefinitionTestData) {
    DefinitionParser.parse(data.definition) shouldBe data.expected()
  }

  @ParameterizedTest
  @MethodSource("invalidDefinitions")
  fun `parse should error it line doesn't match expected structure`(
      data: InvalidDefinitionTestData
  ) {
    val result = DefinitionParser.parse(data.definition)
    result.isFailure.shouldBeTrue()
    result.exceptionOrNull()?.message shouldBe data.expected()
  }

  private fun validDefinitions() =
      Stream.of(
          ValidDefinitionTestData("[c] Test Command -> [e] Test Event", testCommand, testEvent),
          ValidDefinitionTestData("[e] Test Event -> [s] Test Saga", testEvent, testSaga),
          ValidDefinitionTestData("[e] Test Event -> [v] Test View", testEvent, testView),
          ValidDefinitionTestData("[s] Test Saga -> [c] Test Command", testSaga, testCommand),
          ValidDefinitionTestData("[c]Test Command->[e]Test Event", testCommand, testEvent),
          ValidDefinitionTestData(
              "[t] Custom Timeline :: [c] Test Command -> [e] Custom Event Handler :: [e] Test Event",
              testCommandWithContext,
              testEventWithContext),
          ValidDefinitionTestData(
              "[a] Custom Aggregate :: [e] Test Event -> [s] Custom Process :: [s] Test Saga",
              testEventWithAggregateContext,
              testSagaWithContext),
          ValidDefinitionTestData(
              "[t]Custom Timeline::[c]Test Command->[e]Custom Event Handler::[e]Test Event",
              testCommandWithContext,
              testEventWithContext))

  data class ValidDefinitionTestData(
      val definition: String,
      val item: ModelDefinitionItem,
      val target: ModelDefinitionItem
  ) {
    fun expected() =
        Result.success(EventModelDefinition(listOf(ModelDefinitionEntry(item, target))))
  }

  private fun invalidDefinitions() =
      Stream.of(
          InvalidDefinitionTestData("Foo -> Bar"),
          InvalidDefinitionTestData("[x] Foo -> [c] Command"),
          InvalidDefinitionTestData("[c] Command -> [x] Bar"),
          InvalidDefinitionTestData("[c] Command /> [e] Event"),
          InvalidDefinitionTestData("[x] Context :: [c] Command -> [e] Event"),
          InvalidDefinitionTestData("[c] Command -> [x] Context :: [e] Event"),
          InvalidDefinitionTestData("[t] Context :/ [c] Command -> [e] Event"),
          InvalidDefinitionTestData("[c] Command -> [t] Context :/ [e] Event"))

  data class InvalidDefinitionTestData(val definition: String) {
    fun expected() = "Invalid line: $definition. Cause: Line does not match required structure"
  }
}
