package projson.model

// array JSON
class JsonArray : JsonValue() {

    private val elements = mutableListOf<JsonValue>()

    // copia dos elementos
    val items: List<JsonValue>
        get() = elements.toList()

    val size: Int
        get() = elements.size

    // adiciona no fim
    fun add(value: Any?) {
        elements.add(wrap(value))
    }

    fun remove(index: Int) {
        require(index in elements.indices)
        elements.removeAt(index)
    }

    fun set(index: Int, value: Any?) {
        require(index in elements.indices)
        elements[index] = wrap(value)
    }

    fun get(index: Int): JsonValue {
        require(index in elements.indices)
        return elements[index]
    }

    override fun accept(visitor: (JsonValue) -> Unit) {
        // visita o array e os seus elementos
        visitor(this)
        elements.forEach { it.accept(visitor) }
    }

    override fun toJsonString(indent: String): String {
        if (elements.isEmpty()) return "[]"
        val content = elements.joinToString(", ") { it.toJsonString(indent) }
        return "[$content]"
    }

    private fun wrap(value: Any?): JsonValue = when (value) {
        null -> JsonNull
        is JsonValue -> value
        is String, is Number, is Boolean -> JsonPrimitive(value)
        else -> throw IllegalArgumentException()
    }
}
