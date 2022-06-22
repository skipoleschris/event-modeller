package uk.co.skipoles.eventmodeller.definition

data class EventModelDefinition(val entries: List<ModelDefinitionEntry>)

data class ModelDefinitionEntry(val item: ModelDefinitionItem, val target: ModelDefinitionItem) {
    override fun toString(): String = "$item -> $target"
}

enum class ModelDefinitionItemType(val symbol: String) {
    Command("c"),
    Event("e"),
    View("v"),
    Saga("s")
}

data class ModelDefinitionItem(
    val type: ModelDefinitionItemType,
    val name: String,
    val aggregateName: String? = null,
    val context: ModelDefinitionContext? = determineContext(type, aggregateName)
) {
    fun getOrDeriveContext(): ModelDefinitionContext =
        context
            ?: when (type) {
                ModelDefinitionItemType.Command -> ModelDefinitionContext.TIME_LINE_CONTEXT
                ModelDefinitionItemType.View -> ModelDefinitionContext.TIME_LINE_CONTEXT
                ModelDefinitionItemType.Saga -> ModelDefinitionContext.PROCESS_CONTEXT
                ModelDefinitionItemType.Event ->
                    if (aggregateName != null) aggregateContext(aggregateName)
                    else ModelDefinitionContext.EVENT_HANDLER_CONTEXT
            }

    override fun toString(): String =
        if (context != null) "$context :: [${type.symbol}] $name" else "[${type.symbol}] $name"

    companion object {
        fun aggregateContext(aggregateName: String) =
            ModelDefinitionContext(ModelDefinitionContextType.Aggregate, aggregateName)

        fun determineContext(type: ModelDefinitionItemType, aggregateName: String?) =
            if (type == ModelDefinitionItemType.Event && aggregateName != null)
                aggregateContext(aggregateName)
            else null
    }
}

enum class ModelDefinitionContextType(val symbol: String) {
    Saga("s"),
    Timeline("t"),
    EventHandler("e"),
    Aggregate("a")
}

data class ModelDefinitionContext(val type: ModelDefinitionContextType, val name: String) {
    fun isDefault() = isDefaultContext(this)

    override fun toString(): String = "[${type.symbol}] $name"

    companion object {
        val TIME_LINE_CONTEXT = ModelDefinitionContext(ModelDefinitionContextType.Timeline, "Timeline")
        val PROCESS_CONTEXT = ModelDefinitionContext(ModelDefinitionContextType.Saga, "Process")
        val EVENT_HANDLER_CONTEXT =
            ModelDefinitionContext(ModelDefinitionContextType.EventHandler, "EventHandlers")

        fun isDefaultContext(context: ModelDefinitionContext) =
            context == TIME_LINE_CONTEXT ||
                    context == PROCESS_CONTEXT ||
                    context == EVENT_HANDLER_CONTEXT
    }
}
