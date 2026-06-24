package com.player.tv.ui.channel

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.player.tv.domain.model.Channel
import com.player.tv.domain.model.StreamType
import com.player.tv.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelListScreen(
    channels: List<Channel>,
    groups: List<String>,
    selectedGroup: String?,
    onGroupSelected: (String?) -> Unit,
    onChannelClick: (Channel) -> Unit,
    onFavoriteClick: (Channel) -> Unit,
    isFavorite: (String) -> Boolean,
    streamType: StreamType,
    onBackClick: () -> Unit
) {
    val title = when (streamType) {
        StreamType.LIVE -> "TV Trực tiếp"
        StreamType.VOD -> "Phim"
        StreamType.SERIES -> "Phim bộ"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Group filter chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedGroup == null,
                        onClick = { onGroupSelected(null) },
                        label = { Text("Tất cáº£") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Yellow,
                            selectedLabelColor = DarkBackground
                        )
                    )
                }
                items(groups) { group ->
                    FilterChip(
                        selected = selectedGroup == group,
                        onClick = { onGroupSelected(group) },
                        label = { Text(group) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Yellow,
                            selectedLabelColor = DarkBackground
                        )
                    )
                }
            }

            // Channel count
            Text(
                text = "${channels.size} kênh",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Channel list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(channels) { channel ->
                    ChannelItem(
                        channel = channel,
                        isFavorite = isFavorite(channel.id),
                        onClick = { onChannelClick(channel) },
                        onFavoriteClick = { onFavoriteClick(channel) }
                    )
                }
            }
        }
    }
}

@Composable
fun ChannelItem(
    channel: Channel,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlassBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Channel logo
            AsyncImage(
                model = channel.logo,
                contentDescription = channel.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GlassSurface),
                contentScale = ContentScale.Crop
            )

            // Channel info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (channel.groupTitle != null) {
                    Text(
                        text = channel.groupTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }

            // Live indicator
            if (channel.streamType == StreamType.LIVE) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(LiveRed)
                )
            }

            // Favorite button
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Yellow else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
