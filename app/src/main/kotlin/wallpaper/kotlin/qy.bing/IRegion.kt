package wallpaper.kotlin.qy.bing

import java.nio.file.Path

interface IRegion {

    val BASE_BING_API: String
        get() = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=10&nc=1612409408851&pid=hp&FORM=BEHPTB&uhd=1&uhdwidth=3840&uhdheight=2160"

    fun getURL(): String
    fun getBingPath(): Path
    fun getReadmePath(): Path
    fun getMonthPathString(): String
    fun getMonthPath(): Path

    fun getMarkdownHeadText(): String
    fun getCurrentDayFormat(): String
    fun getTitleFormat(): String
    fun getCopyrightFormat(): String
    fun getCurrentMonthText(): String

    fun getArchivesText(): String
}