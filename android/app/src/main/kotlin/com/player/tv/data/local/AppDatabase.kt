package com.player.tv.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PlaylistEntity::class,
        ChannelEntity::class,
        FavoriteEntity::class,
        RecentEntity::class,
        EpgProgramEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playlistDao(): PlaylistDao
    abstract fun channelDao(): ChannelDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentDao(): RecentDao
    abstract fun epgDao(): EpgDao
}
