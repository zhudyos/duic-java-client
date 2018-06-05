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

import io.zhudy.duic.config.spring.DuicConfigBeanFactoryPostProcessor;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        processor.setWatchEnabled(true);
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
        assertEquals(aBoolean, aRefBoolean);
    }
}
