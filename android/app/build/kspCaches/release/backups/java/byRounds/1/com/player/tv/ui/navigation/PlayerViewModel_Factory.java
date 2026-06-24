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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<AppRepository> repositoryProvider;

  public PlayerViewModel_Factory(Provider<AppRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static PlayerViewModel_Factory create(Provider<AppRepository> repositoryProvider) {
    return new PlayerViewModel_Factory(repositoryProvider);
  }

  public static PlayerViewModel newInstance(AppRepository repository) {
    return new PlayerViewModel(repository);
  }
}
