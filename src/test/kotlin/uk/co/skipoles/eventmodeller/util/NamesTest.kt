package uk.co.skipoles.eventmodeller.util

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NamesTest {
    
    @Test
    fun `non-class-named items should not be adjusted`() {
        makeShortName("ExistingName") shouldBe "ExistingName"
        makeShortName("Existing Name") shouldBe "Existing Name"
    }
    
    @Test
    fun `non-special class names should be correctly shortened`() {
        makeShortName("uk.co.skipoles.SomeClassName") shouldBe "Some Class Name"
    }
    
    @Test
    fun `class names ending with Command should have this removed`() {
        makeShortName("uk.co.skipoles.SomeIntentCommand") shouldBe "Some Intent"
    }

    @Test
    fun `class names ending with Event should have this removed`() {
        makeShortName("uk.co.skipoles.SomeOutcomeEvent") shouldBe "Some Outcome"
    }

    @Test
    fun `class names ending with Query should have this removed`() {
        makeShortName("uk.co.skipoles.SomeInformationQuery") shouldBe "Some Information"
    }

    @Test
    fun `class names ending with Saga should have this removed`() {
        makeShortName("uk.co.skipoles.SomeManagementSaga") shouldBe "Some Management"
    }

    @Test
    fun `class names ending with Projection should have this removed`() {
        makeShortName("uk.co.skipoles.SomeUsefulProjection") shouldBe "Some Useful"
    }

    @Test
    fun `class names ending with View should have this removed`() {
        makeShortName("uk.co.skipoles.SomeUsefulView") shouldBe "Some Useful"
    }

    @Test
    fun `class names containing acronyms should be correctly shortened`() {
        makeShortName("uk.co.skipoles.SomeURLAndDAOHelper") shouldBe "Some URL And DAO Helper"
    }
}