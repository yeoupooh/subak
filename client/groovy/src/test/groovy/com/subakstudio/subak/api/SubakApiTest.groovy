package com.subakstudio.subak.api

import ch.qos.logback.classic.Level
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Created by yeoupooh on 12/15/15.
 */
@Slf4j
class SubakApiTest {
    SubakApi api

    @Before
    void setUp() throws Exception {
        log.setLevel(Level.ALL)
        def config = new JsonSlurper().parse(getClass().getResourceAsStream("/subak.config.json"))
        api = new SubakApi(config.url)
    }

    @Test
    void testGetEngines() {
        def engines = api.getEngines()
        engines.each { engine ->
            log.debug "engine=[$engine]"
        }

        Assert.assertNotNull(engines)
    }

    @Test
    void testChart() {
        def engines = api.getEngines()
        def failed = false
        engines.each { engine ->
            log.debug("engine=[$engine]")
            if (engine.type == 'chart') {
                try {
                    def tracks = api.chart(engine.id)
                    log.debug "tracks=[$tracks]"
                    Assert.assertNotNull(tracks)
                } catch (Exception e) {
                    failed = true
                    log.error(e.getMessage())
                }
            }
        }
        if (failed) {
            Assert.fail()
        }
    }

    @Test
    void testSearch() {
        def keyword = 'AOA'
        def engines = api.getEngines()
        engines.each { engine ->
            log.debug("engine=[$engine]")
            if (engine.type == 'search') {
                try {
                    def tracks = api.search(engine.id, keyword)
                    log.debug "tracks=[$tracks]"
                    Assert.assertNotNull(tracks)
                } catch (Exception e) {
                    Assert.fail(e.getMessage())
                }
            }
        }
    }
}
