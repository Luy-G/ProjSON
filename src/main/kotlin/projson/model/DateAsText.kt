package projson.model

import projson.core.JsonStringPlugin

// plugin para escrever datas como texto
class DateAsText : JsonStringPlugin {
    override fun toJsonString(obj: Any): String {
        require(obj is Date)
        return "%02d/%02d/%04d".format(obj.day, obj.month, obj.year)
    }
}
