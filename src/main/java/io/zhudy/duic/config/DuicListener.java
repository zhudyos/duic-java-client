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
