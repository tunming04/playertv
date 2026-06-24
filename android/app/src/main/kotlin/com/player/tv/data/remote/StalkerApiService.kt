package com.player.tv.data.remote

import com.player.tv.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class StalkerChannel(
    val id: Int = 0,
    val name: String = "",
    val cmd: String = "",
    val tv_genre_id: Int = 0,
    val number: String = "",
    val censored: Int = 0,
    val archive_duration: Int = 0,
    val archive: Int = 0,
    val epg: StalkerEpg? = null,
    val t_shift: Int = 0,
    val x_id: Int = 0,
    val t_time: String = "",
    val t_time_to: String = "",
    val logo: String = "",
    val curtain: String = ""
)

@Serializable
data class StalkerEpg(
    val e_id: String = "",
    val name: String = "",
    val t_time: String = "",
    val t_time_to: String = "",
    val genre: String = ""
)

@Serializable
data class StalkerGenre(
    val id: Int = 0,
    val name: String = "",
    val alias: String = ""
)

@Serializable
data class StalkerMovie(
    val id: Int = 0,
    val name: String = "",
    val cmd: String = "",
    val year: String = "",
    val time: String = "",
    val tv_genre_id: Int = 0,
    val director: String = "",
    val actors: String = "",
    val rating_imdb: String = "",
    val description: String = "",
    val logo: String = "",
    val genres: List<StalkerGenre> = emptyList()
)

@Serializable
data class StalkerOrderedListResponse(
    val js: StalkerJs? = null
)

@Serializable
data class StalkerJs(
    val data: List<StalkerChannel> = emptyList(),
    val total_items: Int = 0,
    val per_page: Int = 0,
    val start_page: Int = 0,
    val cur_page: Int = 0,
    val max_page_items: Int = 0
)

@Singleton
class StalkerApiService @Inject constructor() {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun handshake(
        serverUrl: String,
        macAddress: String,
        deviceId: String = "",
        stbType: String = "STB"
    ): String? = withContext(Dispatchers.IO) {
        try {
            val url = "$serverUrl/stalker_portal/server/load.php?action=handshake&type=stb&token=&JsHttpRequest=1&ver=7.4.7&device_id=$deviceId&device_id2=$macAddress&stb_type=$stbType"
            val connection = java.net.URL(url).openConnection()
            connection.setRequestProperty("Cookie", "mac=$macAddress")
            connection.setRequestProperty("X-User-Agent", "PortalForStb")
            connection.connect()

            // Extract token from response
            val response = connection.getInputStream().bufferedReader().readText()
            val token = extractToken(response)

            // Get MAC from cookie if not provided
            val cookies = connection.getHeaderField("Set-Cookie")
            token
        } catch (e: Exception) {
            null
        }
    }

