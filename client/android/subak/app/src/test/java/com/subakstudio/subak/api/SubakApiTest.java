package com.subakstudio.subak.api;

import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by jinwoomin on 8/6/16.
 */

public class SubakApiTest {

    private boolean taskDone;

    @Test
    public void testGetEngineList() throws InterruptedException {
        SubakClient client = new SubakClient("http://localhost:8081");
        DummySubakClientListner listener = new DummySubakClientListner();
        client.setSubackListener(listener);
        client.getEngineList();

        waitUntilTaskDone();

        assertTrue(listener.getResult() instanceof List);
        System.out.println(listener.getResult());

    }

    private void waitUntilTaskDone() throws InterruptedException {
        while (!taskDone) {
            Thread.sleep(100);
        }
    }

    @Test
    public void testGetTrackList() throws InterruptedException {
        SubakClient client = new SubakClient("http://localhost:8081");
        DummySubakClientListner listener = new DummySubakClientListner();
        client.setSubackListener(listener);
        Engine engine = new Engine();
        engine.setType(SubakApiInterface.ENGINE_TYPE_SEARCH);
        engine.setPath("/api/bobborst/year/billboard/top/year/:keyword");
        client.getTrackList(engine, "2016");

        waitUntilTaskDone();

        assertNotNull(listener.getResult());
        assertEquals(TrackListResponse.class.getName(), listener.getResult().getClass().getName());
        System.out.println(listener.getResult());
    }


    class DummySubakClientListner implements SubakClient.ISubakListener {

        private Object result;

        @Override
        public void onRequest() {
            synchronized (this) {
                taskDone = false;
            }
        }

        @Override
        public void onSuccess(Object result) {
            this.result = result;
        }

        public Object getResult() {
            return result;
        }

        @Override
        public void onError(Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        @Override
        public void onFinally() {
            synchronized (this) {
                taskDone = true;
            }
        }
    }
}
