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
