package com.subakstudio.subak

import com.subakstudio.subak.ui.SubakApplication
import groovy.util.logging.Slf4j

import ch.qos.logback.classic.Level

@Slf4j
class LauncherHelper {
    def start() {
        log.setLevel(Level.ALL)
        new SubakApplication().start()
    }
}

new LauncherHelper().start()
