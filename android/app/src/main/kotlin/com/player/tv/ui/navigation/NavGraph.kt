package com.player.tv.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.player.tv.ui.home.HomeScreen
import com.player.tv.ui.channel.ChannelListScreen
import com.player.tv.ui.player.PlayerScreen
import com.player.tv.ui.player.PlayerViewModel
import com.player.tv.ui.settings.SettingsScreen
import com.player.tv.ui.playlist.AddPlaylistScreen
import com.player.tv.ui.theme.*
import com.player.tv.domain.model.StreamType

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Favorites : Screen("favorites")
    data object Search : Screen("search")
    data object Settings : Screen("settings")
    data object AddPlaylist : Screen("add_playlist")
    data object ChannelList : Screen("channel_list/{playlistId}/{streamType}") {
        fun createRoute(playlistId: String, streamType: StreamType): String {
            return "channel_list/$playlistId/${streamType.name}"
        }
    }
    data object Player : Screen("player?url={url}&name={name}") {
        fun createRoute(url: String, name: String): String {
            return "player?url=${android.net.Uri.encode(url)}&name=${android.net.Uri.encode(name)}"
        }
    }
}

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Default.Home, "Home"),
    BottomNavItem(Screen.Favorites, Icons.Default.Favorite, "Yêu thích"),
    BottomNavItem(Screen.AddPlaylist, Icons.Default.Add, "Nhập"),
    BottomNavItem(Screen.Settings, Icons.Default.Person, "Cá nhân")
)

@Composable
fun AppNavGraph(playerViewModel: PlayerViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.screen.route }
    val currentChannel by playerViewModel.currentChannel.collectAsState()
    val isPlaying by playerViewModel.isPlaying.collectAsState()

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = TextPrimary
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.label,
                                    tint = if (currentRoute == item.screen.route) Yellow else TextSecondary
                                )
                            },
                            label = {
                                Text(
                                    item.label,
                                    color = if (currentRoute == item.screen.route) Yellow else TextSecondary
                                )
                            },
                            selected = currentRoute == item.screen.route,
                            onClick = {
                                if (currentRoute != item.screen.route) {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        playerViewModel = playerViewModel,
                        onChannelClick = { channel ->
                            navController.navigate(Screen.Player.createRoute(channel.url, channel.name))
                        },
                        onPlaylistAddClick = {
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }

                composable(Screen.Favorites.route) {
                    com.player.tv.ui.favorites.FavoritesScreen(
                        onChannelClick = { channel ->
                            navController.navigate(Screen.Player.createRoute(channel.url, channel.name))
                        }
                    )
                }

                composable(Screen.Search.route) {
                    // Search screen
                }

                composable(Screen.Settings.route) {
                    val viewModel: SettingsViewModel = hiltViewModel()
                    val uiState by viewModel.uiState.collectAsState()

                    SettingsScreen(
                        playlists = uiState.playlists,
                        onAddPlaylist = {
                            navController.navigate(Screen.AddPlaylist.route)
                        },
                        onPlaylistClick = { playlist ->
                            navController.navigate(
                                Screen.ChannelList.createRoute(playlist.id, StreamType.LIVE)
                            )
                        },
                        onDeletePlaylist = { playlist ->
                            viewModel.deletePlaylist(playlist)
                        },
                        onRefreshPlaylist = { playlist ->
                            viewModel.refreshPlaylist(playlist)
                        },
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Screen.AddPlaylist.route) {
                    val viewModel: SettingsViewModel = hiltViewModel()

                    AddPlaylistScreen(
                        onImportUrl = { name, url ->
                            viewModel.importM3uFromUrl(url, name)
                            navController.popBackStack()
                        },
                        onImportText = { name, content ->
                            viewModel.importM3uFromText(content, name)
                            navController.popBackStack()
                        },
                        onImportXtream = { serverUrl, username, password ->
                            viewModel.importXtream(serverUrl, username, password)
                            navController.popBackStack()
                        },
                        onImportStalker = { serverUrl, macAddress ->
                            viewModel.importStalker(serverUrl, macAddress)
                            navController.popBackStack()
                        },
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = Screen.ChannelList.route,
                    arguments = listOf(
                        navArgument("playlistId") { type = NavType.StringType },
                        navArgument("streamType") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val playlistId = backStackEntry.arguments?.getString("playlistId") ?: return@composable
                    val streamType = StreamType.valueOf(
                        backStackEntry.arguments?.getString("streamType") ?: "LIVE"
                    )
                    val viewModel: ChannelListViewModel = hiltViewModel()
                    val uiState by viewModel.uiState.collectAsState()

                    LaunchedEffect(playlistId) {
                        viewModel.loadChannels(playlistId, streamType)
                    }

                    ChannelListScreen(
                        channels = uiState.channels,
                        groups = uiState.groups,
                        selectedGroup = uiState.selectedGroup,
                        onGroupSelected = { viewModel.selectGroup(it) },
                        onChannelClick = { channel ->
                            viewModel.addToRecent(channel, playlistId)
                            navController.navigate(Screen.Player.createRoute(channel.url, channel.name))
                        },
                        onFavoriteClick = { channel ->
                            viewModel.toggleFavorite(channel.id, playlistId)
                        },
                        isFavorite = { channelId ->
                            uiState.favorites.contains(channelId)
                        },
                        streamType = streamType,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = Screen.Player.route,
                    arguments = listOf(
                        navArgument("url") { type = NavType.StringType },
                        navArgument("name") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val url = backStackEntry.arguments?.getString("url") ?: return@composable
                    val name = backStackEntry.arguments?.getString("name") ?: return@composable

                    val channel = com.player.tv.domain.model.Channel(
                        id = url.hashCode().toString(),
                        name = name,
                        url = url,
                        streamFormat = if (url.endsWith(".m3u8")) com.player.tv.domain.model.StreamFormat.HLS else com.player.tv.domain.model.StreamFormat.OTHER
                    )

                    PlayerScreen(
                        channel = channel,
                        playerViewModel = playerViewModel,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            // Mini-player overlay khi đang play và không ở Home
            if (currentChannel != null && isPlaying && currentRoute == Screen.Favorites.route) {
                MiniPlayer(
                    channel = currentChannel!!,
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun MiniPlayer(
    channel: com.player.tv.domain.model.Channel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = DarkSurface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Yellow.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Yellow,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Đang phát: ${channel.name}",
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = "Quay về",
                tint = TextSecondary
            )
        }
    }
}
