package projson.model

// objeto JSON com pares chave-valor
class JsonObject : JsonValue() {

    // guarda as propriedades pela ordem de entrada
    private val properties = mutableMapOf<String, JsonValue>()

    // copia das propriedades
    val entries: Map<String, JsonValue>
        get() = properties.toMap()

    // adiciona ou substitui uma propriedade
    fun setProperty(name: String, value: Any?) {
        require(name.isNotBlank())
        properties[name] = wrap(value)
    }

    fun removeProperty(name: String) {
        properties.remove(name)
    }

    fun getProperty(name: String): JsonValue? = properties[name]

    fun hasProperty(name: String): Boolean = properties.containsKey(name)

    override fun accept(visitor: (JsonValue) -> Unit) {
        // visita o objeto e os seus valores
        visitor(this)
        properties.values.forEach { it.accept(visitor) }
    }

    override fun toJsonString(indent: String): String {
        if (properties.isEmpty()) return "{}"
        val nextIndent = "$indent  "
        val content = properties.entries.joinToString(",\n") { (key, value) ->
            "$nextIndent\"${escape(key)}\": ${value.toJsonString(nextIndent)}"
        }
        return "{\n$content\n$indent}"
    }

    // converte valores simples para JsonValue
    private fun wrap(value: Any?): JsonValue = when (value) {
        null -> JsonNull
        is JsonValue -> value
        is String, is Number, is Boolean -> JsonPrimitive(value)
        else -> throw IllegalArgumentException()
    }

    // escapa nomes de propriedades
    private fun escape(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}
