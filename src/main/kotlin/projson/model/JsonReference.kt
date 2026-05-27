package projson.model

// referencia para outro objeto JSON
class JsonReference(val target: JsonObject) : JsonValue() {

    init {
        // a referencia precisa de um id
        require(target.hasProperty("\$id"))
    }

    // id do objeto referenciado
    val id: String
        get() = (target.getProperty("\$id") as JsonPrimitive).value as String

    override fun toJsonString(indent: String): String {
        return "{ \"\$ref\": \"$id\" }"
    }
}
