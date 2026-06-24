package com.player.tv.data.repository

import com.player.tv.data.local.*
import com.player.tv.data.remote.M3UParser
import com.player.tv.data.remote.StalkerApiService
import com.player.tv.data.remote.XtreamApiService
import com.player.tv.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val channelDao: ChannelDao,
    private val favoriteDao: FavoriteDao,
    private val recentDao: RecentDao,
    private val epgDao: EpgDao,
    private val m3uParser: M3UParser,
    private val xtreamApi: XtreamApiService,
    private val stalkerApi: StalkerApiService
) {
    // Playlist operations
    fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun importM3uFromUrl(url: String, name: String): Result<Playlist> {
        return try {
            val channels = m3uParser.parseFromUrl(url)
            if (channels.isEmpty()) {
                return Result.failure(Exception("No channels found"))
            }

            val playlist = Playlist(
                id = UUID.randomUUID().toString(),
                name = name,
                url = url,
                type = PlaylistType.M3U,
                channelCount = channels.size
            )

            playlistDao.insertPlaylist(playlist.toEntity())

            val channelEntities = channels.map { channel ->
                channel.toEntity(playlist.id)
            }
            channelDao.insertChannels(channelEntities)

            Result.success(playlist)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importM3uFromFile(content: String, name: String): Result<Playlist> {
        return try {
            val channels = m3uParser.parse(content)
            if (channels.isEmpty()) {
                return Result.failure(Exception("No channels found"))
            }

            val playlist = Playlist(
                id = UUID.randomUUID().toString(),
                name = name,
                type = PlaylistType.M3U_FILE,
                channelCount = channels.size
            )

            playlistDao.insertPlaylist(playlist.toEntity())

            val channelEntities = channels.map { channel ->
                channel.toEntity(playlist.id)
            }
            channelDao.insertChannels(channelEntities)

            Result.success(playlist)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importXtream(credentials: XtreamCredentials): Result<Playlist> {
        return try {
            val authResponse = xtreamApi.authenticate(
                credentials.serverUrl,
                credentials.username,
                credentials.password
            ) ?: return Result.failure(Exception("Authentication failed"))

            if (authResponse.user_info?.auth != 1) {
                return Result.failure(Exception("Invalid credentials"))
            }

            val playlist = Playlist(
                id = UUID.randomUUID().toString(),
                name = credentials.serverUrl.substringAfter("://").substringBefore("/"),
                serverUrl = credentials.serverUrl,
                username = credentials.username,
                password = credentials.password,
                type = PlaylistType.XTREAM
            )

            playlistDao.insertPlaylist(playlist.toEntity())

            // Fetch all live streams
            val channels = xtreamApi.getLiveStreams(
                credentials.serverUrl,
                credentials.username,
                credentials.password
            )

            val channelEntities = channels.map { xtreamChannel ->
                Channel(
                    id = "xtream_${xtreamChannel.stream_id}",
                    name = xtreamChannel.name,
                    url = xtreamApi.buildLiveStreamUrl(
                        credentials.serverUrl,
                        credentials.username,
                        credentials.password,
                        xtreamChannel.stream_id
                    ),
                    logo = xtreamChannel.stream_icon.ifEmpty { null },
                    groupTitle = xtreamChannel.category_id,
                    tvgId = xtreamChannel.epg_channel_id,
                    tvgName = xtreamChannel.name,
                    isRadio = false,
                    streamFormat = StreamFormat.HLS,
                    streamType = StreamType.LIVE
                ).toEntity(playlist.id)
            }

            channelDao.insertChannels(channelEntities)
            playlistDao.updatePlaylist(playlist.toEntity().copy(channelCount = channelEntities.size))

            Result.success(playlist.copy(channelCount = channelEntities.size))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importStalker(serverUrl: String, macAddress: String): Result<Playlist> {
        return try {
            val token = stalkerApi.handshake(serverUrl, macAddress)
                ?: return Result.failure(Exception("Authentication failed"))

            val playlist = Playlist(
                id = UUID.randomUUID().toString(),
                name = serverUrl.substringAfter("://").substringBefore("/"),
                serverUrl = serverUrl,
                type = PlaylistType.STALKER,
                macAddress = macAddress
            )

            playlistDao.insertPlaylist(playlist.toEntity())

            // Fetch live channels
            val stalkerChannels = stalkerApi.getLiveChannels(
                serverUrl, token, macAddress
            )

            val channelEntities = stalkerChannels.map { stalkerChannel ->
                stalkerApi.convertToChannel(stalkerChannel).toEntity(playlist.id)
            }

            channelDao.insertChannels(channelEntities)
            playlistDao.updatePlaylist(playlist.toEntity().copy(channelCount = channelEntities.size))

            Result.success(playlist.copy(channelCount = channelEntities.size))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Channel operations
    fun getChannelsByPlaylist(playlistId: String): Flow<List<Channel>> {
        return channelDao.getChannelsByPlaylist(playlistId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getChannelsByGroup(playlistId: String, groupTitle: String): Flow<List<Channel>> {
        return channelDao.getChannelsByGroup(playlistId, groupTitle).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getGroups(playlistId: String): Flow<List<String>> {
        return channelDao.getGroups(playlistId)
    }

    fun searchChannels(query: String): Flow<List<Channel>> {
        return channelDao.searchAllChannels(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    // Favorites operations
    fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    fun isFavorite(channelId: String): Flow<Boolean> {
        return favoriteDao.isFavorite(channelId)
    }

    suspend fun toggleFavorite(channelId: String, playlistId: String) {
        val existing = favoriteDao.getFavorite(channelId)
        if (existing != null) {
            favoriteDao.deleteFavorite(existing)
        } else {
            favoriteDao.insertFavorite(
                FavoriteEntity(
                    channelId = channelId,
                    playlistId = playlistId
                )
            )
        }
    }

    // Recent operations
    fun getRecentChannels(limit: Int = 20): Flow<List<RecentEntity>> {
        return recentDao.getRecentChannels(limit)
    }

    suspend fun addToRecent(channel: Channel, playlistId: String) {
        val existing = recentDao.getRecentByChannel(channel.id)
        if (existing != null) {
            recentDao.updateRecent(existing.copy(watchedAt = System.currentTimeMillis()))
        } else {
            recentDao.insertRecent(
                RecentEntity(
                    channelId = channel.id,
                    playlistId = playlistId,
                    channelName = channel.name,
                    channelLogo = channel.logo
                )
            )
        }
    }

    // EPG operations
    fun getCurrentProgram(channelId: String): Flow<EpgProgramEntity?> {
        return epgDao.getCurrentProgram(channelId, System.currentTimeMillis())
    }

    fun getProgramsForChannel(channelId: String): Flow<List<EpgProgramEntity>> {
        return epgDao.getProgramsForChannel(channelId, System.currentTimeMillis())
    }

    // Cleanup
    suspend fun deletePlaylist(playlistId: String) {
        channelDao.deleteChannelsByPlaylist(playlistId)
        playlistDao.deletePlaylistById(playlistId)
    }

    suspend fun refreshPlaylist(playlistId: String): Result<Unit> {
        return try {
            val playlist = playlistDao.getPlaylistById(playlistId)
                ?: return Result.failure(Exception("Playlist not found"))

            // Delete old channels
            channelDao.deleteChannelsByPlaylist(playlistId)

            // Re-import based on type
            when (playlist.type) {
                "M3U" -> {
                    playlist.url?.let { url ->
                        val channels = m3uParser.parseFromUrl(url)
                        val entities = channels.map { it.toEntity(playlistId) }
                        channelDao.insertChannels(entities)
                        playlistDao.updatePlaylist(playlist.copy(
                            channelCount = entities.size,
                            lastUpdated = System.currentTimeMillis()
                        ))
                    }
                }
                "XTREAM" -> {
                    // Re-authenticate and fetch
                }
                "STALKER" -> {
                    // Re-handshake and fetch
                }
                else -> {}
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Extension functions for mapping
fun Playlist.toEntity() = PlaylistEntity(
    id = id,
    name = name,
    url = url,
    filePath = filePath,
    type = type.name,
    channelCount = channelCount,
    lastUpdated = lastUpdated,
    epgUrl = epgUrl,
    username = username,
    password = password,
    serverUrl = serverUrl,
    macAddress = macAddress
)

fun PlaylistEntity.toDomain() = Playlist(
    id = id,
    name = name,
    url = url,
    filePath = filePath,
    type = PlaylistType.valueOf(type),
    channelCount = channelCount,
    lastUpdated = lastUpdated,
    epgUrl = epgUrl,
    username = username,
    password = password,
    serverUrl = serverUrl,
    macAddress = macAddress
)

fun Channel.toEntity(playlistId: String) = ChannelEntity(
    id = id,
    name = name,
    url = url,
    logo = logo,
    groupTitle = groupTitle,
    tvgId = tvgId,
    tvgName = tvgName,
    isRadio = isRadio,
    streamFormat = streamFormat.name,
    streamType = streamType.name,
    playlistId = playlistId
)

fun ChannelEntity.toDomain() = Channel(
    id = id,
    name = name,
    url = url,
    logo = logo,
    groupTitle = groupTitle,
    tvgId = tvgId,
    tvgName = tvgName,
    isRadio = isRadio,
    streamFormat = StreamFormat.valueOf(streamFormat),
    streamType = StreamType.valueOf(streamType)
)
