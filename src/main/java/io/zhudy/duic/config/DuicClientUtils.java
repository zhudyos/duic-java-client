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

import io.zhudy.duic.config.internal.JsonParserFactory;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * DuiC 客户端工具包.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public final class DuicClientUtils {

    private DuicClientUtils() {
        throw new AssertionError("DuicClientUtils 不能创建实例");
    }

    private static OkHttpClient httpClient;

    static {
        httpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(1, 5, TimeUnit.MINUTES))
                .connectTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
    }

    /**
     * 设置 {@link OkHttpClient} 实例.
     */
    public static void setHttpClient(OkHttpClient httpClient) {
        if (httpClient == null) {
            throw new IllegalArgumentException("OkHttpClient 不能为空");
        }
        DuicClientUtils.httpClient = httpClient;
    }

    /**
     * 获取配置状态.
     *
     * @param url https://duic.zhudy.io/api/v1/apps/states/{name}/{profile}
     * @return 配置状态
     */
    public static String getState(String url) {
        return getState(url, null);
    }

    /**
     * 获取配置状态.
     *
     * @param url         https://duic.zhudy.io/api/v1/apps/states/{name}/{profile}
     * @param configToken 配置访问令牌多个采用英文逗号分隔
     * @return 配置状态
     */
    public static String getState(String url, String configToken) {
        Map<String, Object> m = get(url, configToken);
        return (String) m.get("state");
    }

    /**
     * 监控配置状态。
     *
     * @param url         https://duic.zhudy.io/api/v1/apps/watches/{name}/{profile}
     * @param configToken 配置访问令牌多个采用英文逗号分隔
     * @return 配置状态
     */
    public static String watchState(String url, String state, String configToken) {
        HttpUrl hu = HttpUrl.parse(url).newBuilder().addQueryParameter("state", state).build();
        Map<String, Object> m = get(hu, configToken);
        return (String) m.get("state");
    }

    /**
     * 获取配置.
     *
     * @param url https://duic.zhudy.io/api/v1/apps/{name}/{profile}
     * @return 配置
     */
    public static Map<String, Object> getProperties(String url) {
        return getProperties(url, null);
    }

    /**
     * 获取配置.
     *
     * @param url         https://duic.zhudy.io/api/v1/apps/{name}/{profile}
     * @param configToken 配置访问令牌多个采用英文逗号分隔
     * @return 配置
     */
    public static Map<String, Object> getProperties(String url, String configToken) {
        return get(url, configToken);
    }

    private static Map<String, Object> get(String url, String configToken) {
        return get(HttpUrl.parse(url), configToken);
    }

    private static Map<String, Object> get(HttpUrl url, String configToken) {
        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.get().url(url);
        if (configToken != null && !configToken.isEmpty()) {
            reqBuilder.header("x-config-token", configToken);
        }

        Request req = reqBuilder.build();
        Response resp = null;
        try {
            resp = httpClient.newCall(req).execute();
            if (!resp.isSuccessful()) {
                throw new DuicClientException("加载配置 [" + url + "] 失败 status=" + resp.code() + ", body="
                        + resp.body().string());
            }

            return JsonParserFactory.getJsonParser().parse(resp.body().string());
        } catch (IOException e) {
            throw new DuicClientException(e);
        } finally {
            if (resp != null) {
                resp.close();
            }
        }
    }
}