package com.player.tv.ui.playlist

import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.player.tv.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaylistScreen(
    onImportUrl: (String, String) -> Unit,
    onImportText: (String, String) -> Unit,
    onImportXtream: (String, String, String) -> Unit,
    onImportStalker: (String, String) -> Unit,
    onBackClick: () -> Unit
) {
    var inputUrl by remember { mutableStateOf("") }
    var playingUrl by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    
    // Player controls
    var showControls by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(false) }

    LaunchedEffect(playingUrl) {
        playingUrl?.let { url ->
            val mediaItem = MediaItem.fromUri(url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
            exoPlayer.volume = if(isMuted) 0f else 1f
        }
    }
    
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3000)
            showControls = false
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Phát Link M3U8") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = DarkSurface,
                titleContentColor = TextPrimary
            )
        )
        
        // Input Area
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = inputUrl,
                onValueChange = { inputUrl = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Dán link M3U8 vào đây...", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = TextMuted) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = GlassSurface,
                    unfocusedContainerColor = GlassSurface,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Yellow,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 1,
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = { 
                    if (inputUrl.isNotBlank()) {
                        playingUrl = inputUrl.trim()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Yellow),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = DarkBackground)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Phát Video", color = DarkBackground, style = MaterialTheme.typography.titleMedium)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Player Area
        if (playingUrl != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = {
                        PlayerView(context).apply {
                            player = exoPlayer
                            useController = false
                        }
                    },
                    modifier = Modifier.fillMaxSize().clickable { showControls = !showControls }
                )
                
                // Custom Controls
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
                        Row(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                            // Volume Toggle
                            IconButton(onClick = { 
                                isMuted = !isMuted
                                exoPlayer.volume = if(isMuted) 0f else 1f 
                            }) {
                                Icon(if(isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp, "Volume", tint = Color.White)
                            }
                            
                            // PIP
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
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .padding(16.dp)
                    .background(DarkSurface, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Tv, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Trình phát sẽ hiển thị ở đây", color = TextMuted)
                }
            }
        }
    }
}
