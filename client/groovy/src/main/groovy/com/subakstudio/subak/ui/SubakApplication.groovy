package com.subakstudio.subak.ui

import com.subakstudio.subak.api.SubakApi
import groovy.json.JsonSlurper
import groovy.swing.SwingBuilder
import groovy.util.logging.Slf4j

import javax.swing.*

/**
 * Created by yeoupooh on 12/15/15.
 */
@Slf4j
class SubakApplication {
    def api
    def swing
    def tabs
    def cmb
    def cmbModel
    def engines = []
    def searchEngines = []

    def setupMenuBar() {
        swing.menuBar {
            menu(text: 'File') {
                menuItem() {
                    action(name: 'Clear All Tabs', closure: { tabs.removeAll() })
                }
                menuItem() {
                    action(name: 'Reload Engines', closure: { loadEngines() })
                }
            }
            menu(text: 'Help') {
                menuItem() {
                    action(name: 'About', closure: { log.info("about") })
                }
            }
        }
    }

    SubakApplication() {
        swing = new SwingBuilder()
        swing.edt {
            lookAndFeel 'nimbus'
        }

        cmbModel = new SelectableModel()
        cmb = swing.comboBox(
                model: cmbModel.engines,
                selectedItem: swing.bind(target: cmbModel, targetProperty: 'selectedItem'),
                selectedIndex: swing.bind(target: cmbModel, targetProperty: 'selectedIndex')
        )


        tabs = swing.tabbedPane(id: 'tabs', tabLayoutPolicy: JTabbedPane.SCROLL_TAB_LAYOUT)

        swing.frame(title: 'Subak - delicious music', size: [800, 600], location: [200, 100], defaultCloseOperation: JFrame.EXIT_ON_CLOSE) {
            setupMenuBar()

            panel {
                borderLayout()
                hbox(constraints: NORTH) {
                    widget(cmb)
                    keyword = textField(keyPressed: { event ->
                        if (event.keyCode == 10) {
                            search(keyword.text)
                        }
                    })
                    button('Search', actionPerformed: {
                        search(keyword.text)
                    })
                }
                widget(tabs)
            }
        }.setVisible(true)
    }

    def search(keyword) {
        addTrackListPanel(searchEngines[cmbModel.selectedIndex], keyword)
    }

    def loadEngines() {
        engines.clear()
        searchEngines.clear()
        cmbModel.engines.clear()
        swing.doOutside {
            def config = new JsonSlurper().parse(getClass().getResourceAsStream("/subak.config.json"))
            api = new SubakApi(config.url)

            engines = api.getEngines()
            engines.each { engine ->
                if (engine.type == 'search') {
                    cmbModel.engines.addElement(engine.name)
                    searchEngines.add(engine)
                } else {
                    addTrackListPanel(engine)
                }
            }
        }
    }

    def removeTrackListPanel(panel) {
        tabs.remove(panel)
    }

    def addTrackListPanel(engine, keyword = '') {
        def panel
        if (engine.type == 'chart') {
            panel = new TrackListPanel(swing, engine, api, { it ->
                removeTrackListPanel(it)
            })
            tabs.add(engine.name, panel)
        } else {
            panel = new TrackListPanel(swing, engine, api, { it ->
                removeTrackListPanel(it)
            })
            tabs.add("$engine.name:$keyword", panel)
            panel.loadTracksAsync(keyword)
        }
        tabs.selectedIndex = tabs.tabCount - 1
    }

    def start() {
        loadEngines()
    }
}
