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
package io.zhudy.duic.config.internal;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
public final class JsonParserFactory {

    private static JsonParser jsonParser;

    static {
        try {
            Class.forName("com.fasterxml.jackson.databind.ObjectMapper");
            jsonParser = new JacksonJsonParser();
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.google.gson.Gson");
                jsonParser = new GsonJsonParser();
            } catch (ClassNotFoundException e1) {
            }
        }

        if (jsonParser == null) {
            throw new IllegalStateException("缺少 JSON 解析依赖 jackson 或者 gson");
        }
    }

    public static JsonParser getJsonParser() {
        return jsonParser;
    }
}
