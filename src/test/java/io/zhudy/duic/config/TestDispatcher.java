package io.zhudy.duic.config;


import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Kevin Zou (kevinz@weghst.com)
 */
public class TestDispatcher extends Dispatcher {

    private ConcurrentHashMap<String, AtomicInteger> seqs = new ConcurrentHashMap<>();

    @Override
    public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
        String p = request.getPath();
        AtomicInteger seq = seqs.get(p);
        if (seq == null) {
            seq = new AtomicInteger();
            seqs.put(p, seq);
        }
        seq.incrementAndGet();

        if (p.equals("/api/v1/apps/states/hello/world")) {
            if (seq.intValue() >= 2) {
                return new MockResponse().setResponseCode(200).setBody("{ \"state\": \"2\" }");
            }
            return new MockResponse().setResponseCode(200).setBody("{ \"state\": \"" + seq.intValue() + "\" }");
        }

        if (p.equals("/api/v1/apps/watches/hello/world")) {
            return new MockResponse().setResponseCode(200).setBody("{ \"state\": \"2\" }");
        }

        if (p.equals("/api/v1/apps/hello/world")) {
            if (seq.intValue() >= 2) {
                return new MockResponse().setResponseCode(200)
                        .setBody("{ \"a\": { \"boolean\": false, \"ref_boolean\": \"${a.boolean}\", \"int\": 1, \"ref_int\": \"${a.int}\", \"long\": 11, \"ref_long\": \"${a.long}\", \"float\": 1.1, \"ref_float\": \"${a.float}\", \"double\": 1.11, \"ref_double\": \"${a.double}\", \"string\": \"123456\", \"ref_string\": \"ref_${a.string}\", \"object\": { \"a\": \"a\", \"b\": \"b\" }, \"list\": [ \"a\", \"b\" ] } }\n");
            }
            return new MockResponse().setResponseCode(200)
                    .setBody("{ \"a\": { \"boolean\": true, \"ref_boolean\": \"${a.boolean}\", \"int\": 1, \"ref_int\": \"${a.int}\", \"long\": 11, \"ref_long\": \"${a.long}\", \"float\": 1.1, \"ref_float\": \"${a.float}\", \"double\": 1.11, \"ref_double\": \"${a.double}\", \"string\": \"123456\", \"ref_string\": \"ref_${a.string}\", \"object\": { \"a\": \"a\", \"b\": \"b\" }, \"list\": [ \"a\", \"b\" ] } }\n");
        }
        return new MockResponse().setResponseCode(404);
    }
}
