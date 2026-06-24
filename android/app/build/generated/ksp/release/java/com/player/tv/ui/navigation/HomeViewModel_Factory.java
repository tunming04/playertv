package com.player.tv.ui.navigation;

import com.player.tv.data.remote.M3UParser;
import com.player.tv.data.repository.AppRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<AppRepository> repositoryProvider;

  private final Provider<M3UParser> m3uParserProvider;

  public HomeViewModel_Factory(Provider<AppRepository> repositoryProvider,
      Provider<M3UParser> m3uParserProvider) {
    this.repositoryProvider = repositoryProvider;
    this.m3uParserProvider = m3uParserProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(repositoryProvider.get(), m3uParserProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<AppRepository> repositoryProvider,
      Provider<M3UParser> m3uParserProvider) {
    return new HomeViewModel_Factory(repositoryProvider, m3uParserProvider);
  }

  public static HomeViewModel newInstance(AppRepository repository, M3UParser m3uParser) {
    return new HomeViewModel(repository, m3uParser);
  }
}
