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

import io.zhudy.duic.config.util.PropertyPlaceholderHelper;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 配置获取实现.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class Config {

    private static final Logger log = LoggerFactory.getLogger(Config.class);
    private static final PropertyPlaceholderHelper PLACEHOLDER_HELPER = new PropertyPlaceholderHelper("${", "}", ":", true);
    private static final Pattern DOT_REGEX = Pattern.compile("\\.");

    private String stateUrl;
    private String propsUrl;
    private String watchStateUrl;
    private String configToken;
    private String state;

    private boolean failFast;

    private Set<DuicListener> listeners;

    private Map<String, Object> properties = Collections.emptyMap();

    private Config(String baseUri, String name, String profile, String configToken, boolean watchEnabled, boolean failFast,
                   Set<DuicListener> listeners) {
        this.configToken = configToken;
        this.failFast = failFast;
        this.listeners = listeners;

        stateUrl = HttpUrl.parse(baseUri).newBuilder()
                .addPathSegments("apps/states")
                .addPathSegment(name)
                .addPathSegment(profile)
                .build()
                .toString();
        propsUrl = HttpUrl.parse(baseUri).newBuilder()
                .addPathSegments("apps")
                .addPathSegment(name)
                .addPathSegment(profile)
                .build()
                .toString();
        watchStateUrl = HttpUrl.parse(baseUri).newBuilder()
                .addPathSegments("apps/watches")
                .addPathSegment(name)
                .addPathSegment(profile)
                .build()
                .toString();

        loadProperties();
        if (watchEnabled) {
            watch();
        }
    }

    public Object get(String key) {
        if (key == null || key.isEmpty()) {
            throw new ConfigNotFoundException("config key 不能为 null");
        }
        String[] segs = DOT_REGEX.split(key);

        Object p = null;
        for (String k : segs) {
            if (p == null) {
                p = properties.get(k);
            } else {
                p = ((Map) p).get(k);
            }
        }

        if (p instanceof String) {
            return PLACEHOLDER_HELPER.replacePlaceholders((String) p, new PropertyPlaceholderHelper.PlaceholderResolver() {
                @Override
                public String resolvePlaceholder(String placeholderName) {
                    Object o = get(placeholderName);
                    if (o == null) {
                        o = System.getProperty(placeholderName);
                        if (o == null) return System.getenv(placeholderName);
                    }
                    return o != null ? o.toString() : null;
                }
            });
        }
        return p;
    }

    private void loadProperties() {
        try {
            state = getState();
            long b = System.currentTimeMillis();
            properties = DuicClientUtils.getProperties(propsUrl, configToken);
            log.info("加载 DuiC 配置 [{},{}ms]", propsUrl, System.currentTimeMillis() - b);
            for (DuicListener listener : listeners) {
                listener.handle(state, properties);
            }
        } catch (RuntimeException e) {
            if (failFast) {
                throw e;
            } else {
                log.warn("获取配置错误 [{}] {}", propsUrl, e.getMessage());
            }
        }
    }

    private void watch() {
        Thread t = new Thread("duic-watch-state") {
            @Override
            public void run() {
                for (; ; ) {
                    try {
                        String newState = DuicClientUtils.watchState(watchStateUrl, state, configToken);
                        if (!Objects.equals(state, newState)) {
                            loadProperties();
                        }
                    } catch (Exception e) {
                        log.warn("获取配置状态错误 [{}]", stateUrl, e);

                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e1) {
                        }
                    }
                }
            }
        };
        t.setDaemon(true);
        t.start();
    }

    private String getState() {
        return DuicClientUtils.getState(stateUrl, configToken);
    }

    public static class Builder {

        private String baseUri;
        private String name;
        private String profile;
        private String configToken;
        private boolean watchEnabled = false;
        private boolean failFast;
        private Set<DuicListener> listeners = new HashSet<>();

        /**
         * 基 uri.
         *
         * @param baseUri https://duic.zhudy.io/api/v1
         */
        public Builder baseUri(String baseUri) {
            this.baseUri = baseUri;
            return this;
        }

        /**
         * 配置应用名称.
         *
         * @param name 应用名称
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * 配置应用环境.
         *
         * @param profile 应用环境
         */
        public Builder profile(String profile) {
            this.profile = profile;
            return this;
        }

        /**
         * 配置访问令牌.
         *
         * @param configToken 访问令牌
         */
        public Builder configToken(String configToken) {
            this.configToken = configToken;
            return this;
        }

        /**
         * 启用监控配置变化。
         */
        public Builder enableWatch() {
            this.watchEnabled = true;
            return this;
        }

        /**
         * 启用监控配置变化。
         */
        public Builder watchEnabled(boolean watchEnabled) {
            this.watchEnabled = watchEnabled;
            return this;
        }

        /**
         * 当配置获取失败时, 直接抛出异常, 快速失败。
         */
        public Builder enableFailFast() {
            this.failFast = true;
            return this;
        }

        /**
         * 当配置获取失败时, 直接抛出异常, 快速失败。
         */
        public Builder failFast(boolean failFast) {
            this.failFast = failFast;
            return this;
        }

        /**
         * 配置监听器。
         *
         * @param listener 监听器
         */
        public Builder listener(DuicListener listener) {
            listeners.add(listener);
            return this;
        }

        /**
         * 返回配置实例。
         */
        public Config build() {
            return new Config(baseUri, name, profile, configToken, watchEnabled, failFast, listeners);
        }
    }
}