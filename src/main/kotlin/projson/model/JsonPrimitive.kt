package projson.model

// valor simples de JSON
class JsonPrimitive(val value: Any) : JsonValue() {

    init {
        // valida tipos simples
        require(value is String || value is Number || value is Boolean)
    }

    override fun toJsonString(indent: String): String {
        return if (value is String) "\"${escape(value)}\"" else value.toString()
    }

    // permite comparar dois valores iguais
    override fun equals(other: Any?): Boolean = other is JsonPrimitive && other.value == value

    override fun hashCode(): Int = value.hashCode()

    // escapa texto para JSON
    private fun escape(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}
