import projson.core.ProJson
import projson.model.Date
import projson.model.JsonArray
import projson.model.JsonObject
import projson.model.JsonPrimitive
import projson.model.JsonReference
import projson.model.Task
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReferenceTests {

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
}
