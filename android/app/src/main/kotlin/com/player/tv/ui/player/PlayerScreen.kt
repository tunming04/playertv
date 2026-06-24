package com.player.tv.ui.player

import android.app.PictureInPictureParams
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.player.tv.domain.model.Channel
import com.player.tv.ui.theme.*
import com.player.tv.util.FavoritesManager

@Composable
fun PlayerScreen(
    channel: Channel,
    playerViewModel: PlayerViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val currentChannel by playerViewModel.currentChannel.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()
    val errorMessage by playerViewModel.errorMessage.collectAsState()

    // Load channel if not already playing this one
    LaunchedEffect(channel.url) {
        if (currentChannel?.url != channel.url) {
            playerViewModel.loadChannel(channel)
        }
    }

    Scaffold(
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Video Player
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = playerViewModel.exoPlayer
                            useController = true
                            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

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
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = errorMessage ?: "",
                                color = TextPrimary
                            )
                            Button(
                                onClick = {
                                    playerViewModel.clearError()
                                    playerViewModel.loadChannel(channel)
                                }
                            ) {
                                Text("Thử lại")
                            }
                        }
                    }
                }
            }

            // Channel info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = channel.name,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = TextPrimary
                        )
                        if (channel.groupTitle != null) {
                            Text(
                                text = channel.groupTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                    // Favorite button
                    val favoritesManager = remember { FavoritesManager(context) }
                    var isFav by remember { mutableStateOf(favoritesManager.isFavorite(channel.url)) }

                    IconButton(
                        onClick = {
                            favoritesManager.toggleFavorite(channel)
                            isFav = favoritesManager.isFavorite(channel.url)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(GlassBackground)
                    ) {
                        Icon(
                            if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFav) Yellow else TextSecondary
                        )
                    }
                }
            }
        }
    }
}
