package wallpaper.kotlin

import wallpaper.kotlin.qy.bing.*
import java.io.IOException

class App {
    @Throws(IOException::class)
    fun write2File(region: IRegion) {
        FileUtils.updateRegion(region)
        val imageCollection = FileUtils.readFromNet() + FileUtils.readFromSource()
        FileUtils.write2Source(imageCollection)
        FileUtils.writeReadme(imageCollection)
        FileUtils.writeMonthInfo(imageCollection)
    }
}

fun main() {
    val app = App()
    app.write2File(CNRegion)
    app.write2File(USRegion)
}
