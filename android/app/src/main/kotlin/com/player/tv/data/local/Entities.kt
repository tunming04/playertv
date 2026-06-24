package com.player.tv.data.local

import androidx.room.*

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val id: String,
    val name: String,
    val url: String? = null,
    val filePath: String? = null,
    val type: String,
    val channelCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val epgUrl: String? = null,
    val username: String? = null,
    val password: String? = null,
    val serverUrl: String? = null,
    val macAddress: String? = null
)

@Entity(
    tableName = "channels",
    foreignKeys = [ForeignKey(
        entity = PlaylistEntity::class,
        parentColumns = ["id"],
        childColumns = ["playlistId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("playlistId"), Index("groupTitle")]
)
data class ChannelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val url: String,
    val logo: String? = null,
    val groupTitle: String? = null,
    val tvgId: String? = null,
    val tvgName: String? = null,
    val isRadio: Boolean = false,
    val streamFormat: String = "HLS",
    val streamType: String = "LIVE",
    val playlistId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val channelId: String,
    val playlistId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "recent")
data class RecentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val channelId: String,
    val playlistId: String,
    val channelName: String,
    val channelLogo: String? = null,
    val watchedAt: Long = System.currentTimeMillis(),
    val playbackPosition: Long = 0
)

@Entity(tableName = "epg_programs")
data class EpgProgramEntity(
    @PrimaryKey val id: String,
    val channelId: String,
    val title: String,
    val description: String? = null,
    val startTime: Long,
    val endTime: Long,
    val icon: String? = null
)
