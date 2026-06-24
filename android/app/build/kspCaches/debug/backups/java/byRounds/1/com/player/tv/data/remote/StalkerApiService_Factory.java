package com.player.tv.data.remote;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class StalkerApiService_Factory implements Factory<StalkerApiService> {
  @Override
  public StalkerApiService get() {
    return newInstance();
  }

  public static StalkerApiService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static StalkerApiService newInstance() {
    return new StalkerApiService();
  }

  private static final class InstanceHolder {
    private static final StalkerApiService_Factory INSTANCE = new StalkerApiService_Factory();
  }
}
