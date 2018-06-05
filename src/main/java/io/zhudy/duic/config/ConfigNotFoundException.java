/**
 * Copyright 2017-2018 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zhudy.duic.config;

/**
 * 未找到指定的配置项异常类型.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class ConfigNotFoundException extends RuntimeException {

    private String key;

    /**
     * 根据配置键创建异常对象.
     *
     * @param key 配置键
     */
    public ConfigNotFoundException(String key) {
        super(formatMessage(key));
        this.key = key;
    }

    /**
     * 根据配置键与目标异常创建异常对象.
     *
     * @param key   配置键
     * @param cause 目标异常
     */
    public ConfigNotFoundException(String key, Throwable cause) {
        super(formatMessage(key), cause);

        this.key = key;
    }

    /**
     * 返回未找到的配置键.
     *
     * @return 配置键
     */
    public String getKey() {
        return key;
    }

    private static String formatMessage(String propertyName) {
        return String.format("未找到键为 [%s] 的配置", propertyName);
    }
}
