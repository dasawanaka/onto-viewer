package org.edmcouncil.spec.fibo.config.configuration.model;

import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.PostConstruct;
import org.edmcouncil.spec.fibo.config.configuration.loader.ConfigLoader;
import org.edmcouncil.spec.fibo.config.configuration.model.impl.ViewerCoreConfiguration;
import org.edmcouncil.spec.fibo.config.utils.files.FileSystemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Component
public class AppConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(AppConfiguration.class);

  @Autowired
  private FileSystemManager fileSystemManager;
  private ViewerCoreConfiguration viewerCoreConfig;

  public AppConfiguration() {
  }

  @PostConstruct
  public void init() {
    LOG.debug("Start loading configuration.");

    ConfigLoader cl = new ConfigLoader();
    Path configFilePath = null;

    try {
      configFilePath = fileSystemManager.getPathToWeaselConfigFile();

    } catch (IOException ex) {
      LOG.error("[ERROR] IOException while loading config file");
    }

    this.viewerCoreConfig = cl.loadWeaselConfiguration(configFilePath);

    if (!viewerCoreConfig.isEmpty()) {
      LOG.debug("Configuration: ");
      viewerCoreConfig.getConfiguration()
          .entrySet()
          .stream()
          .map((entry) -> {
            LOG.debug("\t{}", entry.getKey());
            return entry;
          }).forEachOrdered((entry) -> {
        entry.getValue()
            .stream()
            .map((object) -> object.toString())
            .forEachOrdered((s) -> {
              LOG.debug("\t\t{}", s);
            });
      });
    } else {
      LOG.debug("Use default weasel configuration");
    }

  }

  public ViewerCoreConfiguration getViewerCoreConfig() {
    return viewerCoreConfig;
  }

}
