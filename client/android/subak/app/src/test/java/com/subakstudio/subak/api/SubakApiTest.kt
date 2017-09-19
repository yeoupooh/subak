package com.subakstudio.subak.api

import org.junit.Before
import org.junit.Test

import java.util.ArrayList

import rx.Subscriber

import org.junit.Assert.assertTrue

/**
 * Created by jinwoomin on 8/6/16.
 */

class SubakApiTest {

    private var taskDone: Boolean = false

    @Before
    fun setup() {
        taskDone = false
    }

    @Throws(InterruptedException::class)
    private fun waitUntilTaskDone() {
        while (!taskDone) {
            Thread.sleep(100)
        }
    }

    @Test
    @Throws(Exception::class)
    fun testGetEngineList() {
        val client = SubakClient("http://localhost:8081")
        val result = ArrayList<Engine>()
        client.engineList
                .subscribe(object : Subscriber<Engine>() {
                    override fun onCompleted() {
                        println("oncompleted")
                        taskDone = true
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        println("onerror: e=" + e)
                        taskDone = true
                    }

                    override fun onNext(engine: Engine) {
                        println("onnext: engine=" + engine)
                        result.add(engine)
                    }
                })

        waitUntilTaskDone()

        assertTrue(result.size > 0)
    }

    @Test
    @Throws(Exception::class)
    fun testTrackList() {
        val result = ArrayList<Track>()

        val client = SubakClient("http://localhost:8081")
        val engine = Engine()
        engine.type = SubakApiInterface.ENGINE_TYPE_SEARCH
        engine.path = "/api/bobborst/year/billboard/top/year/:keyword"
        client.getTrackList(engine, "2016")
                .subscribe(object : Subscriber<TrackListResponse>() {
                    override fun onCompleted() {
                        println("oncompleted")
                        taskDone = true
                    }

                    override fun onError(e: Throwable) {
                        println("onerror: e=" + e)
                        taskDone = true
                    }

                    override fun onNext(trackListResponse: TrackListResponse) {
                        println("onnext: response=" + trackListResponse)
                        result.addAll(trackListResponse.tracks)
                    }
                })

        waitUntilTaskDone()

        assertTrue(result.size > 0)
    }
}
