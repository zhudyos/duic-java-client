package io.zhudy.duic.config;

import io.zhudy.duic.config.spring.DuicConfigBeanFactoryPostProcessor;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
@ContextConfiguration(classes = SpringTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class SpringTests {

    private static String baseUri;

    @Value("${a.boolean}")
    private boolean aBoolean;
    @Value("${a.ref_boolean}")
    private boolean aRefBoolean;

    @BeforeClass
    public static void mockServer() {
        MockWebServer webServer = new MockWebServer();
        webServer.setDispatcher(new TestDispatcher());
        baseUri = "http://" + webServer.getHostName() + ":" + webServer.getPort() + "/api/v1";
    }

    @Bean
    public static DuicConfigBeanFactoryPostProcessor duicConfigBeanFactoryPostProcessor() {
        DuicConfigBeanFactoryPostProcessor processor = new DuicConfigBeanFactoryPostProcessor();
        processor.setBaseUri(baseUri);
        processor.setName("hello");
        processor.setProfile("world");
        processor.setReloadPlot(new ReloadPlot(1, TimeUnit.SECONDS));
        return processor;
    }

    @Test
    public void execute() throws InterruptedException {
        assertTrue(aBoolean);
        assertEquals(aBoolean, aRefBoolean);
        Thread.sleep(1200);
    }

    @Test
    public void execute2() {
        assertFalse(aBoolean);
        assertEquals(aBoolean, aRefBoolean);
    }
}
