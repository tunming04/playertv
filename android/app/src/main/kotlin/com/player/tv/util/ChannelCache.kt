package com.player.tv.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.player.tv.domain.model.Channel

object ChannelCache {
    private const val CACHE_KEY = "cached_channels"
    private const val CACHE_TIME_KEY = "cache_timestamp"
    private const val CACHE_MAX_AGE_MS = 6 * 60 * 60 * 1000L // 6 hours

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences("channel_cache", Context.MODE_PRIVATE)

    fun save(context: Context, channels: List<Channel>) {
        val json = Gson().toJson(channels)
        prefs(context).edit()
            .putString(CACHE_KEY, json)
            .putLong(CACHE_TIME_KEY, System.currentTimeMillis())
            .apply()
    }

    fun load(context: Context): List<Channel> {
        val json = prefs(context).getString(CACHE_KEY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Channel>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun isStale(context: Context): Boolean {
        val timestamp = prefs(context).getLong(CACHE_TIME_KEY, 0)
        return System.currentTimeMillis() - timestamp > CACHE_MAX_AGE_MS
    }
}
