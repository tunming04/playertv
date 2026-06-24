package com.player.tv.util

import com.player.tv.domain.model.Channel
import com.player.tv.domain.model.StreamFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

data class ParseResult(val epgUrl: String?, val channels: List<Channel>)

object M3uParser {
    suspend fun parse(url: String): ParseResult = withContext(Dispatchers.IO) {
        val channels = mutableListOf<Channel>()
        var epgUrl: String? = null
        try {
            val content = URL(url).readText()
            val lines = content.lines()
            var currentTvgName: String? = null
            var currentTvgId: String? = null
            var currentLogo: String? = null
            var currentGroup: String? = null
            
            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("#EXTM3U")) {
                    val urlMatch = Regex("""x-tvg-url="([^"]+)"""").find(trimmed)
                    epgUrl = urlMatch?.groupValues?.get(1)
                } else if (trimmed.startsWith("#EXTINF:")) {
                    // Extract info
                    val tvgNameMatch = Regex("""tvg-name="([^"]+)"""").find(trimmed)
                    val tvgIdMatch = Regex("""tvg-id="([^"]+)"""").find(trimmed)
                    val tvgLogoMatch = Regex("""tvg-logo="([^"]+)"""").find(trimmed)
                    val groupTitleMatch = Regex("""group-title="([^"]+)"""").find(trimmed)
                    
                    currentTvgName = tvgNameMatch?.groupValues?.get(1)
                    currentTvgId = tvgIdMatch?.groupValues?.get(1)
                    currentLogo = tvgLogoMatch?.groupValues?.get(1)
                    currentGroup = groupTitleMatch?.groupValues?.get(1)
                    
                    if (currentTvgName == null) {
                        // Fallback to name after comma
                        val commaIndex = trimmed.lastIndexOf(',')
                        if (commaIndex != -1 && commaIndex < trimmed.length - 1) {
                            currentTvgName = trimmed.substring(commaIndex + 1).trim()
                        }
                    }
                } else if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                    // It's a URL
                    if (currentTvgName != null) {
                        channels.add(
                            Channel(
                                id = trimmed.hashCode().toString(),
                                name = currentTvgName,
                                url = trimmed,
                                logo = currentLogo,
                                groupTitle = currentGroup,
                                tvgId = currentTvgId,
                                streamFormat = if (trimmed.endsWith(".m3u8")) StreamFormat.HLS else StreamFormat.OTHER
                            )
                        )
                        currentTvgName = null
                        currentTvgId = null
                        currentLogo = null
                        currentGroup = null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext ParseResult(epgUrl, channels)
    }
}
