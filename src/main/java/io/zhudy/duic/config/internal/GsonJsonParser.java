package io.zhudy.duic.config.internal;

import com.google.gson.Gson;

import java.util.Map;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class GsonJsonParser implements JsonParser {

    private Gson gson = new Gson();

    @Override
    public Map<String, Object> parse(String content) {
        return gson.fromJson(content, Map.class);
    }
}
