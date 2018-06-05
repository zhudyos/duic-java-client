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

/**
 * DuiC 操作异常.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class DuicClientException extends RuntimeException {

    /**
     * 带有详细描述的构造函数.
     *
     * @param message 详细描述
     */
    public DuicClientException(String message) {
        super(message);
    }

    /**
     * 带有目标异常的构造函数.
     *
     * @param cause 目标异常
     */
    public DuicClientException(Throwable cause) {
        super(cause);
    }

    /**
     * 带有详细描述及目标异常的构造函数.
     *
     * @param message 详细描述
     * @param cause   目标异常
     */
    public DuicClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
