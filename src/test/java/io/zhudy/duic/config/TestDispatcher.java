package io.zhudy.duic.config;


import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class TestDispatcher extends Dispatcher {

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        String p = request.getPath();
        if (p.equals("/api/v1/apps/states/hello/world")) {
            return new MockResponse().setResponseCode(200).setBody("{ \"state\": \"0\" }");
        }
        if (p.equals("/api/v1/apps/hello/world")) {
            return new MockResponse().setResponseCode(200)
                    .setBody("{ \"a\": { \"boolean\": true, \"ref_boolean\": \"${a.boolean}\", \"int\": 1, \"ref_int\": \"${a.int}\", \"long\": 11, \"ref_long\": \"${a.long}\", \"float\": 1.1, \"ref_float\": \"${a.float}\", \"double\": 1.11, \"ref_double\": \"${a.double}\", \"string\": \"123456\", \"ref_string\": \"ref_${a.string}\", \"object\": { \"a\": \"a\", \"b\": \"b\" }, \"list\": [ \"a\", \"b\" ] } }\n");
        }
        return new MockResponse().setResponseCode(404);
    }
}
