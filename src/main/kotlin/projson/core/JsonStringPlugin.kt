package projson.core

// interface dos plugins que escrevem objetos como texto JSON
interface JsonStringPlugin {
    fun toJsonString(obj: Any): String
}
