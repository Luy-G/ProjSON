import projson.core.ProJson
import projson.model.Date
import projson.model.DateAsText
import projson.model.JsonArray
import projson.model.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals

class PluginTests {

    @Test
    fun testPluginHoraSerializaComoString() {
        val h = Hora(15, 30)
        val json = ProJson().toJson(h)
        assertEquals(JsonPrimitive("15h30"), json)
    }

    @Test
    fun testPluginHoraEmListaDeObjetos() {
        val lista = listOf(Hora(9, 0), Hora(15, 30))
        val json = ProJson().toJson(lista) as JsonArray
        assertEquals("[\"9h00\", \"15h30\"]", json.toJsonString())
    }

    @Test
    fun testPluginDataSerializaComoTexto() {
        val plugin = DateAsText()
        assertEquals("30/02/2026", plugin.toJsonString(Date(30, 2, 2026)))
    }
}
