package com.player.tv.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.player.tv.domain.model.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FavoritesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    private val _favoritesFlow = MutableStateFlow<List<Channel>>(emptyList())
    val favoritesFlow: StateFlow<List<Channel>> = _favoritesFlow.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        val json = prefs.getString("favorites_list", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<Channel>>() {}.type
                val channels: List<Channel> = gson.fromJson(json, type)
                // Decrypt URLs when loading
                _favoritesFlow.value = channels.map {
                    it.copy(url = com.player.tv.util.Constants.decryptUrl(it.url))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveFavorites(channels: List<Channel>) {
        // Encrypt URLs before saving to storage
        val encryptedChannels = channels.map {
            it.copy(url = com.player.tv.util.Constants.encryptUrl(it.url))
        }
        prefs.edit().putString("favorites_list", gson.toJson(encryptedChannels)).apply()
        _favoritesFlow.value = channels
    }

    fun isFavorite(url: String): Boolean {
        return _favoritesFlow.value.any { it.url == url }
    }

    fun toggleFavorite(channel: Channel) {
        val currentList = _favoritesFlow.value.toMutableList()
        val index = currentList.indexOfFirst { it.url == channel.url }
        if (index != -1) {
            currentList.removeAt(index)
        } else {
            currentList.add(channel)
        }
        saveFavorites(currentList)
    }
}