    private fun extractToken(response: String): String? {
        return try {
            val json = json.parseToJsonElement(response)
            json.jsonObject["js"]?.jsonObject?.get("token")?.jsonPrimitive?.content
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getLiveChannels(
        serverUrl: String,
        token: String,
        macAddress: String,
        categoryId: Int? = null,
        page: Int = 1,
        perPage: Int = 50
    ): List<StalkerChannel> = withContext(Dispatchers.IO) {
        try {
            var url = "$serverUrl/stalker_portal/server/load.php?type=itv&retession_hours=24&action=get_ordered_list&p=$page&c=$perPage"
            if (categoryId != null) {
                url += "&genre=$categoryId"
            }
            val response = makeAuthenticatedRequest(url, token, macAddress)
            val result = json.decodeFromString<StalkerOrderedListResponse>(response)
            result.js?.data ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getLiveCategories(
        serverUrl: String,
        token: String,
        macAddress: String
    ): List<StalkerGenre> = withContext(Dispatchers.IO) {
        try {
            val url = "$serverUrl/stalker_portal/server/load.php?type=itv&action=get_genres"
            val response = makeAuthenticatedRequest(url, token, macAddress)
            val result = json.parseToJsonElement(response)
            val data = result.jsonObject["js"]?.jsonArray ?: return@withContext emptyList()
            data.map { element ->
                json.decodeFromJsonElement<StalkerGenre>(element)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getVodMovies(
        serverUrl: String,
        token: String,
        macAddress: String,
        categoryId: Int? = null,
        page: Int = 1,
        perPage: Int = 50
    ): List<StalkerMovie> = withContext(Dispatchers.IO) {
        try {
            var url = "$serverUrl/stalker_portal/server/load.php?type=vod&retession_hours=24&action=get_ordered_list&p=$page&c=$perPage"
            if (categoryId != null) {
                url += "&genre=$categoryId"
            }
            val response = makeAuthenticatedRequest(url, token, macAddress)
            val result = json.parseToJsonElement(response)
            val data = result.jsonObject["js"]?.jsonObject?.get("data")?.jsonArray ?: return@withContext emptyList()
            data.map { element ->
                json.decodeFromJsonElement<StalkerMovie>(element)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getVodCategories(
        serverUrl: String,
        token: String,
        macAddress: String
    ): List<StalkerGenre> = withContext(Dispatchers.IO) {
        try {
            val url = "$serverUrl/stalker_portal/server/load.php?type=vod&action=get_genres"
            val response = makeAuthenticatedRequest(url, token, macAddress)
            val result = json.parseToJsonElement(response)
            val data = result.jsonObject["js"]?.jsonArray ?: return@withContext emptyList()
            data.map { element ->
                json.decodeFromJsonElement<StalkerGenre>(element)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createLiveLink(
        serverUrl: String,
        token: String,
        macAddress: String,
        channelId: Int
    ): String? = withContext(Dispatchers.IO) {
        try {
            val url = "$serverUrl/stalker_portal/server/load.php?type=itv&action=create_link&ch_id=$channelId"
            val response = makeAuthenticatedRequest(url, token, macAddress)
            val result = json.parseToJsonElement(response)
            result.jsonObject["js"]?.jsonObject?.get("cmd")?.jsonPrimitive?.content
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createVodLink(
        serverUrl: String,
        token: String,
        macAddress: String,
        movieId: Int
    ): String? = withContext(Dispatchers.IO) {
        try {
            val url = "$serverUrl/stalker_portal/server/load.php?type=vod&action=create_link&movie_id=$movieId"
            val response = makeAuthenticatedRequest(url, token, macAddress)
            val result = json.parseToJsonElement(response)
            result.jsonObject["js"]?.jsonObject?.get("cmd")?.jsonPrimitive?.content
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getChannelEpg(
        serverUrl: String,
        token: String,
        macAddress: String,
        channelId: Int
    ): List<StalkerEpg> = withContext(Dispatchers.IO) {
        try {
            val url = "$serverUrl/stalker_portal/server/load.php?type=itv&action=get_short_epg&ch_id=$channelId&P=1&L=24"
            val response = makeAuthenticatedRequest(url, token, macAddress)
            val result = json.parseToJsonElement(response)
            val epgs = result.jsonObject["js"]?.jsonObject?.get("epg_listings")?.jsonArray ?: return@withContext emptyList()
            epgs.map { element ->
                json.decodeFromJsonElement<StalkerEpg>(element)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun makeAuthenticatedRequest(url: String, token: String, macAddress: String): String {
        val connection = java.net.URL(url).openConnection()
        connection.setRequestProperty("Cookie", "mac=$macAddress;stb_token=$token")
        connection.setRequestProperty("Authorization", "Bearer $token")
        connection.setRequestProperty("X-User-Agent", "PortalForStb")
        connection.connect()
        return connection.getInputStream().bufferedReader().readText()
    }

    fun convertToChannel(stalkerChannel: StalkerChannel): Channel {
        return Channel(
            id = "stalker_${stalkerChannel.id}",
            name = stalkerChannel.name,
            url = "", // Will be filled when creating link
            logo = stalkerChannel.logo.ifEmpty { null },
            groupTitle = null,
            tvgId = null,
            tvgName = stalkerChannel.name,
            isRadio = false,
            streamFormat = StreamFormat.HLS,
            streamType = StreamType.LIVE
        )
    }

    fun convertToChannel(stalkerMovie: StalkerMovie): Channel {
        return Channel(
            id = "stalker_vod_${stalkerMovie.id}",
            name = stalkerMovie.name,
            url = "", // Will be filled when creating link
            logo = stalkerMovie.logo.ifEmpty { null },
            groupTitle = stalkerMovie.genres.firstOrNull()?.name,
            tvgId = null,
            tvgName = stalkerMovie.name,
            isRadio = false,
            streamFormat = StreamFormat.HLS,
            streamType = StreamType.VOD
        )
    }
}
