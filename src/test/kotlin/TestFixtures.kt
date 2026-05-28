import projson.core.JsonIgnore
import projson.core.JsonProperty
import projson.core.JsonString
import projson.core.JsonStringPlugin
import projson.core.Reference
import projson.model.Date
import projson.model.Task

class TarefaCustom(
    @JsonProperty("desc")
    val description: String,
    @JsonIgnore
    val deadline: Date?,
    @JsonProperty("deps")
    @Reference
    val dependencies: List<Task>
)

class HoraComoTexto : JsonStringPlugin {
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
