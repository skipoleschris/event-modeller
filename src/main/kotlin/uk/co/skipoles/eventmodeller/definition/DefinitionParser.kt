package uk.co.skipoles.eventmodeller.definition

object DefinitionParser {

  fun parse(definition: String): Result<EventModelDefinition> =
      try {
        Result.success(EventModelDefinition(definition.split("\n").map(::lineToEntry)))
      } catch (e: DefinitionParseException) {
        Result.failure(e)
      }

  private fun lineToEntry(line: String): ModelDefinitionEntry {
    val itemRegex = """(\[([stea])\]\s*(.*)\s*::)?\s*\[([cevs])\]\s*(.*)"""
    val regex = """$itemRegex\s*->\s*$itemRegex""".toRegex()
    val match =
        regex.matchEntire(line.trim())
            ?: throw DefinitionParseException(line, "Line does not match required structure")

    try {
      val fromItemContext = createContext(match.groupValues[2], match.groupValues[3].trim())
      val toItemContext = createContext(match.groupValues[7], match.groupValues[8].trim())

      return ModelDefinitionEntry(
          createItem(match.groupValues[4], match.groupValues[5].trim(), fromItemContext),
          createItem(match.groupValues[9], match.groupValues[10].trim(), toItemContext))
    } catch (e: IllegalStateException) {
      throw DefinitionParseException(line, e.message ?: "Unknown type")
    }
  }

  private fun createContext(type: String, name: String) =
      if (type.isNotEmpty() && name.isNotEmpty())
          ModelDefinitionContext(type.toModelDefinitionContextType(), name)
      else null

  private fun createItem(type: String, name: String, context: ModelDefinitionContext?) =
      ModelDefinitionItem(type.toModelDefinitionItemType(), name, context)

  private fun String.toModelDefinitionItemType() =
      when (this) {
        "c" -> ModelDefinitionItemType.Command
        "e" -> ModelDefinitionItemType.Event
        "v" -> ModelDefinitionItemType.View
        "s" -> ModelDefinitionItemType.Saga
        else -> throw IllegalStateException("Unknown item type: $this")
      }

  private fun String.toModelDefinitionContextType() =
      when (this) {
        "s" -> ModelDefinitionContextType.Saga
        "t" -> ModelDefinitionContextType.Timeline
        "e" -> ModelDefinitionContextType.EventHandler
        "a" -> ModelDefinitionContextType.Aggregate
        else -> throw IllegalStateException("Unknown context type: $this")
      }

  class DefinitionParseException(private val line: String, private val detail: String) :
      Exception() {

    override val message: String
      get() = "Invalid line: $line. Cause: $detail"
  }
}
