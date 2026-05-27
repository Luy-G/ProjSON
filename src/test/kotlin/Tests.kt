import projson.core.JsonIgnore
import projson.core.JsonProperty
import projson.core.JsonString
import projson.core.DateToText
import projson.core.ProJson
import projson.core.Reference
import projson.model.Date
import projson.model.DateAsText
import projson.model.JsonArray
import projson.model.JsonNull
import projson.model.JsonObject
import projson.model.JsonPrimitive
import projson.model.JsonReference
import projson.model.JsonValue
import projson.model.Task
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertNotEquals

class TarefaCustom(
    @JsonProperty("desc")
    val description: String,
    @JsonIgnore
    val deadline: Date?,
    @JsonProperty("deps")
    @Reference
    val dependencies: List<Task>
)

class HoraComoTexto : DateToText {
    override fun toJsonString(obj: Any): String {
        require(obj is Hora)
        return "${obj.hour}h${"%02d".format(obj.minutes)}"
    }
}

@JsonString(HoraComoTexto::class)
data class Hora(val hour: Int, val minutes: Int)

class PropriedadesRepetidas(
    @JsonProperty("b")
    val a: String,
    val b: String
)

class Tests {

    @Test
    fun testJsonNull() {
        assertEquals("null", JsonNull.toJsonString())
    }

    @Test
    fun testJsonPrimitiveString() {
        assertEquals("\"ola\"", JsonPrimitive("ola").toJsonString())
    }

    @Test
    fun testJsonPrimitiveEscapaTexto() {
        val json = JsonPrimitive("ola \"x\"\\y\nfim")
        assertEquals("\"ola \\\"x\\\"\\\\y\\nfim\"", json.toJsonString())
    }

    @Test
    fun testJsonPrimitiveNumberAndBoolean() {
        assertEquals("42", JsonPrimitive(42).toJsonString())
        assertEquals("3.14", JsonPrimitive(3.14).toJsonString())
        assertEquals("true", JsonPrimitive(true).toJsonString())
    }

    @Test
    fun testJsonPrimitiveRejeitaTiposInvalidos() {
        assertFailsWith<IllegalArgumentException> { JsonPrimitive(Date(1, 1, 2026)) }
    }

    @Test
    fun testJsonObjectVazio() {
        assertEquals("{}", JsonObject().toJsonString())
    }

    @Test
    fun testJsonObjectAdicionarPropriedades() {
        val obj = JsonObject()
        obj.setProperty("nome", "Afonso")
        obj.setProperty("idade", 20)
        obj.setProperty("ativo", true)
        obj.setProperty("hobby", null)

        assertTrue(obj.hasProperty("nome"))
        assertEquals(4, obj.entries.size)
        assertEquals(JsonNull, obj.getProperty("hobby"))
    }

    @Test
    fun testJsonObjectEscapaNomeDaPropriedade() {
        val obj = JsonObject()
        obj.setProperty("nome \"principal\"", "Afonso")
        assertEquals("{\n  \"nome \\\"principal\\\"\": \"Afonso\"\n}", obj.toJsonString())
    }

    @Test
    fun testJsonObjectRemoverPropriedade() {
        val obj = JsonObject()
        obj.setProperty("a", 1)
        obj.setProperty("b", 2)
        obj.removeProperty("a")
        assertNull(obj.getProperty("a"))
        assertNotNull(obj.getProperty("b"))
    }

    @Test
    fun testJsonObjectAtualizarPropriedade() {
        val obj = JsonObject()
        obj.setProperty("year", 2026)
        obj.setProperty("year", 2027)
        assertEquals(JsonPrimitive(2027), obj.getProperty("year"))
    }

    @Test
    fun testJsonObjectRejeitaNomeVazio() {
        assertFailsWith<IllegalArgumentException> { JsonObject().setProperty("", 1) }
    }

    @Test
    fun testJsonArrayVazio() {
        assertEquals("[]", JsonArray().toJsonString())
    }

    @Test
    fun testJsonArrayAdicionarRemover() {
        val arr = JsonArray()
        arr.add("a")
        arr.add(null)
        arr.add("b")
        assertEquals(3, arr.size)
        arr.remove(1)
        assertEquals(2, arr.size)
    }

    @Test
    fun testJsonArraySet() {
        val arr = JsonArray()
        arr.add(1); arr.add(2); arr.add(3)
        arr.set(1, 99)
        assertEquals(JsonPrimitive(99), arr.get(1))
    }

    @Test
    fun testJsonArrayIndiceForaDosLimites() {
        val arr = JsonArray()
        assertFailsWith<IllegalArgumentException> { arr.get(0) }
        assertFailsWith<IllegalArgumentException> { arr.set(0, "x") }
        assertFailsWith<IllegalArgumentException> { arr.remove(5) }
    }

    @Test
    fun testVisitorVisitaTodosOsNodos() {
        val obj = JsonObject()
        obj.setProperty("a", 1)
        val nested = JsonArray()
        nested.add("x")
        nested.add(true)
        obj.setProperty("lista", nested)

        val visitados = mutableListOf<JsonValue>()
        obj.accept { visitados.add(it) }

        assertEquals(5, visitados.size)
    }

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
    fun testJsonIgnoreEJsonProperty() {
        val t = TarefaCustom("T1", Date(30, 2, 2026), emptyList())
        val json = ProJson().toJson(t) as JsonObject
        assertEquals(JsonPrimitive("T1"), json.getProperty("desc"))
        assertNull(json.getProperty("description"))
        assertNull(json.getProperty("deadline"))
        assertNotNull(json.getProperty("deps"))
    }

    @Test
    fun testJsonPropertyNaoPodeCriarNomeRepetido() {
        val obj = PropriedadesRepetidas("A", "B")
        assertFailsWith<IllegalArgumentException> { ProJson().toJson(obj) }
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
    fun testReferenciasNoExemploDoEnunciado() {
        val t1 = Task("T1", Date(30, 2, 2026), emptyList())
        val t2 = Task("T2", Date(31, 4, 2026), emptyList())
        val t3 = Task("T3", null, listOf(t1, t2))
        val all = listOf(t1, t2, t3)

        val json = ProJson().toJson(all) as JsonArray
        assertEquals(3, json.size)

        val terceiro = json.get(2) as JsonObject
        val deps = terceiro.getProperty("dependencies") as JsonArray
        assertEquals(2, deps.size)
        assertTrue(deps.get(0) is JsonReference)
        assertTrue(deps.get(1) is JsonReference)

        val idT1 = ((json.get(0) as JsonObject).getProperty("\$id") as JsonPrimitive).value
        assertEquals(idT1, (deps.get(0) as JsonReference).id)
    }

    @Test
    fun testPluginJsonStringSerializaComoString() {
        val h = Hora(15, 30)
        val json = ProJson().toJson(h)
        assertEquals(JsonPrimitive("15h30"), json)
    }

    @Test
    fun testPluginJsonStringEmListaDeObjetos() {
        val lista = listOf(Hora(9, 0), Hora(15, 30))
        val json = ProJson().toJson(lista) as JsonArray
        assertEquals("[\"9h00\", \"15h30\"]", json.toJsonString())
    }

    @Test
    fun testPluginDateAsText() {
        val plugin = DateAsText()
        assertEquals("30/02/2026", plugin.toJsonString(Date(30, 2, 2026)))
    }

    @Test
    fun testToStringDoArrayComNullsEStrings() {
        val list = listOf("a", null, "b")
        val json = ProJson().toJson(list) as JsonArray
        json.add("c")
        assertEquals("[\"a\", null, \"b\", \"c\"]", json.toString())
    }
}
