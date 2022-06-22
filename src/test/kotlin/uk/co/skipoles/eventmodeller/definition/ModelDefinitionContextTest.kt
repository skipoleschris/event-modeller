package uk.co.skipoles.eventmodeller.definition

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ModelDefinitionContextTest {

  @Test
  fun `can identify whether it id a default context item or not`() {
    ModelDefinitionContext.PROCESS_CONTEXT.isDefault().shouldBeTrue()
    ModelDefinitionContext.TIME_LINE_CONTEXT.isDefault().shouldBeTrue()
    ModelDefinitionContext.EVENT_HANDLER_CONTEXT.isDefault().shouldBeTrue()
    ModelDefinitionContext(ModelDefinitionContextType.Aggregate, "Test").isDefault().shouldBeTrue()
  }

  @Test
  fun `generates a meaningful string representation for debugging`() {
    ModelDefinitionContext.PROCESS_CONTEXT.toString() shouldBe "[s] Process"
    ModelDefinitionContext.TIME_LINE_CONTEXT.toString() shouldBe "[t] Timeline"
    ModelDefinitionContext.EVENT_HANDLER_CONTEXT.toString() shouldBe "[e] EventHandlers"
    ModelDefinitionContext(ModelDefinitionContextType.Aggregate, "Test").toString() shouldBe
        "[a] Test"
  }
}
