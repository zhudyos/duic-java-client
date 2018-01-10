package io.zhudy.duic.config.internal;

import java.util.Map;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface JsonParser {

    Map<String, Object> parse(String content);

}
