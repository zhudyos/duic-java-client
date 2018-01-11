package io.zhudy.duic.config.web;

import io.zhudy.duic.config.Config;
import io.zhudy.duic.config.ConfigUtils;
import io.zhudy.duic.config.DuicClientException;
import io.zhudy.duic.config.ReloadPlot;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
        String period = props.getProperty("duic.reload.period");
        String unit = props.getProperty("duic.reload.unit");
        String failFast = props.getProperty("duic.fail.fast");

        Config.Builder builder = new Config.Builder()
                .baseUri(baseUri)
                .name(name)
                .profile(profile)
                .configToken(configToken);
        if (period != null && !period.isEmpty()) {
            TimeUnit tu = TimeUnit.SECONDS;
            if (unit != null && !unit.isEmpty()) {
                tu = TimeUnit.valueOf(unit);
            }
            builder.reloadPlot(new ReloadPlot(Integer.parseInt(period), tu));
        }
        if (failFast != null && !failFast.isEmpty()) {
            builder.failFast(Boolean.parseBoolean(failFast));
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
