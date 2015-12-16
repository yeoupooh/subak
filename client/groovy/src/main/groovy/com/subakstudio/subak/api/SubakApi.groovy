package com.subakstudio.subak.api

import groovy.util.logging.Slf4j
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseException

/**
 * Created by yeoupooh on 12/15/15.
 */

@Slf4j
class SubakApi {
    def baseUrl = 'http://localhost'
    def engines = []

    def SubakApi(baseUrl) {
        this.baseUrl = baseUrl
    }


    def getEngines() {

        def http = new HTTPBuilder(baseUrl)
        engines = http.get(path: '/api/engines')
        return engines

    }

    def chart(id) throws HttpResponseException {
        def engine = engines.find { engine -> engine.id == id && engine.type == 'chart' }
        if (engine != null) {
            log.debug("engine.id=[$engine.id] id=[$id]")

            def http = new HTTPBuilder(baseUrl)
            def trackList = http.get(path: engine.path)

            return trackList
        }

        return null
    }

    def search(id, keyword) {
        def engine = engines.find { engine -> engine.id == id && engine.type == 'search' }
        if (engine != null) {

            def http = new HTTPBuilder(baseUrl)
            def path = engine.path.replace(':keyword', keyword)
            def trackList = http.get(path: path)

            return trackList
        }

        return null
    }
}
