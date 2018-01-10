package io.zhudy.duic.config.internal;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.zhudy.duic.config.DuicClientException;

import java.io.IOException;
import java.util.Map;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class JacksonJsonParser implements JsonParser {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> parse(String content) {
        try {
            return objectMapper.readValue(content, Map.class);
        } catch (IOException e) {
            throw new DuicClientException(e);
        }
    }
}
