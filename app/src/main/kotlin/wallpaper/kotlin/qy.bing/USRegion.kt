package wallpaper.kotlin.qy.bing

import java.nio.file.Paths

object USRegion : IRegion {

    private val BING_API: String = "${super.BASE_BING_API}&ensearch=1"
    private val BING_PATH = Paths.get("sources/sources_en-US.txt")
    private val README_PATH = Paths.get("README_en-US.md")
    private const val MONTH_PATH_STRING = "archives/en-US/"
    private val MONTH_PATH = Paths.get(MONTH_PATH_STRING)

    override fun getURL() = BING_API
    override fun getBingPath() = BING_PATH
    override fun getReadmePath() = README_PATH
    override fun getMonthPathString() = MONTH_PATH_STRING
    override fun getMonthPath() = MONTH_PATH

    override fun getMarkdownHeadText() = "## bing wallpaper"
    override fun getCurrentDayFormat() = "Today: "
    override fun getTitleFormat() = "title："
    override fun getCopyrightFormat() = "copyright："
    override fun getCurrentMonthText() = "current month"

    override fun getArchivesText() = "## archives"
}