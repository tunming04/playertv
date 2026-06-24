package com.player.tv.data.remote

import com.player.tv.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class XtreamResponse(
    val user_info: UserInfo? = null,
    val server_info: ServerInfo? = null
)

@Serializable
data class UserInfo(
    val username: String = "",
    val password: String = "",
    val message: String = "",
    val auth: Int = 0,
    val status: String = "",
    val exp_date: String = "",
    val is_trial: String = "",
    val active_cons: String = "",
    val created_at: String = "",
    val max_connections: String = "",
    val allowed_output_formats: List<String> = emptyList()
)

@Serializable
data class ServerInfo(
    val url: String = "",
    val port: String = "",
    val https_port: String = "",
    val server_protocol: String = "",
    val rtmp_port: String = "",
    val timezone: String = "",
    val timestamp_now: Long = 0,
    val time: String = ""
)

@Serializable
data class XtreamCategory(
    val category_id: String = "",
    val category_name: String = "",
    val parent_id: Int = 0
)

@Serializable
data class XtreamChannel(
    val num: Int = 0,
    val name: String = "",
    val stream_type: String = "",
    val stream_id: Int = 0,
    val stream_icon: String = "",
    val epg_channel_id: String? = null,
    val added: String = "",
    val is_adult: String = "0",
    val category_id: String = "",
    val category_ids: List<Int> = emptyList(),
    val custom_sid: String = "",
    val tv_archive: Int = 0,
    val direct_source: String = "",
    val tv_archive_duration: Int = 0
)

@Serializable
data class XtreamSeries(
    val series_id: Int = 0,
    val name: String = "",
    val cover: String = "",
    val plot: String = "",
    val cast: String = "",
    val director: String = "",
    val genre: String = "",
    val releaseDate: String = "",
    val duration: String = "",
    val rating: String = "",
    val backdrop_path: List<String> = emptyList()
)

@Serializable
data class XtreamSeriesInfo(
    val seasons: List<XtreamSeason> = emptyList(),
    val episodes: Map<String, List<XtreamEpisode>> = emptyMap()
)

@Serializable
data class XtreamSeason(
    val air_date: String = "",
    val episode_count: Int = 0,
    val id: Int = 0,
    val name: String = "",
    val overview: String = "",
    val season_number: Int = 0,
    val cover: String = "",
    val cover_big: String = ""
)

@Serializable
data class XtreamEpisode(
    val id: String = "",
    val episode_num: Int = 0,
    val title: String = "",
    val container_extension: String = "",
    val info: XtreamEpisodeInfo = XtreamEpisodeInfo(),
    val custom_sid: String = "",
    val added: String = "",
    val season: Int = 0,
    val direct_source: String = ""
)

@Serializable
data class XtreamEpisodeInfo(
    val tmdb_id: Int = 0,
    val releasedate: String = "",
    val plot: String = "",
    val duration_secs: Int = 0,
    val duration: String = "",
    val movie_image: String = "",
    val rating: String = "",
    val name: String = ""
)

@Singleton
class XtreamApiService @Inject constructor() {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun authenticate(
        serverUrl: String,
        username: String,
        password: String
    ): XtreamResponse? = withContext(Dispatchers.IO) {
        try {
            val url = "$serverUrl/player_api.php?username=$username&password=$password"
            val response = java.net.URL(url).readText()
            json.decodeFromString<XtreamResponse>(response)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getLiveCategories(
        serverUrl: String,
        username: String,
        password: String
    ): List<XtreamCategory> = withContext(Dispatchers.IO) {
        try {
            val url = "$serverUrl/player_api.php?username=$username&password=$password&action=get_live_categories"
            val response = java.net.URL(url).readText()
            json.decodeFromString<List<XtreamCategory>>(response)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getLiveStreams(
        serverUrl: String,
        username: String,
        password: String,
        categoryId: String? = null
    ): List<XtreamChannel> = withContext(Dispatchers.IO) {
        try {
            var url = "$serverUrl/player_api.php?username=$username&password=$password&action=get_live_streams"
            if (categoryId != null) {
                url += "&category_id=$categoryId"
            }
            val response = java.net.URL(url).readText()
            json.decodeFromString<List<XtreamChannel>>(response)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getVodCategories(
        serverUrl: String,
        username: String,
        password: String
    ): List<XtreamCategory> = withContext(Dispatchers.IO) {
        try {
            val url = "$serverUrl/player_api.php?username=$username&password=$password&action=get_vod_categories"
            val response = java.net.URL(url).readText()
            json.decodeFromString<List<XtreamCategory>>(response)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getVodStreams(
        serverUrl: String,
        username: String,
        password: String,
        categoryId: String? = null
    ): List<XtreamChannel> = withContext(Dispatchers.IO) {
        try {
            var url = "$serverUrl/player_api.php?username=$username&password=$password&action=get_vod_streams"
            if (categoryId != null) {
                url += "&category_id=$categoryId"
            }
            val response = java.net.URL(url).readText()
            json.decodeFromString<List<XtreamChannel>>(response)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSeries(
        serverUrl: String,
        username: String,
        password: String,
        categoryId: String? = null
    ): List<XtreamSeries> = withContext(Dispatchers.IO) {
        try {
            var url = "$serverUrl/player_api.php?username=$username&password=$password&action=get_series"
            if (categoryId != null) {
                url += "&category_id=$categoryId"
            }
            val response = java.net.URL(url).readText()
            json.decodeFromString<List<XtreamSeries>>(response)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSeriesInfo(
        serverUrl: String,
        username: String,
        password: String,
        seriesId: Int
    ): XtreamSeriesInfo? = withContext(Dispatchers.IO) {
        try {
            val url = "$serverUrl/player_api.php?username=$username&password=$password&action=get_series_info&series_id=$seriesId"
            val response = java.net.URL(url).readText()
            json.decodeFromString<XtreamSeriesInfo>(response)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getShortEpg(
        serverUrl: String,
        username: String,
        password: String,
        streamId: Int
    ): JsonElement? = withContext(Dispatchers.IO) {
        try {
            val url = "$serverUrl/player_api.php?username=$username&password=$password&action=get_short_epg&stream_id=$streamId"
            val response = java.net.URL(url).readText()
            json.parseToJsonElement(response)
        } catch (e: Exception) {
            null
        }
    }

    fun buildLiveStreamUrl(
        serverUrl: String,
        username: String,
        password: String,
        streamId: Int
    ): String {
        return "$serverUrl/live/$username/$password/$streamId.m3u8"
    }

    fun buildVodStreamUrl(
        serverUrl: String,
        username: String,
        password: String,
        streamId: Int,
        extension: String = "mp4"
    ): String {
        return "$serverUrl/movie/$username/$password/$streamId.$extension"
    }

    fun buildSeriesStreamUrl(
        serverUrl: String,
        username: String,
        password: String,
        episodeId: String,
        extension: String = "mp4"
    ): String {
        return "$serverUrl/series/$username/$password/$episodeId.$extension"
    }
}
