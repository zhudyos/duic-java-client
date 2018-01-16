package io.zhudy.duic.config;

import java.util.EventListener;
import java.util.Map;

/**
 * 加载配置的监听器。
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public interface DuicListener extends EventListener {

    /**
     * 事件处理。
     *
     * @param state      状态
     * @param properties 可修改的配置对象
     */
    void handle(String state, Map<String, Object> properties);

}
