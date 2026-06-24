package com.player.tv.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlists ORDER BY lastUpdated DESC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun getPlaylistById(id: String): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlists WHERE id = :id")
    suspend fun deletePlaylistById(id: String)
}

@Dao
interface ChannelDao {
    @Query("SELECT * FROM channels WHERE playlistId = :playlistId ORDER BY name ASC")
    fun getChannelsByPlaylist(playlistId: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE playlistId = :playlistId AND groupTitle = :groupTitle ORDER BY name ASC")
    fun getChannelsByGroup(playlistId: String, groupTitle: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE playlistId = :playlistId AND (name LIKE '%' || :query || '%' OR tvgName LIKE '%' || :query || '%')")
    fun searchChannels(playlistId: String, query: String): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE name LIKE '%' || :query || '%' OR tvgName LIKE '%' || :query || '%'")
    fun searchAllChannels(query: String): Flow<List<ChannelEntity>>

    @Query("SELECT DISTINCT groupTitle FROM channels WHERE playlistId = :playlistId AND groupTitle IS NOT NULL")
    fun getGroups(playlistId: String): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM channels WHERE playlistId = :playlistId")
    suspend fun getChannelCount(playlistId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<ChannelEntity>)

    @Query("DELETE FROM channels WHERE playlistId = :playlistId")
    suspend fun deleteChannelsByPlaylist(playlistId: String)

    @Query("DELETE FROM channels")
    suspend fun deleteAllChannels()
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites ORDER BY createdAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT * FROM favorites WHERE channelId = :channelId")
    suspend fun getFavorite(channelId: String): FavoriteEntity?

    @Query("SELECT * FROM favorites WHERE channelId = :channelId")
    fun observeFavorite(channelId: String): Flow<FavoriteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE channelId = :channelId")
    suspend fun deleteFavoriteByChannelId(channelId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE channelId = :channelId)")
    fun isFavorite(channelId: String): Flow<Boolean>
}

@Dao
interface RecentDao {
    @Query("SELECT * FROM recent ORDER BY watchedAt DESC LIMIT :limit")
    fun getRecentChannels(limit: Int = 20): Flow<List<RecentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecent(recent: RecentEntity)

    @Update
    suspend fun updateRecent(recent: RecentEntity)

    @Delete
    suspend fun deleteRecent(recent: RecentEntity)

    @Query("DELETE FROM recent WHERE channelId = :channelId")
    suspend fun deleteRecentByChannelId(channelId: String)

    @Query("SELECT * FROM recent WHERE channelId = :channelId")
    suspend fun getRecentByChannel(channelId: String): RecentEntity?
}

@Dao
interface EpgDao {
    @Query("SELECT * FROM epg_programs WHERE channelId = :channelId AND startTime >= :time ORDER BY startTime ASC")
    fun getProgramsForChannel(channelId: String, time: Long): Flow<List<EpgProgramEntity>>

    @Query("SELECT * FROM epg_programs WHERE channelId = :channelId AND startTime <= :time AND endTime > :time LIMIT 1")
    fun getCurrentProgram(channelId: String, time: Long): Flow<EpgProgramEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrograms(programs: List<EpgProgramEntity>)

    @Query("DELETE FROM epg_programs WHERE endTime < :time")
    suspend fun deleteOldPrograms(time: Long)

    @Query("DELETE FROM epg_programs")
    suspend fun deleteAllPrograms()
}
