import projson.model.Date
import projson.model.JsonNull
import projson.model.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JsonPrimitiveTests {

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
}
