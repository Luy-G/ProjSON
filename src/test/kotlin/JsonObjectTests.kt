import projson.model.JsonNull
import projson.model.JsonObject
import projson.model.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsonObjectTests {

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
}
