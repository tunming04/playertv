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
public final class M3UParser_Factory implements Factory<M3UParser> {
  @Override
  public M3UParser get() {
    return newInstance();
  }

  public static M3UParser_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static M3UParser newInstance() {
    return new M3UParser();
  }

  private static final class InstanceHolder {
    private static final M3UParser_Factory INSTANCE = new M3UParser_Factory();
  }
}
