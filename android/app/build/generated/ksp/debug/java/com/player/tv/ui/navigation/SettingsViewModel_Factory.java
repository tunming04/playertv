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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<AppRepository> repositoryProvider;

  public SettingsViewModel_Factory(Provider<AppRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<AppRepository> repositoryProvider) {
    return new SettingsViewModel_Factory(repositoryProvider);
  }

  public static SettingsViewModel newInstance(AppRepository repository) {
    return new SettingsViewModel(repository);
  }
}
