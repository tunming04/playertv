package com.player.tv.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.player.tv.data.repository.AppRepository
import com.player.tv.domain.model.Channel
import com.player.tv.domain.model.StreamType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChannelListUiState(
    val channels: List<Channel> = emptyList(),
    val groups: List<String> = emptyList(),
    val selectedGroup: String? = null,
    val favorites: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ChannelListViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChannelListUiState())
    val uiState: StateFlow<ChannelListUiState> = _uiState.asStateFlow()

    private var currentPlaylistId: String? = null

    fun loadChannels(playlistId: String, streamType: StreamType) {
        currentPlaylistId = playlistId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.getGroups(playlistId).collect { groups ->
                _uiState.update { it.copy(groups = groups) }
            }
        }

        viewModelScope.launch {
            repository.getChannelsByPlaylist(playlistId).collect { channels ->
                val filteredChannels = channels.filter { it.streamType == streamType }
                _uiState.update {
                    it.copy(
                        channels = filteredChannels,
                        isLoading = false
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getAllFavorites().collect { favorites ->
                _uiState.update {
                    it.copy(favorites = favorites.map { f -> f.channelId }.toSet())
                }
            }
        }
    }

    fun selectGroup(group: String?) {
        _uiState.update { it.copy(selectedGroup = group) }

        val playlistId = currentPlaylistId ?: return
        viewModelScope.launch {
            if (group != null) {
                repository.getChannelsByGroup(playlistId, group).collect { channels ->
                    _uiState.update { it.copy(channels = channels) }
                }
            } else {
                repository.getChannelsByPlaylist(playlistId).collect { channels ->
                    _uiState.update { it.copy(channels = channels) }
                }
            }
        }
    }

    fun toggleFavorite(channelId: String, playlistId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(channelId, playlistId)
        }
    }

    fun addToRecent(channel: Channel, playlistId: String) {
        viewModelScope.launch {
            repository.addToRecent(channel, playlistId)
        }
    }
}
