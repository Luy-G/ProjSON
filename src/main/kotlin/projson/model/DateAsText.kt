package projson.model

import projson.core.DateToText

// plugin para escrever datas como texto
class DateAsText : DateToText {
    override fun toJsonString(obj: Any): String {
        require(obj is Date)
        return "%02d/%02d/%04d".format(obj.day, obj.month, obj.year)
    }
}
