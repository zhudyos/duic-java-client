package io.zhudy.duic.config;

import io.zhudy.duic.config.util.PropertyPlaceholderHelper;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
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
    private static final PropertyPlaceholderHelper PLACEHOLDER_HELPER = new PropertyPlaceholderHelper("${", "}");
    private static final Pattern DOT_REGEX = Pattern.compile("\\.");

    private String stateUrl;
    private String propsUrl;
    private String configToken;
    private String state;
    private boolean failFast;

    private Map<String, Object> properties;

    private Config(String baseUri, String name, String profile, String configToken, ReloadPlot plot, boolean failFast) {
        this.configToken = configToken;
        this.failFast = failFast;

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
                    Object o = getOrNull(placeholderName);
                    if (o == null) {
                        return "";
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
            properties = DuicClientUtils.getProperties(propsUrl, configToken);
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
         * 返回配置实例.
         */
        public Config build() {
            return new Config(baseUri, name, profile, configToken, reloadPlot, failFast);
        }
    }
}