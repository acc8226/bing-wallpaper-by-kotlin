package cn.qy.bingwallpaper

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object FileUtils {

    private val MAPPER = ObjectMapper()
    private var region: IRegion = USRegion

    fun updateRegion(region: IRegion) {
        FileUtils.region = region
    }

    fun readFromNet(): Set<Image> {
        val jsonNode: JsonNode = MAPPER.readTree(URL(region.getURL()))
        val imagesNode = jsonNode["images"].toString()
        val images = MAPPER.readValue(imagesNode, object : TypeReference<Array<Image>>() {})
        val imageSet: MutableSet<Image> = TreeSet()
        for (image in images) {
            // 图片时间
            val endDate: String? = image.endDateStr
            val localDate = LocalDate.parse(endDate, DateTimeFormatter.BASIC_ISO_DATE)
            image.endDateStr = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
            // 图片地址
            var path: String = image.url!!
            path.takeIf { it.contains("&") }?.let {
                path = it.substring(0, it.indexOf("&"))
            }
            image.appendPath(path)
            imageSet.add(image)
        }
        return imageSet
    }

    /**
     * 读取 sources_zh-CN.txt
     *
     * @return
     * @throws IOException
     */
    fun readFromSource(): Collection<Image> {
        val bingPath = region.getBingPath()
        if (!Files.exists(bingPath)) {
            bingPath.parent.let { Files.createDirectories(it) }
            Files.createFile(bingPath)
            return Collections.emptySet()
        }
        return Files.readAllLines(bingPath).filter { it.isNotEmpty() && !it.startsWith("#") }
            .map { Image.fromSourceByLine(it) }
    }

    /**
     * 写入 sources_zh-CN.txt
     *
     * @param imgList
     * @throws IOException
     */
    fun write2Source(imgList: Collection<Image>) {
        val bingPath = region.getBingPath()
        Files.deleteIfExists(bingPath)
        Files.createFile(bingPath)
        for (images in imgList) {
            Files.write(bingPath, images.sourceFormat().toByteArray(), StandardOpenOption.APPEND)
        }
    }

    /**
     * 写入 README.md
     *
     * @param imgList
     * @throws IOException
     */
    fun writeReadme(imgList: Collection<Image>) {
        val readmePath = region.getReadmePath()
        Files.deleteIfExists(readmePath)
        Files.createFile(readmePath)
        val iterator = imgList.iterator()
        var current = iterator.next()

        // 写入当天
        val byteArray =
            current.largeImg(region.getCurrentDayFormat(), region.getTitleFormat(), region.getCopyrightFormat())
                .toByteArray()
        Files.write(readmePath, byteArray, StandardOpenOption.APPEND)

        val currentMonthImageList: MutableList<Image> = ArrayList(31)
        val firstImageEndDate: String = current.endDateStr?.substring(0, 7) ?: ""
        while (iterator.hasNext()) {
            val next = iterator.next()
            currentMonthImageList.add(current)
            if (firstImageEndDate != (next.endDateStr?.substring(0, 7) ?: "")) {
                break
            }
            current = next
        }
        // 写入当月
        writeFile(readmePath, currentMonthImageList)

        // 归档
        Files.write(
            readmePath,
            (System.lineSeparator() + region.getArchivesText() + System.lineSeparator() + System.lineSeparator()).toByteArray(),
            StandardOpenOption.APPEND
        )

        val dateList = imgList.map { it.endDateStr ?: "" }
            .filter { it.isNotEmpty() }
            .map { it.substring(0, 7) }
            .distinct()

        var i = 0
        val stringBuilder = StringBuilder()
        for (date in dateList) {
            if (i % 8 != 0) {
                stringBuilder.append(" ")
            }
            stringBuilder.append("[")
            stringBuilder.append(date)
            stringBuilder.append("](")
            stringBuilder.append("./${region.getMonthPathString()}$date.md")
            stringBuilder.append(") |")
            if (i % 8 == 7) {
                stringBuilder.append(System.lineSeparator())
            }
            i++
        }
        if (i % 8 != 0) {
            stringBuilder.append(System.lineSeparator())
        }
        Files.write(readmePath, stringBuilder.toString().toByteArray(), StandardOpenOption.APPEND)
    }

    /**
     * 按月份写入图片信息
     *
     * @param imgList
     * @throws IOException
     */
    fun writeMonthInfo(imgList: Collection<Image>) {
        val monthPath = region.getMonthPath()
        val monthMap: MutableMap<String, MutableList<Image>> = HashMap()
        for (images in imgList) {
            val key: String = images.endDateStr!!.substring(0, 7)
            var list: MutableList<Image>
            if (monthMap.containsKey(key)) {
                list = monthMap[key]!!
            } else {
                list = ArrayList(31)
                monthMap[key] = list
            }
            list.add(images)
        }
        if (!Files.exists(monthPath)) {
            Files.createDirectories(monthPath)
        }
        for (monthName in monthMap.keys) {
            val path = monthPath.resolve("$monthName.md")
            Files.deleteIfExists(path)
            Files.createFile(path)
            writeFile(path, monthMap[monthName]!!, monthName)
        }
    }

    private fun writeFile(path: Path, imagesList: List<Image>, name: String?) {
        if (imagesList.isNotEmpty()) {
            val stringBuilder = StringBuilder(300)
            stringBuilder.append(region.getMarkdownHeadText())
            if (name != null) {
                stringBuilder.append("(")
                stringBuilder.append(name)
                stringBuilder.append(")")
            }
            stringBuilder.append(System.lineSeparator())
            stringBuilder.append(System.lineSeparator())

            // 需要处理 1、 2 和 多张图的情况
            stringBuilder.append("|")
            var i = 1
            val j = 3.coerceAtMost(imagesList.size)
            while (i <= j) {
                stringBuilder.append("  |")
                i++
            }
            stringBuilder.append(System.lineSeparator())

            // 需要处理 1、 2 和 多张图的情况
            stringBuilder.append("|")
            i = 1
            while (i <= j) {
                stringBuilder.append(" :----: |")
                i++
            }
            stringBuilder.append(System.lineSeparator())
            i = 0
            for (images in imagesList) {
                stringBuilder.append(images.smallImg())
                // 每 3 张图进行封底+换行
                if (i % 3 == 2) {
                    stringBuilder.append("|").append(System.lineSeparator())
                }
                i++
            }
            // 最后一张图如果不是最后一列表示从来没有进行过封底+换行则 换之
            if (i % 3 != 0) {
                stringBuilder.append("|").append(System.lineSeparator())
            }
            Files.write(path, stringBuilder.toString().toByteArray(), StandardOpenOption.APPEND)
        }
    }

    private fun writeFile(path: Path, imagesList: List<Image>) {
        writeFile(path, imagesList, region.getCurrentMonthText())
    }

}
