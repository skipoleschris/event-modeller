package uk.co.skipoles.eventmodeller.util

private val knownSuffixes = setOf(
    "Command", "Event", "Query", "Saga", "View", "Projection"
)

fun makeShortName(name: String): String {
    if (!name.contains('.')) return name

    val className = name.split('.').last()
    val messageName = knownSuffixes.find { className.endsWith(it) }?.let { className.dropLast(it.length) } ?: className
    return messageName
        .split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])".toRegex())
        .joinToString(" ")
}
