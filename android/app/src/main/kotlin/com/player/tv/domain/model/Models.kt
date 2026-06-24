package com.player.tv.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    val id: String,
    val name: String,
    val url: String,
    val logo: String? = null,
    val groupTitle: String? = null,
    val tvgId: String? = null,
    val tvgName: String? = null,
    val isRadio: Boolean = false,
    val streamFormat: StreamFormat = StreamFormat.HLS,
    val streamType: StreamType = StreamType.LIVE
)

enum class StreamFormat { HLS, DASH, MPEGTS, RTMP, OTHER }
enum class StreamType { LIVE, VOD, SERIES }

@Serializable
data class Playlist(
    val id: String,
    val name: String,
    val url: String? = null,
    val filePath: String? = null,
    val type: PlaylistType,
    val channelCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val epgUrl: String? = null,
    // Xtream credentials
    val username: String? = null,
    val password: String? = null,
    val serverUrl: String? = null,
    val macAddress: String? = null
)

enum class PlaylistType { M3U, M3U_FILE, XTREAM, STALKER }

@Serializable
data class EpgProgram(
    val id: String,
    val channelId: String,
    val channelName: String,
    val title: String,
    val description: String? = null,
    val startTime: Long,
    val endTime: Long,
    val icon: String? = null
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val channelCount: Int = 0
)

@Serializable
data class XtreamCredentials(
    val serverUrl: String,
    val username: String,
    val password: String
)

@Serializable
data class StalkerCredentials(
    val serverUrl: String,
    val macAddress: String? = null,
    val token: String? = null
)

data class DashboardStats(
    val liveChannels: Int = 0,
    val movies: Int = 0,
    val series: Int = 0,
    val lastUpdate: String = ""
)
