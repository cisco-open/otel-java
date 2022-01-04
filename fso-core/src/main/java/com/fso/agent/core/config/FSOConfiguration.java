package com.fso.agent.core.config;

import java.util.Iterator;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Instrumentation config holds configuration for the instrumentation. */
public interface FSOConfiguration {
  boolean metadataOnly();

  Integer maxPayload();

  String token();

  class ConfigProvider {
    private static final Logger logger = LoggerFactory.getLogger(ConfigProvider.class);

    private static volatile FSOConfiguration fsoConfiguration;

    /** Reset the config, use only in tests. */
    public static void reset() {
      synchronized (ConfigProvider.class) {
        fsoConfiguration = null;
      }
    }

    private static FSOConfiguration load() {
      ServiceLoader<FSOConfiguration> configs = ServiceLoader.load(FSOConfiguration.class);
      Iterator<FSOConfiguration> iterator = configs.iterator();
      if (!iterator.hasNext()) {
        logger.error("Failed to load instrumentation config");
        return null;
      } else {
        FSOConfiguration configClass = iterator.next();
        logger.debug("Loaded: " + configClass.getClass().getName());
        return configClass;
      }
    }

    public static FSOConfiguration get() {
      if (fsoConfiguration == null) {
        synchronized (ConfigProvider.class) {
          if (fsoConfiguration == null) {
            fsoConfiguration = load();
          }
        }
      }
      return fsoConfiguration;
    }
  }
}
