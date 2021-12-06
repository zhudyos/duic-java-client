package io.zhudy.duic.config;

import java.util.Map;

/**
 * @author KK (kzou227@qq.com)
 */
public class ConfigResponse {

  /**
   * 版本.
   */
  private final String state;
  /**
   * 配置内容.
   */
  private final Map<String, Object> properties;

  public ConfigResponse(String state, Map<String, Object> properties) {
    this.state = state;
    this.properties = properties;
  }

  public String getState() {
    return state;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }
}
