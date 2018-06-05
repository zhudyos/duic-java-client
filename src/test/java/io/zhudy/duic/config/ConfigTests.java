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

import okhttp3.mockwebserver.MockWebServer;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class ConfigTests {

    private static String baseUri;

    @BeforeClass
    public static void mockServer() {
        MockWebServer webServer = new MockWebServer();
        webServer.setDispatcher(new TestDispatcher());
        baseUri = "http://" + webServer.getHostName() + ":" + webServer.getPort() + "/api/v1";
    }

    @Test
    public void get() {
        Config config = new Config.Builder()
                .baseUri(baseUri)
                .name("hello")
                .profile("world")
                .build();
        ConfigUtils.setDefaultConfig(config);

        assertTrue(ConfigUtils.containsKey("a.boolean"));

        assertTrue(ConfigUtils.getBoolean("a.boolean"));
        assertTrue(ConfigUtils.getBoolean("a.ref_boolean"));
        assertFalse(ConfigUtils.getBoolean("a.null_boolean", false));

        assertEquals(ConfigUtils.getInt("a.int"), 1);
        assertEquals(ConfigUtils.getInt("a.ref_int"), 1);
        assertEquals(ConfigUtils.getInt("a.null_int", 5), 5);

        assertEquals(ConfigUtils.getLong("a.long"), 11);
        assertEquals(ConfigUtils.getLong("a.ref_long"), 11);
        assertEquals(ConfigUtils.getLong("a.null_long", 5), 5);

        assertEquals(ConfigUtils.getFloat("a.float"), 1.1f, 1.1f);
        assertEquals(ConfigUtils.getFloat("a.ref_float"), 1.1f, 1.1f);
        assertEquals(ConfigUtils.getFloat("a.null_long", 5.5f), 5.5f, 5.5f);

        assertEquals(ConfigUtils.getDouble("a.double"), 1.11, 1.11);
        assertEquals(ConfigUtils.getDouble("a.ref_double"), 1.11, 1.11);
        assertEquals(ConfigUtils.getDouble("a.null_long", 5.5), 5.5, 5.5);

        assertEquals(ConfigUtils.getString("a.string"), "123456");
        assertEquals(ConfigUtils.getString("a.ref_string"), "ref_123456");
        assertEquals(ConfigUtils.getString("a.null_string", "null"), "null");

        assertTrue(ConfigUtils.get("a.object") instanceof Map);
        assertTrue(ConfigUtils.get("a.list") instanceof List);
    }
}
