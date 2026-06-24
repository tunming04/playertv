package com.player.tv.ui.home

import android.app.Activity
import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational
import android.util.Base64
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.player.tv.R
import com.player.tv.domain.model.Channel
import com.player.tv.domain.model.EpgProgram
import com.player.tv.ui.player.PlayerViewModel
import com.player.tv.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    playerViewModel: PlayerViewModel,
    onChannelClick: (Channel) -> Unit,
    onPlaylistAddClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    var channels by remember { mutableStateOf<List<Channel>>(emptyList()) }
    var filteredChannels by remember { mutableStateOf<List<Channel>>(emptyList()) }
    var epgMapping by remember { mutableStateOf<Map<String, EpgProgram>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGroup by remember { mutableStateOf<String>("Tất cả") }

    val currentChannel by playerViewModel.currentChannel.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val errorMessage by playerViewModel.errorMessage.collectAsState()

    val groups = remember(channels) {
        listOf("Tất cả") + channels.mapNotNull { it.groupTitle }.filter { it.isNotBlank() }.distinct().sorted()
    }

    // Player States
    var showControls by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }
    var showDrawer by remember { mutableStateOf(false) }

    // Load channels: cache-first → instant UI, then refresh in background
    LaunchedEffect(Unit) {
        // 1. Load from cache instantly
        val cached = com.player.tv.util.ChannelCache.load(context)
        if (cached.isNotEmpty()) {
            channels = cached
            filteredChannels = cached
            isLoading = false
            if (currentChannel == null) {
                playerViewModel.loadChannel(cached[0])
            }
        } else {
            isLoading = true
        }

        // 2. Fetch fresh data in background
        try {
            val response = withContext(Dispatchers.IO) {
                URL("https://playertv-app.buoisangvatoi.workers.dev/api/channels").readText()
            }
            val channelsArray = JSONArray(response)
            val adminChannels = mutableListOf<Channel>()
            for (i in 0 until channelsArray.length()) {
                val c = channelsArray.getJSONObject(i)
                val encodedUrl = c.getString("url")
                val decodedUrl = String(Base64.decode(encodedUrl, Base64.DEFAULT))
                adminChannels.add(Channel(
                    id = c.getString("name").hashCode().toString(),
                    name = c.getString("name"),
                    url = decodedUrl,
                    groupTitle = c.optString("group", ""),
                    logo = c.optString("logo", null)
                ))
            }
            if (adminChannels.isNotEmpty()) {
                channels = adminChannels
                filteredChannels = adminChannels
                com.player.tv.util.ChannelCache.save(context, adminChannels)
                if (currentChannel == null) {
                    playerViewModel.loadChannel(adminChannels[0])
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (channels.isEmpty()) {
                channels = emptyList()
                filteredChannels = emptyList()
            }
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(searchQuery, selectedGroup, channels) {
        var result = channels
        if (selectedGroup != "Tất cả") {
            result = result.filter { it.groupTitle == selectedGroup }
        }
        if (searchQuery.isNotEmpty()) {
            result = result.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.groupTitle?.contains(searchQuery, ignoreCase = true) == true
            }
        }
        filteredChannels = result
    }

    // Auto-hide controls
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3000)
            if(!showDrawer) {
                showControls = false
            }
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
            // 1. Logo - Tìm Kiếm
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo_new),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    placeholder = { Text("Tìm kiếm kênh...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GlassSurface,
                        unfocusedContainerColor = GlassSurface,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Yellow,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            // 2. Bạn đang xem kênh
            if (currentChannel != null) {
                Text(
                    text = "Bạn đang xem kênh: ${currentChannel!!.name}",
                    color = Yellow,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // 3. Videoplayer đang phát kênh
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = {
                        androidx.media3.ui.PlayerView(context).apply {
                            player = playerViewModel.exoPlayer
                            useController = false
                        }
                    },
                    modifier = Modifier.fillMaxSize().clickable { showControls = !showControls; showDrawer = false }
                )

                // Custom Overlay Controls
                androidx.compose.animation.AnimatedVisibility(
                    visible = showControls,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )) {
                        // Top Actions
                        Row(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                            IconButton(onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    val params = PictureInPictureParams.Builder()
                                        .setAspectRatio(Rational(16, 9))
                                        .build()
                                    activity?.enterPictureInPictureMode(params)
                                }
                            }) {
                                Icon(Icons.Default.PictureInPicture, "PIP", tint = Color.White)
                            }

                            IconButton(onClick = { showDrawer = true }) {
                                Icon(Icons.Default.List, "Channels", tint = Color.White)
                            }
                        }

                        // Bottom Actions
                        Row(
                            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = {
                                isMuted = !isMuted
                                playerViewModel.setMuted(isMuted)
                            }) {
                                Icon(if(isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp, "Volume", tint = Color.White)
                            }

                            IconButton(onClick = {
                                currentChannel?.let { onChannelClick(it) }
                            }) {
                                Icon(Icons.Default.Fullscreen, "Fullscreen", tint = Color.White)
                            }
                        }

                        // Bottom Title
                        currentChannel?.let {
                            Text(
                                text = it.name,
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                            )
                        }
                    }
                }

                // Channel Drawer Overlay
                androidx.compose.animation.AnimatedVisibility(
                    visible = showDrawer,
                    enter = slideInHorizontally(initialOffsetX = { it }),
                    exit = slideOutHorizontally(targetOffsetX = { it }),
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().fillMaxWidth(0.5f)
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f)),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        item {
                            Text("Danh sách kênh", color = Yellow, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(8.dp))
                            HorizontalDivider(color = Color.White.copy(alpha=0.2f))
                        }
                        items(filteredChannels) { c ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        playerViewModel.loadChannel(c)
                                        showDrawer = false
                                        showControls = false
                                    }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = c.name,
                                    color = if(c.id == currentChannel?.id) Yellow else Color.White,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Error overlay
                if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.8f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = errorMessage ?: "",
                                color = TextPrimary
                            )
                            Button(
                                onClick = { playerViewModel.clearError() }
                            ) {
                                Text("Đóng")
                            }
                        }
                    }
                }
            }

            // 4. List kênh chia cột và Group filter
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).weight(1f)) {
                if (groups.size > 1) {
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(groups) { group ->
                            FilterChip(
                                selected = selectedGroup == group,
                                onClick = { selectedGroup = group },
                                label = { Text(group) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Yellow,
                                    selectedLabelColor = DarkBackground,
                                    containerColor = DarkSurface,
                                    labelColor = TextPrimary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true, selected = selectedGroup == group,
                                    borderColor = if (selectedGroup == group) Yellow else Color.Transparent
                                )
                            )
                        }
                    }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Yellow)
                    }
                } else if (filteredChannels.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredChannels) { channel ->
                            val currentEpg = epgMapping[channel.tvgId]
                            val isChannelPlaying = currentChannel?.url == channel.url
                            ChannelCard(channel = channel, currentEpg = currentEpg, isPlaying = isChannelPlaying) {
                                playerViewModel.loadChannel(channel)
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
            .height(110.dp)
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
                            .background(Color.White.copy(alpha = 0.05f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Tv, contentDescription = null, tint = TextMuted)
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
