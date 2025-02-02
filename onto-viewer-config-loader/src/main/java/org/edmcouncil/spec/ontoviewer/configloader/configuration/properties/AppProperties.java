package org.edmcouncil.spec.ontoviewer.configloader.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

/**
 *
 * @author Michał Daniel (michal.daniel@makolab.com)
 */
@Component
@PropertySources({
  @PropertySource("classpath:application.properties")
})
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private String defaultHomePath;
  private String viewerConfigFileName;
  private String defaultOntologyFileName;
  private String configPath;

  public String getConfigPath() {
    return configPath;
  }

  public void setConfigPath(String configPath) {
    this.configPath = configPath;
  }

  public String getDefaultHomePath() {
    return defaultHomePath;
  }

  public void setDefaultHomePath(String defaultHomePath) {
    this.defaultHomePath = defaultHomePath;
  }

  public String getViewerConfigFileName() {
    return viewerConfigFileName;
  }

  public void setViewerConfigFileName(String viewerConfigFileName) {
    this.viewerConfigFileName = viewerConfigFileName;
  }

  public String getDefaultOntologyFileName() {
    return defaultOntologyFileName;
  }

  public void setDefaultOntologyFileName(String defaultOntologyFileName) {
    this.defaultOntologyFileName = defaultOntologyFileName;
  }

}
