package io.zhudy.duic.config;

import io.zhudy.duic.config.util.PropertyPlaceholderHelper;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
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

    private String stateUrl;
    private String propsUrl;
    private String configToken;
    private String state;
    private boolean failFast;
    private Set<DuicListener> listeners;

    private Map<String, Object> properties = Collections.emptyMap();

    private Config(String baseUri, String name, String profile, String configToken, ReloadPlot plot, boolean failFast,
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
        if (plot != null) {
            watch(plot);
        }
        loadProperties();
    }

    public Object get(String key) throws ConfigNotFoundException {
        Object v = getOrNull(key);
        if (v == null) {
            throw new ConfigNotFoundException(key);
        }
        return v;
    }

    public Object getOrNull(String key) {
        if (key == null || key.isEmpty()) {
            throw new ConfigNotFoundException("config key 不能为 null");
        }

        Object p = properties.get(key);
        if (p instanceof String) {
            return PLACEHOLDER_HELPER.replacePlaceholders((String) p, new PropertyPlaceholderHelper.PlaceholderResolver() {
                @Override
                public String resolvePlaceholder(String placeholderName) {
                    Object o = getOrNull(placeholderName);
                    if (o == null) {
                        o = System.getProperty(placeholderName);
                        if (o == null) return System.getenv(placeholderName);
                    }
                    return o.toString();
                }
            });
        }
        return p;
    }

    private void loadProperties() {
        try {
            state = getState();
            long b = System.currentTimeMillis();
            properties = getFlattenedMap(DuicClientUtils.getProperties(propsUrl, configToken));
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

    private void watch(ReloadPlot plot) {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "duic-reload-config");
                t.setDaemon(true);
                return t;
            }
        });

        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    String newState = getState();
                    if (!Objects.equals(state, newState)) {
                        loadProperties();
                    }
                } catch (Exception e) {
                    log.warn("获取配置状态错误 [{}] {}", state, e.getMessage());
                }
            }
        }, plot.period, plot.period, plot.unit);
    }

    protected final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        buildFlattenedMap(result, source, null);
        return result;
    }

    private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            if (path != null && !path.isEmpty()) {
                if (key.startsWith("[")) {
                    key = path + key;
                } else {
                    key = path + '.' + key;
                }
            }
            Object value = entry.getValue();
            if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof Map) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            } else if (value instanceof Collection) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) value;
                int count = 0;
                for (Object object : collection) {
                    buildFlattenedMap(result, Collections.singletonMap("[" + (count++) + "]", object), key);
                }
            } else {
                result.put(key, (value != null ? value : ""));
            }
        }
    }

    private String getState() {
        return DuicClientUtils.getState(stateUrl, configToken);
    }

    public static class Builder {

        private String baseUri;
        private String name;
        private String profile;
        private String configToken;
        private ReloadPlot reloadPlot = new ReloadPlot(30, TimeUnit.SECONDS);
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
         * 配置重载策略
         *
         * @param reloadPlot 重载策略
         */
        public Builder reloadPlot(ReloadPlot reloadPlot) {
            this.reloadPlot = reloadPlot;
            return this;
        }

        /**
         * 当配置获取失败时, 直接抛出异常, 快速失败
         *
         * @param failFast 快速失败策略
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
            return new Config(baseUri, name, profile, configToken, reloadPlot, failFast, listeners);
        }
    }
}