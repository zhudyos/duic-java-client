package io.zhudy.duic.config.spring;

import org.springframework.context.ApplicationEvent;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class ReloadConfigEvent extends ApplicationEvent {

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public ReloadConfigEvent(Object source) {
        super(source);
    }
}
