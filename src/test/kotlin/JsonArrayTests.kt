import projson.model.JsonArray
import projson.model.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class JsonArrayTests {

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
        arr.add(1)
        arr.add(2)
        arr.add(3)
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
}
