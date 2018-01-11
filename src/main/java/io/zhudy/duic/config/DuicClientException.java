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
     *
     * @param message
     * @param cause
     */
    public DuicClientException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 带有目标异常的构造函数.
     *
     * @param cause 目标异常
     */
    public DuicClientException(Throwable cause) {
        super(cause);
    }
}
