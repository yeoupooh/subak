package com.subakstudio.subak.api;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

import static org.junit.Assert.assertTrue;

/**
 * Created by jinwoomin on 8/6/16.
 */

public class SubakApiTest {

    private boolean taskDone;

    @Before
    public void setup() {
        taskDone = false;
    }

    private void waitUntilTaskDone() throws InterruptedException {
        while (!taskDone) {
            Thread.sleep(100);
        }
    }

    @Test
    public void testGetEngineList() throws Exception {
        SubakClient client = new SubakClient("http://localhost:8081");
        final List<Engine> result = new ArrayList<>();
        client.getEngineList()
                .subscribe(new Subscriber<Engine>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("oncompleted");
                        taskDone = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        System.out.println("onerror: e=" + e);
                        taskDone = true;
                    }

                    @Override
                    public void onNext(Engine engine) {
                        System.out.println("onnext: engine=" + engine);
                        result.add(engine);
                    }
                });

        waitUntilTaskDone();

        assertTrue(result.size() > 0);
    }

    @Test
    public void testTrackList() throws Exception {
        final List<Track> result = new ArrayList<>();

        SubakClient client = new SubakClient("http://localhost:8081");
        Engine engine = new Engine();
        engine.setType(SubakApiInterface.ENGINE_TYPE_SEARCH);
        engine.setPath("/api/bobborst/year/billboard/top/year/:keyword");
        client.getTrackList(engine, "2016")
                .subscribe(new Subscriber<TrackListResponse>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("oncompleted");
                        taskDone = true;
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("onerror: e=" + e);
                        taskDone = true;
                    }

                    @Override
                    public void onNext(TrackListResponse trackListResponse) {
                        System.out.println("onnext: response=" + trackListResponse);
                        result.addAll(trackListResponse.getTracks());
                    }
                });

        waitUntilTaskDone();

        assertTrue(result.size() > 0);
    }
}
