import projson.core.ProJson
import projson.model.Date
import projson.model.JsonObject
import projson.model.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AnnotationTests {

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
}
