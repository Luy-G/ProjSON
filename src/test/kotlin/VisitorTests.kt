import projson.model.JsonArray
import projson.model.JsonObject
import projson.model.JsonValue
import kotlin.test.Test
import kotlin.test.assertEquals

class VisitorTests {

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
}
