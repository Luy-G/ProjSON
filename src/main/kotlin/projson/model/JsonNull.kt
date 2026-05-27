package projson.model

// valor null em JSON
object JsonNull : JsonValue() {
    // o texto JSON de um null é sempre a palavra null
    override fun toJsonString(indent: String): String = "null"
}
