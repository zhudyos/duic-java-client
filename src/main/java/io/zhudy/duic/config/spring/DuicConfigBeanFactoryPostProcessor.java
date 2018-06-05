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
package io.zhudy.duic.config.spring;

import io.zhudy.duic.config.Config;
import io.zhudy.duic.config.ConfigUtils;
import io.zhudy.duic.config.DuicListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * DuiC Spring BeanFactoryPostProcessor.
 *
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class DuicConfigBeanFactoryPostProcessor implements EnvironmentAware, ApplicationContextAware, BeanFactoryPostProcessor {

    private ConfigurableEnvironment environment;
    private ConfigurableApplicationContext applicationContext;

    private String baseUri;
    private String name;
    private String profile;
    private String configToken;
    private boolean watchEnabled;
    private boolean failFast;
    private List<DuicListener> listeners;

    private String oldState;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ConfigurableApplicationContext ac = (ConfigurableApplicationContext) applicationContext;
        ReloadConfigApplicationListener listener = applicationContext.getAutowireCapableBeanFactory()
                .createBean(ReloadConfigApplicationListener.class);
        ac.addApplicationListener(listener);
        this.applicationContext = ac;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        postProcess();
    }

    private void postProcess() {
        Config config = buildConfig();
        ConfigUtils.setDefaultConfig(config);

        // 注册 Spring 属性配置
        environment.getPropertySources().addFirst(new PropertySource<String>(ConfigUtils.class.getName()) {

            @Override
            public String getProperty(String name) {
                return ConfigUtils.getString(name, null);
            }
        });
    }

    private Config buildConfig() {
        Config.Builder builder = new Config.Builder()
                .baseUri(baseUri)
                .name(name)
                .profile(profile)
                .configToken(configToken)
                .watchEnabled(watchEnabled)
                .failFast(failFast);

        if (listeners != null && !listeners.isEmpty()) {
            for (DuicListener listener : listeners) {
                builder.listener(listener);
            }
        }

        builder.listener(new DuicListener() {
            @Override
            public void handle(String state, Map<String, Object> properties) {
                if (oldState != null && !Objects.equals(oldState, state)) {
                    applicationContext.publishEvent(new ReloadConfigEvent(state));
                }
                oldState = state;
            }
        });
        return builder.build();
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getConfigToken() {
        return configToken;
    }

    public void setConfigToken(String configToken) {
        this.configToken = configToken;
    }

    public boolean isWatchEnabled() {
        return watchEnabled;
    }

    public void setWatchEnabled(boolean watchEnabled) {
        this.watchEnabled = watchEnabled;
    }

    public boolean isFailFast() {
        return failFast;
    }

    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    public List<DuicListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<DuicListener> listeners) {
        this.listeners = listeners;
    }

}
