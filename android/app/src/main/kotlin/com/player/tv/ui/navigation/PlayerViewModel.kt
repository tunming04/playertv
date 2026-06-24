package com.player.tv.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.player.tv.data.repository.AppRepository
import com.player.tv.domain.model.Channel
import com.player.tv.domain.model.EpgProgram
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val channel: Channel? = null,
    val currentProgram: EpgProgram? = null,
    val upcomingPrograms: List<EpgProgram> = emptyList(),
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var currentChannelId: String? = null

    fun loadChannel(channelId: String) {
        currentChannelId = channelId

        // Load channel from all playlists
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Search for channel in all playlists
            repository.searchChannels(channelId).firstOrNull()?.let { channels ->
                val channel = channels.find { it.id == channelId }
                _uiState.update {
                    it.copy(channel = channel, isLoading = false)
                }

                // Check favorite status
                channel?.let { ch ->
                    repository.isFavorite(ch.id).collect { isFav ->
                        _uiState.update { it.copy(isFavorite = isFav) }
                    }
                }
            }
        }

        // Load EPG
        viewModelScope.launch {
            repository.getCurrentProgram(channelId).collect { program ->
                _uiState.update {
                    it.copy(
                        currentProgram = program?.let { p ->
                            EpgProgram(
                                id = p.id,
                                channelId = p.channelId,
                                channelName = "",
                                title = p.title,
                                description = p.description,
                                startTime = p.startTime,
                                endTime = p.endTime,
                                icon = p.icon
                            )
                        }
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getProgramsForChannel(channelId).collect { programs ->
                val epgPrograms = programs.map { p ->
                    EpgProgram(
                        id = p.id,
                        channelId = p.channelId,
                        channelName = "",
                        title = p.title,
                        description = p.description,
                        startTime = p.startTime,
                        endTime = p.endTime,
                        icon = p.icon
                    )
                }
                _uiState.update { it.copy(upcomingPrograms = epgPrograms) }
            }
        }
    }

    fun toggleFavorite() {
        val channel = _uiState.value.channel ?: return
        val playlistId = channel.id.substringBefore("_")

        viewModelScope.launch {
            repository.toggleFavorite(channel.id, playlistId)
        }
    }
}
