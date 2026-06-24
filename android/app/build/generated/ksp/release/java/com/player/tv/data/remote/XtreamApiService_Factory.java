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
public final class XtreamApiService_Factory implements Factory<XtreamApiService> {
  @Override
  public XtreamApiService get() {
    return newInstance();
  }

  public static XtreamApiService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static XtreamApiService newInstance() {
    return new XtreamApiService();
  }

  private static final class InstanceHolder {
    private static final XtreamApiService_Factory INSTANCE = new XtreamApiService_Factory();
  }
}
