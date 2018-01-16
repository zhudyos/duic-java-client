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
     * @param state      当前配置状态
     * @param properties 当前配置，可修改的 {@code Map} 实例
     */
    void handle(String state, Map<String, Object> properties);

}
