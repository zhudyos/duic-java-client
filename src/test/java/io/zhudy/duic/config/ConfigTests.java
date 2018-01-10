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
    public void getOrNull() {
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
