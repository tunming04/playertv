import sys

content = '''package com.player.tv.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.player.tv.domain.model.Channel
import com.player.tv.domain.model.EpgProgram
import com.player.tv.ui.theme.*
import com.player.tv.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onChannelClick: (Channel) -> Unit, // used for full screen if needed
    onPlaylistAddClick: () -> Unit // optional
) {
    val context = LocalContext.current
    var channels by remember { mutableStateOf<List<Channel>>(emptyList()) }
    var filteredChannels by remember { mutableStateOf<List<Channel>>(emptyList()) }
    var epgMapping by remember { mutableStateOf<Map<String, EpgProgram>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var playingChannel by remember { mutableStateOf<Channel?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Load default playlist
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val response = withContext(Dispatchers.IO) {
                URL("https://playertv-app.buoisangvatoi.workers.dev/api/playlist").readText()
            }
            val json = JSONObject(response)
            val channelsArray = json.getJSONArray("channels")
            val adminChannels = mutableListOf<Channel>()
            for (i in 0 until channelsArray.length()) {
                val c = channelsArray.getJSONObject(i)
                adminChannels.add(Channel(
                    id = c.getString("name").hashCode().toString(),
                    name = c.getString("name"),
                    url = c.getString("url"),
                    groupTitle = c.optString("group", ""),
                    logo = c.optString("logo", null)
                ))
            }
            channels = adminChannels
            filteredChannels = adminChannels
            if (adminChannels.isNotEmpty()) {
                playingChannel = adminChannels[0]
            }
        } catch (e: Exception) {
            e.printStackTrace()
            channels = emptyList()
            filteredChannels = emptyList()
        } finally {
            isLoading = false
        }
    }

    // Filter channels
    LaunchedEffect(searchQuery, channels) {
        if (searchQuery.isBlank()) {
            filteredChannels = channels
        } else {
            filteredChannels = channels.filter {
                it.name.contains(searchQuery, ignoreCase = true) || 
                (it.groupTitle?.contains(searchQuery, ignoreCase = true) == true)
            }
        }
    }

    // ExoPlayer Instance
    val exoPlayer = remember {
        val httpDataSourceFactory = androidx.media3.datasource.DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
            .setAllowCrossProtocolRedirects(true)
        val dataSourceFactory = androidx.media3.datasource.DefaultDataSource.Factory(context, httpDataSourceFactory)
        
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(androidx.media3.exoplayer.source.DefaultMediaSourceFactory(dataSourceFactory))
            .build()
    }

    // Update Player when playingChannel changes
    LaunchedEffect(playingChannel) {
        playingChannel?.let { channel ->
            val mediaItem = MediaItem.Builder()
                .setUri(channel.url)
                .build()
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Player Box (Top)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
                    .clickable {
                        playingChannel?.let { onChannelClick(it) } // Navigate to full screen
                    }
            ) {
                AndroidView(
                    factory = {
                        PlayerView(context).apply {
                            player = exoPlayer
                            useController = true
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Tìm ki?m kênh...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GlassSurface,
                        unfocusedContainerColor = GlassSurface,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Yellow,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Channel List
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Yellow)
                    }
                } else if (filteredChannels.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredChannels) { channel ->
                            val currentEpg = epgMapping[channel.tvgId]
                            val isPlaying = playingChannel?.url == channel.url
                            ChannelCard(channel = channel, currentEpg = currentEpg, isPlaying = isPlaying) {
                                playingChannel = channel
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChannelCard(channel: Channel, currentEpg: EpgProgram?, isPlaying: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isPlaying) Yellow.copy(alpha = 0.1f) else DarkSurface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                if (!channel.logo.isNullOrEmpty()) {
                    AsyncImage(
                        model = channel.logo,
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = channel.name.take(2).uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                
                if (!channel.groupTitle.isNullOrEmpty()) {
                    Surface(
                        color = Yellow.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = channel.groupTitle!!,
                            color = Yellow,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            Column {
                Text(
                    text = channel.name,
                    color = if (isPlaying) Yellow else TextPrimary,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                
                if (currentEpg != null) {
                    val now = System.currentTimeMillis()
                    val progress = if (now >= currentEpg.endTime) 1f else if (now <= currentEpg.startTime) 0f else {
                        (now - currentEpg.startTime).toFloat() / (currentEpg.endTime - currentEpg.startTime)
                    }
                    
                    Text(
                        text = currentEpg.title,
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = Yellow,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                } else {
                    Text(
                        text = "Không có thông tin EPG",
                        color = TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
'''

with open('e:/Code/iptv-cap/android/app/src/main/kotlin/com/player/tv/ui/home/HomeScreen.kt', 'w', encoding='utf-8') as f:
    f.write(content)
