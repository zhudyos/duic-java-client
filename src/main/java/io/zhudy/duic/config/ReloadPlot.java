package io.zhudy.duic.config;

import java.util.concurrent.TimeUnit;

/**
 * 配置重载策略.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class ReloadPlot {

    /**
     * 构建配置状态间隔周期.
     */
    final int period;
    /**
     * 周期单位.
     */
    final TimeUnit unit;

    /**
     * 构造配置重载实例.
     *
     * @param period 构建配置状态间隔周期
     * @param unit   周期单位
     */
    public ReloadPlot(int period, TimeUnit unit) {
        this.period = period;
        this.unit = unit;
    }
}
