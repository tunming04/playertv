package com.player.tv.util

import com.player.tv.domain.model.EpgProgram
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object EpgParser {
    // XMLTV date format: "20240321123000 +0000"
    private val dateFormat = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    suspend fun parse(url: String): List<EpgProgram> = withContext(Dispatchers.IO) {
        val programs = mutableListOf<EpgProgram>()
        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val parser = factory.newPullParser()
            
            // Note: For large XMLTV files, this should ideally be streamed or cached.
            // For simplicity, we are downloading and parsing it on the fly.
            val inputStream = URL(url).openStream()
            parser.setInput(inputStream, null)

            var eventType = parser.eventType
            var currentProgram: EpgProgramBuilder? = null
            var text = ""

            while (eventType != XmlPullParser.END_DOCUMENT) {
                val name = parser.name
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (name.equals("programme", ignoreCase = true)) {
                            val startStr = parser.getAttributeValue(null, "start")
                            val stopStr = parser.getAttributeValue(null, "stop")
                            val channelId = parser.getAttributeValue(null, "channel")

                            currentProgram = EpgProgramBuilder(
                                channelId = channelId ?: "",
                                startTime = parseTime(startStr),
                                endTime = parseTime(stopStr)
                            )
                        } else if (name.equals("icon", ignoreCase = true) && currentProgram != null) {
                            val src = parser.getAttributeValue(null, "src")
                            if (src != null) currentProgram.icon = src
                        }
                    }
                    XmlPullParser.TEXT -> {
                        text = parser.text.trim()
                    }
                    XmlPullParser.END_TAG -> {
                        if (name.equals("programme", ignoreCase = true)) {
                            currentProgram?.build()?.let { programs.add(it) }
                            currentProgram = null
                        } else if (name.equals("title", ignoreCase = true)) {
                            currentProgram?.title = text
                        } else if (name.equals("desc", ignoreCase = true)) {
                            currentProgram?.description = text
                        }
                    }
                }
                eventType = parser.next()
            }
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext programs
    }

    private fun parseTime(timeStr: String?): Long {
        if (timeStr == null) return 0L
        return try {
            dateFormat.parse(timeStr)?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}

private class EpgProgramBuilder(
    val channelId: String,
    val startTime: Long,
    val endTime: Long,
    var title: String = "",
    var description: String? = null,
    var icon: String? = null
) {
    fun build(): EpgProgram {
        return EpgProgram(
            id = "${channelId}_$startTime",
            channelId = channelId,
            channelName = "", // Will map this later
            title = title,
            description = description,
            startTime = startTime,
            endTime = endTime,
            icon = icon
        )
    }
}
