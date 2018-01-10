package io.zhudy.duic.config;

/**
 * 错误的配置属性值异常.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class WrongConfigValueException extends RuntimeException {

    private String key;
    private Object value;

    /**
     * 通过配置键, 值与目标异常构建异常.
     *
     * @param key   配置键
     * @param value 配置值
     * @param cause 目标异常
     */
    public WrongConfigValueException(String key, Object value, Throwable cause) {
        super(formatMessage(key, value), cause);
        this.key = key;
        this.value = value;
    }

    /**
     * 返回属性值错误的键.
     *
     * @return 键
     */
    public String getKey() {
        return key;
    }

    /**
     * 返回错误的属性值.
     *
     * @return 值
     */
    public Object getValue() {
        return value;
    }

    private static String formatMessage(String propertyName, Object propertyValue) {
        return String.format("错误的属性配置 [%s: %s]", propertyName, propertyValue);
    }
}
