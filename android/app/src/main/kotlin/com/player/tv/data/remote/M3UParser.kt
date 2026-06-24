package com.player.tv.data.remote

import com.player.tv.domain.model.Channel
import com.player.tv.domain.model.StreamFormat
import com.player.tv.domain.model.StreamType
import com.player.tv.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class M3UParser @Inject constructor() {

    /**
     * Fetch channels from secure API (with token + obfuscated URLs)
     */
    suspend fun fetchFromSecureApi(): List<Channel> = withContext(Dispatchers.IO) {
        try {
            val playlistUrl = Constants.getPlaylistUrl()
            val content = URL(playlistUrl).readText()
            
            // Parse JSON response
            val json = Json { ignoreUnknownKeys = true }
            val playlist = json.parseToJsonElement(content).jsonObject
            val channelsJson = playlist["channels"]?.jsonArray ?: return@withContext emptyList()
            
            channelsJson.map { element ->
                val ch = element.jsonObject
                val name = ch["name"]?.jsonPrimitive?.content ?: ""
                val group = ch["group"]?.jsonPrimitive?.content ?: ""
                val encodedUrl = ch["url"]?.jsonPrimitive?.content ?: ""
                
                // Decode obfuscated URL
                val url = Constants.decodeUrl(encodedUrl)
                
                Channel(
                    id = generateId(url),
                    name = name,
                    url = url,
                    logo = null,
                    groupTitle = group,
                    tvgId = null,
                    tvgName = name,
                    isRadio = false,
                    streamFormat = detectStreamFormat(url),
                    streamType = StreamType.LIVE
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun parseFromUrl(url: String): List<Channel> = withContext(Dispatchers.IO) {
        try {
            val content = URL(url).readText()
            parse(content)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun parse(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines = content.lines()

        var i = 0
        while (i < lines.size) {
            val line = lines[i].trim()

            if (line.startsWith("#EXTINF:")) {
                val attributes = parseExtInfAttributes(line)
                val displayName = parseDisplayName(line)

                // Get the stream URL from the next non-empty, non-comment line
                var url = ""
                var j = i + 1
                while (j < lines.size) {
                    val nextLine = lines[j].trim()
                    if (nextLine.isNotEmpty() && !nextLine.startsWith("#")) {
                        url = nextLine
                        break
                    }
                    j++
                }

                if (url.isNotEmpty()) {
                    val channel = Channel(
                        id = attributes["tvg-id"] ?: generateId(url),
                        name = attributes["tvg-name"] ?: displayName,
                        url = url,
                        logo = attributes["tvg-logo"],
                        groupTitle = attributes["group-title"],
                        tvgId = attributes["tvg-id"],
                        tvgName = attributes["tvg-name"],
                        isRadio = attributes["radio"]?.lowercase() == "true",
                        streamFormat = detectStreamFormat(url),
                        streamType = detectStreamType(url, attributes)
                    )
                    channels.add(channel)
                }
            }
            i++
        }

        return channels
    }

    private fun parseExtInfAttributes(line: String): Map<String, String> {
        val attributes = mutableMapOf<String, String>()

        // Parse key="value" patterns
        val regex = Regex("""(\w[\w-]*)="([^"]*)""")
        regex.findAll(line).forEach { match ->
            attributes[match.groupValues[1]] = match.groupValues[2]
        }

        // Parse key='value' patterns
        val singleQuoteRegex = Regex("""(\w[\w-]*)='([^']*)""")
        singleQuoteRegex.findAll(line).forEach { match ->
            attributes[match.groupValues[1]] = match.groupValues[2]
        }

        // Parse group-title without quotes
        val groupRegex = Regex("""group-title=([^\s,]+)""")
        groupRegex.find(line)?.let {
            if (!attributes.containsKey("group-title")) {
                attributes["group-title"] = it.groupValues[1]
            }
        }

        return attributes
    }

    private fun parseDisplayName(line: String): String {
        // Extract name after the last comma
        val lastCommaIndex = line.lastIndexOf(',')
        return if (lastCommaIndex != -1 && lastCommaIndex < line.length - 1) {
            line.substring(lastCommaIndex + 1).trim()
        } else {
            "Unknown Channel"
        }
    }

    private fun detectStreamFormat(url: String): StreamFormat {
        val lowerUrl = url.lowercase()
        return when {
            lowerUrl.contains(".m3u8") || lowerUrl.contains("m3u8") -> StreamFormat.HLS
            lowerUrl.contains(".mpd") || lowerUrl.contains("dash") -> StreamFormat.DASH
            lowerUrl.contains(".ts") || lowerUrl.contains("mpegts") -> StreamFormat.MPEGTS
            lowerUrl.contains("rtmp") -> StreamFormat.RTMP
            else -> StreamFormat.HLS
        }
    }

    private fun detectStreamType(url: String, attributes: Map<String, String>): StreamType {
        val groupTitle = attributes["group-title"]?.lowercase() ?: ""
        val radio = attributes["radio"]?.lowercase() == "true"

        return when {
            radio -> StreamType.LIVE
            groupTitle.contains("movie") || groupTitle.contains("phim") -> StreamType.VOD
            groupTitle.contains("series") || groupTitle.contains("phim bộ") -> StreamType.SERIES
            else -> StreamType.LIVE
        }
    }

    private fun generateId(url: String): String {
        return url.hashCode().toString().replace("-", "")
    }
}
