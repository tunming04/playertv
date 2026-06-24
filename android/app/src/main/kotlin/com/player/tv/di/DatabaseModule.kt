package com.player.tv.di

import android.content.Context
import androidx.room.Room
import com.player.tv.data.local.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "playertv.db"
        ).build()
    }

    @Provides
    fun providePlaylistDao(database: AppDatabase): PlaylistDao {
        return database.playlistDao()
    }

    @Provides
    fun provideChannelDao(database: AppDatabase): ChannelDao {
        return database.channelDao()
    }

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    fun provideRecentDao(database: AppDatabase): RecentDao {
        return database.recentDao()
    }

    @Provides
    fun provideEpgDao(database: AppDatabase): EpgDao {
        return database.epgDao()
    }
}
