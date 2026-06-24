package com.player.tv.data.repository;

import com.player.tv.data.local.ChannelDao;
import com.player.tv.data.local.EpgDao;
import com.player.tv.data.local.FavoriteDao;
import com.player.tv.data.local.PlaylistDao;
import com.player.tv.data.local.RecentDao;
import com.player.tv.data.remote.M3UParser;
import com.player.tv.data.remote.StalkerApiService;
import com.player.tv.data.remote.XtreamApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class AppRepository_Factory implements Factory<AppRepository> {
  private final Provider<PlaylistDao> playlistDaoProvider;

  private final Provider<ChannelDao> channelDaoProvider;

  private final Provider<FavoriteDao> favoriteDaoProvider;

  private final Provider<RecentDao> recentDaoProvider;

  private final Provider<EpgDao> epgDaoProvider;

  private final Provider<M3UParser> m3uParserProvider;

  private final Provider<XtreamApiService> xtreamApiProvider;

  private final Provider<StalkerApiService> stalkerApiProvider;

  public AppRepository_Factory(Provider<PlaylistDao> playlistDaoProvider,
      Provider<ChannelDao> channelDaoProvider, Provider<FavoriteDao> favoriteDaoProvider,
      Provider<RecentDao> recentDaoProvider, Provider<EpgDao> epgDaoProvider,
      Provider<M3UParser> m3uParserProvider, Provider<XtreamApiService> xtreamApiProvider,
      Provider<StalkerApiService> stalkerApiProvider) {
    this.playlistDaoProvider = playlistDaoProvider;
    this.channelDaoProvider = channelDaoProvider;
    this.favoriteDaoProvider = favoriteDaoProvider;
    this.recentDaoProvider = recentDaoProvider;
    this.epgDaoProvider = epgDaoProvider;
    this.m3uParserProvider = m3uParserProvider;
    this.xtreamApiProvider = xtreamApiProvider;
    this.stalkerApiProvider = stalkerApiProvider;
  }

  @Override
  public AppRepository get() {
    return newInstance(playlistDaoProvider.get(), channelDaoProvider.get(), favoriteDaoProvider.get(), recentDaoProvider.get(), epgDaoProvider.get(), m3uParserProvider.get(), xtreamApiProvider.get(), stalkerApiProvider.get());
  }

  public static AppRepository_Factory create(Provider<PlaylistDao> playlistDaoProvider,
      Provider<ChannelDao> channelDaoProvider, Provider<FavoriteDao> favoriteDaoProvider,
      Provider<RecentDao> recentDaoProvider, Provider<EpgDao> epgDaoProvider,
      Provider<M3UParser> m3uParserProvider, Provider<XtreamApiService> xtreamApiProvider,
      Provider<StalkerApiService> stalkerApiProvider) {
    return new AppRepository_Factory(playlistDaoProvider, channelDaoProvider, favoriteDaoProvider, recentDaoProvider, epgDaoProvider, m3uParserProvider, xtreamApiProvider, stalkerApiProvider);
  }

  public static AppRepository newInstance(PlaylistDao playlistDao, ChannelDao channelDao,
      FavoriteDao favoriteDao, RecentDao recentDao, EpgDao epgDao, M3UParser m3uParser,
      XtreamApiService xtreamApi, StalkerApiService stalkerApi) {
    return new AppRepository(playlistDao, channelDao, favoriteDao, recentDao, epgDao, m3uParser, xtreamApi, stalkerApi);
  }
}
