package wallpaper.kotlin.qy.bing

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class Image() : Comparable<Image> {

    @JsonProperty("startdate")
    var endDateStr: String? = null
    var url: String? = null
    var title: String? = null
    var copyright: String? = null

    constructor(endDate: String?, url: String?, title: String?, copyright: String?) : this() {
        this.endDateStr = endDate
        this.url = url
        this.title = title
        this.copyright = copyright
    }

    fun appendPath(path: String) {
        this.url = BASE_URL_PREFIX + path
    }

    /**
     * 用于写入 sources_zh-CN.txt 的单行格式
     *
     * @return
     */
    fun sourceFormat(): String = "$endDateStr|$url|$title|$copyright${System.lineSeparator()}"

    /**
     * README 首页大图的排版
     */
    fun largeImg(todayFormat: String?, titleFormat: String?, copyrightFormat: String?) = ("![$title]($url&w=1000)"
            + System.lineSeparator() + System.lineSeparator()
            + "$todayFormat$endDateStr | $titleFormat$title | $copyrightFormat$copyright [download 4k]($url)"
            + System.lineSeparator() + System.lineSeparator())

    /**
     * README 和 归档文件 小图的排版
     */
    fun smallImg() = "| ![$title]($url&pid=hp&w=384&h=216&rs=1&c=4) <br/>$endDateStr [download 4k]($url)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Image
        return endDateStr == other.endDateStr
    }

    override fun hashCode() = endDateStr?.hashCode() ?: 0

    override fun toString() = "Image(endDateStr=$endDateStr, url=$url, title=$title, copyright=$copyright)"

    override fun compareTo(other: Image) = (other.endDateStr ?: "").compareTo(this.endDateStr ?: "")

    companion object {
        private const val BASE_URL_PREFIX = "https://cn.bing.com"
        fun fromSourceByLine(line: String): Image {
            val split = line.split("\\|".toRegex())
            return Image(split[0], split[1], split[2], split[3])
        }
    }
}