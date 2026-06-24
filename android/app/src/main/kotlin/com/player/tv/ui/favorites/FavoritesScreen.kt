package com.player.tv.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.player.tv.domain.model.Channel
import com.player.tv.ui.theme.*
import com.player.tv.util.FavoritesManager

@Composable
fun FavoritesScreen(
    onChannelClick: (Channel) -> Unit
) {
    val context = LocalContext.current
    val favoritesManager = remember { FavoritesManager(context) }
    val channels by favoritesManager.favoritesFlow.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Kênh Yêu Thích",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 16.dp, top = 24.dp)
            )

            if (channels.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có kênh yêu thích nào", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(channels) { channel ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GlassSurface)
                                .clickable { onChannelClick(channel) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!channel.logo.isNullOrEmpty()) {
                                AsyncImage(
                                    model = channel.logo,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.Black)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                            Column {
                                Text(
                                    text = channel.name,
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                                )
                                if (!channel.groupTitle.isNullOrEmpty()) {
                                    Text(
                                        text = channel.groupTitle,
                                        color = TextSecondary,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
