package com.subakstudio.subak.ui

import com.subakstudio.subak.xspf.XspfFile
import groovy.util.logging.Slf4j
import groovyx.net.http.HttpResponseException

import javax.swing.*
import javax.swing.event.ListSelectionListener
import javax.swing.filechooser.FileFilter

/**
 * Created by yeoupooh on 12/15/15.
 */
@Slf4j
class TrackListPanel extends JPanel {
    def swing
    def engine
    def api
    def closeCallback
    def trackList = []
    def tableModelForTrackList
    def msg = new MsgModel()
    def popupMenuOnTable

    TrackListPanel(swing, engine, api, closeCallback) {
        this.swing = swing
        this.engine = engine
        this.api = api
        this.closeCallback = closeCallback

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))

        popupMenuOnTable = swing.popupMenu() {
            menuItem(text: 'Play', actionPerformed: {
                def selected = trackList[trackListTable.selectionModel.minSelectionIndex]
                log.debug(selected.toString())
            })
        }

        log.debug(popupMenuOnTable.toString())

        swing.table(id: 'trackListTable', componentPopupMenu: popupMenuOnTable, selectionMode: ListSelectionModel.SINGLE_SELECTION) {
            trackListTable.selectionModel.addListSelectionListener({ evt ->
                log.debug("selection changed" + trackListTable.selectionModel)
                DefaultListSelectionModel model = trackListTable.selectionModel
                log.debug(model.maxSelectionIndex + "," + model.minSelectionIndex + "," + model.leadSelectionIndex)
                log.debug(trackList[model.leadSelectionIndex].toString())
            } as ListSelectionListener)

            tableModelForTrackList = tableModel(list: trackList) {
                closureColumn(header: 'File', read: { row -> return row.file })
                closureColumn(header: 'Track', read: { row -> return row.track })
                closureColumn(header: 'Artist', read: { row -> return row.artist })
                closureColumn(header: 'Size', read: { row -> return row.size })
                closureColumn(header: 'Length', read: { row -> return row.length })
                closureColumn(header: 'Bit Rate', read: { row -> return row.bitrate })
            }
        }

        add((JComponent) swing.vbox {
            hbox {
                hbox {
                    button('Close', actionPerformed: {
                        println(tabs)
                        println(tabs.selectedIndex)
                        closeCallback(this)
                    })
                    button('Refresh', actionPerformed: {
                        println('refresh')
                        loadTracksAsync(keyword)
                    })
                    button('Save to .xspf', actionPerformed: {
                        saveToXspf()
                    })
                    label(text: swing.bind(source: msg, sourceProperty: 'text'))
                }
            }
            scrollPane {
                widget(trackListTable)
            }
        })
    }

    void saveToXspf() {
        def dialog = swing.fileChooser(
                dialogTitle: 'Save as .xspf file',
                fileSelectionMode: JFileChooser.FILES_ONLY,
                fileFilter: [
                        getDescription: { -> "xspf files" },
                        accept        : {
                            file -> file ==~ /.*?\.xspf/ || file.isDirectory()
                        }
                ] as FileFilter
        )
        if (dialog.showOpenDialog() == JFileChooser.APPROVE_OPTION) {
            File selected = dialog.selectedFile
            if (!selected.name.endsWith(".xspf")) {
                selected = new File(selected.absolutePath + ".xspf")
            }
            println(selected)
            XspfFile.saveTo(trackList, selected)
            return
        }
    }

    def loadTracksAsync(keyword) {
        msg.text = 'Loading...'
        trackList.clear()
        swing.doOutside {
            def result
            try {
                if (engine.type == 'chart') {
                    result = api.chart(engine.id)
                } else {
                    result = api.search(engine.id, keyword)
                }
                log.debug("result=[$result]")

                swing.doLater {
                    result.tracks.each { track ->
                        println(track)
                        trackList.add(['file'  : track.file, 'track': track.track,
                                       'artist': track.artist, 'size': track.size,
                                       'length': track.length, 'bitrate': track.bitrate])
                    }
                    tableModelForTrackList.fireTableDataChanged()
                    msg.text = 'Done.'
                }

            } catch (HttpResponseException e) {
                log.error("Error in loading $engine.name with $keyword. msg=[$e.message]")
            }
        }
    }
}
