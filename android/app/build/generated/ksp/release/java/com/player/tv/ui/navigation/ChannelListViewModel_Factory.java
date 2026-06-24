package com.player.tv.ui.navigation;

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
public final class ChannelListViewModel_Factory implements Factory<ChannelListViewModel> {
  private final Provider<AppRepository> repositoryProvider;

  public ChannelListViewModel_Factory(Provider<AppRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ChannelListViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static ChannelListViewModel_Factory create(Provider<AppRepository> repositoryProvider) {
    return new ChannelListViewModel_Factory(repositoryProvider);
  }

  public static ChannelListViewModel newInstance(AppRepository repository) {
    return new ChannelListViewModel(repository);
  }
}
