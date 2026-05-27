package projson.model

// base de todos os valores JSON
abstract class JsonValue {

    // devolve o JSON em texto
    abstract fun toJsonString(indent: String = ""): String

    // percorre este valor
    open fun accept(visitor: (JsonValue) -> Unit) {
        visitor(this)
    }

    override fun toString(): String = toJsonString()
}
