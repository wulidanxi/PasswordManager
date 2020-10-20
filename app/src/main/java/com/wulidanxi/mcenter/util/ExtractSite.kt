import java.util.regex.Pattern

class ExtractSite {
    companion object {
        private const val regexChinese = "[\u4e00-\u9fa5]"
        fun removeChinese(site: String): String {
            val pat: Pattern = Pattern.compile(regexChinese)
            val mat = pat.matcher(site)
            return mat.replaceAll("");
        }

        fun addChinese(webSite: String, msg: String): String {
            val clear = removeChinese(msg).trim()
            if (clear.isEmpty()) {
                val buff = StringBuffer(webSite)
                val buffMsg = StringBuffer(msg)
                val length = buff.length/buffMsg.length
                for (index in buffMsg.indices) {
                    val site: Int = (length * index until length * (index + 1)).random()
                    buff.insert(site, msg[index])
                }
                return buff.toString()
            }
            return "error"
        }
    }
}