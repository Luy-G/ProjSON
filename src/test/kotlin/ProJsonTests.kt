import projson.core.ProJson
import projson.model.Date
import projson.model.JsonArray
import projson.model.JsonNull
import projson.model.JsonObject
import projson.model.JsonPrimitive
import projson.model.Task
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ProJsonTests {

    @Test
    fun testToJsonComPrimitivos() {
        val engine = ProJson()
        assertEquals(JsonNull, engine.toJson(null))
        assertEquals(JsonPrimitive("ola"), engine.toJson("ola"))
        assertEquals(JsonPrimitive(7), engine.toJson(7))
        assertEquals(JsonPrimitive(false), engine.toJson(false))
    }

    @Test
    fun testToJsonComMapNaoTemType() {
        val mapa = mapOf("nome" to "Afonso", "idade" to 20)
        val json = ProJson().toJson(mapa) as JsonObject
        assertNull(json.getProperty("\$type"))
        assertEquals(JsonPrimitive("Afonso"), json.getProperty("nome"))
        assertEquals(JsonPrimitive(20), json.getProperty("idade"))
    }

    @Test
    fun testToJsonComColecaoDaArray() {
        val lista = listOf("a", null, "b")
        val json = ProJson().toJson(lista) as JsonArray
        json.add("c")
        assertEquals("[\"a\", null, \"b\", \"c\"]", json.toJsonString())
    }

    @Test
    fun testToJsonDataClassTemTypeSemId() {
        val d = Date(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject
        assertEquals(JsonPrimitive("Date"), json.getProperty("\$type"))
        assertNull(json.getProperty("\$id"))
        assertEquals(JsonPrimitive(31), json.getProperty("day"))
        assertEquals(JsonPrimitive(4), json.getProperty("month"))
        assertEquals(JsonPrimitive(2026), json.getProperty("year"))
    }

    @Test
    fun testToJsonExemploEnunciadoDate() {
        val d = Date(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject
        json.setProperty("year", 2027)
        assertEquals(JsonPrimitive(2027), json.getProperty("year"))
    }

    @Test
    fun testToJsonClasseNormalTemIdEType() {
        val t = Task("T1", null, emptyList())
        val json = ProJson().toJson(t) as JsonObject
        assertEquals(JsonPrimitive("Task"), json.getProperty("\$type"))
        assertNotNull(json.getProperty("\$id"))
    }

    @Test
    fun testProJsonNaoReutilizaEstadoEntreChamadas() {
        val engine = ProJson()
        val t = Task("T1", null, emptyList())

        val first = engine.toJson(t) as JsonObject
        val second = engine.toJson(t) as JsonObject

        val firstId = (first.getProperty("\$id") as JsonPrimitive).value
        val secondId = (second.getProperty("\$id") as JsonPrimitive).value

        assertNotEquals(firstId, secondId)
    }

    @Test
    fun testToStringDoArrayComNullsEStrings() {
        val list = listOf("a", null, "b")
        val json = ProJson().toJson(list) as JsonArray
        json.add("c")
        assertEquals("[\"a\", null, \"b\", \"c\"]", json.toString())
    }
}
