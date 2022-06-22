package uk.co.skipoles.eventmodeller.definition

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ModelDefinitionItemTest {

  @Test
  fun `defaults the correct context on creation if not explicitly specified`() {
    ModelDefinitionItem(ModelDefinitionItemType.Command, "Command").context.shouldBeNull()
    ModelDefinitionItem(ModelDefinitionItemType.Event, "Event").context.shouldBeNull()
    ModelDefinitionItem(ModelDefinitionItemType.Event, "Event", "Aggregate").context shouldBe
        ModelDefinitionContext(ModelDefinitionContextType.Aggregate, "Aggregate")
  }

  @Test
  fun `returns the explicitly specified context`() {
    val context = ModelDefinitionContext(ModelDefinitionContextType.Timeline, "Custom")
    ModelDefinitionItem(ModelDefinitionItemType.Command, "Command", context = context)
        .getOrDeriveContext() shouldBe context
  }

  @Test
  fun `determines the timeline context for a Command with no explicit context`() {
    ModelDefinitionItem(ModelDefinitionItemType.Command, "Command").getOrDeriveContext() shouldBe
        ModelDefinitionContext.TIME_LINE_CONTEXT
  }

  @Test
  fun `determines the timeline context for a View with no explicit context`() {
    ModelDefinitionItem(ModelDefinitionItemType.View, "View").getOrDeriveContext() shouldBe
        ModelDefinitionContext.TIME_LINE_CONTEXT
  }

  @Test
  fun `determines the process context for a Saga with no explicit context`() {
    ModelDefinitionItem(ModelDefinitionItemType.Saga, "Saga").getOrDeriveContext() shouldBe
        ModelDefinitionContext.PROCESS_CONTEXT
  }

  @Test
  fun `determines the event handler context for an Event with no explicit context or aggregate`() {
    ModelDefinitionItem(ModelDefinitionItemType.Event, "Event").getOrDeriveContext() shouldBe
        ModelDefinitionContext.EVENT_HANDLER_CONTEXT
  }

  @Test
  fun `determines a custom aggregate context for an Event with no explicit context but an aggregate defined`() {
    ModelDefinitionItem(ModelDefinitionItemType.Event, "Event", "Aggregate", null)
        .getOrDeriveContext() shouldBe
        ModelDefinitionContext(ModelDefinitionContextType.Aggregate, "Aggregate")
  }
}
