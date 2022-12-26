package wallpaper.kotlin.qy.bing

import java.nio.file.Paths

object CNRegion : IRegion {

    private val BING_API: String = "${super.BASE_BING_API}&ensearch=0"
    private val BING_PATH = Paths.get("sources/sources_zh-CN.txt")
    private val README_PATH = Paths.get("README.md")
    private const val MONTH_PATH_STRING = "archives/zh-CN/"
    private val MONTH_PATH = Paths.get(MONTH_PATH_STRING)

    override fun getURL() = BING_API
    override fun getBingPath() = BING_PATH
    override fun getReadmePath() = README_PATH
    override fun getMonthPathString() = MONTH_PATH_STRING
    override fun getMonthPath() = MONTH_PATH

    override fun getMarkdownHeadText() = "## 必应壁纸"
    override fun getCurrentDayFormat() = "今日："
    override fun getTitleFormat() = "标题："
    override fun getCopyrightFormat() = "版权："
    override fun getCurrentMonthText() = "当月"

    override fun getArchivesText() = "## 历史归档"
}