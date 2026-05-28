package projson.core

import projson.model.JsonArray
import projson.model.JsonNull
import projson.model.JsonObject
import projson.model.JsonPrimitive
import projson.model.JsonReference
import projson.model.JsonValue
import java.util.UUID
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties

// converte objetos Kotlin para JSON
class ProJson {

    /**
     * API publica principal da biblioteca.
     *
     * Recebe um valor Kotlin e devolve o modelo JSON correspondente.
     * O valor recebido pode ser null, um valor simples, um Map, uma Collection,
     * uma data class ou uma classe normal.
     *
     * Esta funcao tambem trata automaticamente dos ids, das referencias,
     * das anotacoes e dos plugins definidos pela biblioteca.
     */
    fun toJson(obj: Any?): JsonValue {
        val seen = mutableMapOf<Any, JsonObject>()
        return toJson(obj, seen)
    }

    // decide que tipo de JSON criar
    private fun toJson(obj: Any?, seen: MutableMap<Any, JsonObject>): JsonValue {
        if (obj == null) return JsonNull
        if (obj is JsonValue) return obj
        if (obj is String || obj is Number || obj is Boolean) return JsonPrimitive(obj)
        if (obj is Map<*, *>) return mapToJson(obj, seen)
        if (obj is Collection<*>) return collectionToJson(obj, seen)

        val kClass = obj::class
        kClass.findAnnotation<JsonString>()?.let { annotation ->
            return pluginToJson(obj, annotation)
        }

        return objectToJson(obj, seen)
    }

    // converte um Map
    private fun mapToJson(map: Map<*, *>, seen: MutableMap<Any, JsonObject>): JsonObject {
        val jObj = JsonObject()
        map.forEach { (key, value) ->
            jObj.setProperty(key.toString(), toJson(value, seen))
        }
        return jObj
    }

    // converte uma lista
    private fun collectionToJson(col: Collection<*>, seen: MutableMap<Any, JsonObject>): JsonArray {
        val jArr = JsonArray()
        col.forEach { jArr.add(toJson(it, seen)) }
        return jArr
    }

    // usa o plugin indicado na anotacao
    private fun pluginToJson(obj: Any, annotation: JsonString): JsonValue {
        val plugin = annotation.plugin.createInstance()
        return JsonPrimitive(plugin.toJsonString(obj))
    }

    // converte um objeto
    private fun objectToJson(obj: Any, seen: MutableMap<Any, JsonObject>): JsonObject {
        seen[obj]?.let { return it }

        val kClass = obj::class
        val jObj = JsonObject()
        seen[obj] = jObj

        if (!kClass.isData) {
            jObj.setProperty("\$id", JsonPrimitive(UUID.randomUUID().toString()))
        }
        jObj.setProperty("\$type", JsonPrimitive(kClass.simpleName ?: "Unknown"))

        kClass.memberProperties.forEach { prop ->
            if (prop.hasAnnotation<JsonIgnore>()) return@forEach

            val name = prop.findAnnotation<JsonProperty>()?.name ?: prop.name
            require(!jObj.hasProperty(name))
            val value = prop.getter.call(obj)

            val jsonValue = if (prop.hasAnnotation<Reference>()) {
                toReference(value, seen)
            } else {
                toJson(value, seen)
            }
            jObj.setProperty(name, jsonValue)
        }
        return jObj
    }

    // converte uma propriedade com @Reference
    private fun toReference(value: Any?, seen: MutableMap<Any, JsonObject>): JsonValue {
        if (value == null) return JsonNull

        if (value is Collection<*>) {
            val arr = JsonArray()
            value.forEach { item -> if (item != null) arr.add(buildReference(item, seen)) }
            return arr
        }

        return buildReference(value, seen)
    }

    // cria uma referencia para o objeto
    private fun buildReference(obj: Any, seen: MutableMap<Any, JsonObject>): JsonReference {
        val target = seen[obj] ?: objectToJson(obj, seen)
        return JsonReference(target)
    }
}
