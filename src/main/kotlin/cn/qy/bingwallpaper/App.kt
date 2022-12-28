package cn.qy.bingwallpaper

class App {
    fun write2File(region: IRegion) {
        FileUtils.updateRegion(region)
        val imageCollection = FileUtils.readFromNet() + FileUtils.readFromSource()
        FileUtils.write2Source(imageCollection)
        FileUtils.writeReadme(imageCollection)
        FileUtils.writeMonthInfo(imageCollection)
    }
}

fun main() {
    App().write2File(USRegion)
}
