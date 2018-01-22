package io.zhudy.duic.config.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.*;
import org.springframework.context.event.ContextClosedEvent;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * ApplicationListener。
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class ReloadConfigApplicationListener implements ApplicationContextAware, ApplicationListener<ApplicationEvent> {

    private static final Logger log = LoggerFactory.getLogger(ReloadConfigApplicationListener.class);
    private static final Set<ConfigurableApplicationContext> applicationContexts = new LinkedHashSet<>(2);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            boolean r = applicationContexts.add((ConfigurableApplicationContext) applicationContext);
            log.info("add application context [{}, {}]", applicationContext, r);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextClosedEvent) {
            ContextClosedEvent e = (ContextClosedEvent) event;
            boolean r = applicationContexts.remove(e.getApplicationContext());
            log.info("remove application context [{}, {}]", e.getApplicationContext(), r);
        } else if (event instanceof ReloadConfigEvent) {
            for (ConfigurableApplicationContext ac : applicationContexts) {
                ConfigurableListableBeanFactory beanFactory = ac.getBeanFactory();
                AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
                bpp.setAutowiredAnnotationType(Value.class);
                bpp.setBeanFactory(beanFactory);
                for (String name : beanFactory.getBeanDefinitionNames()) {
                    bpp.processInjection(beanFactory.getBean(name));
                }

                log.info("reload @Value annotation config [{}]", ac);
            }
        }
    }
}
