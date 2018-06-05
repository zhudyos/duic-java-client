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
package io.zhudy.duic.config.web;

import io.zhudy.duic.config.Config;
import io.zhudy.duic.config.ConfigUtils;
import io.zhudy.duic.config.DuicClientException;
import io.zhudy.duic.config.DuicListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class DuicConfigContextListener implements ServletContextListener {

    private static final String INIT_PARAMETER_KEY = "duicConfigLocation";
    private static final String DEFAULT_CONFIG_LOCATION = "classpath:duic.properties";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        Properties props = loadConfigProperties(sc);

        String baseUri = props.getProperty("duic.base.uri");
        String name = props.getProperty("duic.name");
        String profile = props.getProperty("duic.profile");
        String configToken = props.getProperty("duic.config.token");
        String watchEnabled = props.getProperty("duic.watch.enabled");
        String failFast = props.getProperty("duic.fail.fast");
        String listeners = props.getProperty("duic.listeners");

        Config.Builder builder = new Config.Builder()
                .baseUri(baseUri)
                .name(name)
                .profile(profile)
                .configToken(configToken)
                .watchEnabled("true".equalsIgnoreCase(watchEnabled))
                .failFast("true".equalsIgnoreCase(failFast));

        if (listeners != null && !listeners.isEmpty()) {
            for (String c : listeners.split(",")) {
                try {
                    DuicListener listener = (DuicListener) Class.forName(c).newInstance();
                    builder.listener(listener);
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        ConfigUtils.setDefaultConfig(builder.build());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    private Properties loadConfigProperties(ServletContext sc) {
        String location = sc.getInitParameter(INIT_PARAMETER_KEY);
        if (location == null) {
            location = DEFAULT_CONFIG_LOCATION;
        }

        InputStream in;
        if (location.startsWith("classpath:") || location.startsWith("classpath*:")) {
            in = getClass().getClassLoader().getResourceAsStream(location.substring(location.indexOf(':') + 1));
        } else {
            try {
                in = new FileInputStream(location);
            } catch (FileNotFoundException e) {
                throw new DuicClientException("未找到文件 " + location, e);
            }
        }

        Properties props = new Properties();
        try {
            props.load(in);
            return props;
        } catch (IOException e) {
            throw new DuicClientException(e);
        }
    }
}
