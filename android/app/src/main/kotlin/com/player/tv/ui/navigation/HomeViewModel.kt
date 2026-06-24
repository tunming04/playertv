package com.player.tv.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.player.tv.data.local.RecentEntity
import com.player.tv.data.remote.M3UParser
import com.player.tv.data.repository.AppRepository
import com.player.tv.domain.model.DashboardStats
import com.player.tv.domain.model.Playlist
import com.player.tv.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val stats: DashboardStats = DashboardStats(),
    val recentChannels: List<RecentEntity> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AppRepository,
    private val m3uParser: M3UParser
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDefaultPlaylistIfNeeded()
        loadData()
    }

    private fun loadDefaultPlaylistIfNeeded() {
        viewModelScope.launch {
            repository.getAllPlaylists().first().let { playlists ->
                if (playlists.isEmpty()) {
                    // Try secure API first
                    try {
                        val channels = m3uParser.fetchFromSecureApi()
                        if (channels.isNotEmpty()) {
                            val m3uContent = buildString {
                                appendLine("#EXTM3U")
                                channels.forEach { ch ->
                                    appendLine("#EXTINF:-1 tvg-name=\"${ch.name}\" group-title=\"${ch.groupTitle ?: ""}\",${ch.name}")
                                    appendLine(ch.url)
                                }
                            }
                            repository.importM3uFromFile(m3uContent, "PlayerTV")
                        } else {
                            importBuiltInDefaults()
                        }
                    } catch (e: Exception) {
                        importBuiltInDefaults()
                    }
                }
            }
        }
    }

    private fun importBuiltInDefaults() {
        // Removed
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getAllPlaylists().collect { playlists ->
                val totalChannels = playlists.sumOf { it.channelCount }
                _uiState.update { state ->
                    state.copy(
                        playlists = playlists,
                        stats = DashboardStats(
                            liveChannels = totalChannels,
                            movies = 0,
                            series = 0
                        )
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.getRecentChannels().collect { recent ->
                _uiState.update { it.copy(recentChannels = recent) }
            }
        }
    }
}
