/*global module, console */
/*global require */
/*global Buffer */
/*global httpGet */
/*jslint nomen: true */
/*global __dirname */

(function () {

    'use strict';

    var fs = require("fs"),
        id3 = require("id3js"),
        site = {
            id: 'fs',
            name: 'FileSystem'
        },
        folders = [{
            id: 'music',
            folder: '../../../files',
            files: [],
            loaded: false
        }],
        engines = [];

    function loadFiles(folder) {
        var files,
            i,
            filename,
            artist,
            track,
            artistAndTrack;

        files = fs.readdirSync(__dirname + '/' + folder.folder);
        i = 1;
        console.log('loading files...');
        files.forEach(function (file) {
            filename = __dirname + '/' + folder.folder + '/' + file;
            if (fs.lstatSync(filename).isFile() === true && (filename.indexOf('.mp3') > -1 || filename.indexOf('ogg') > -1)) {
                id3({
                    file: filename,
                    type: id3.OPEN_LOCAL
                }, function (err, tags) {
                    if (err === undefined) {
                        artistAndTrack = file.slice(0, file.length - 4).split('-');
                        if (artistAndTrack.length === 2) {
                            artist = artistAndTrack[0];
                            track = artistAndTrack[1];
                        } else {
                            artist = artistAndTrack[0];
                            track = artistAndTrack[0];
                        }
                        folder.files.push({
                            id: i,
                            name: file,
                            artist: (tags.artist !== undefined && tags.artist !== null && tags.artist.length > 0) ? tags.artist : artist,
                            track: (tags.title !== undefined && tags.title !== null && tags.title.length > 0) ? tags.title : track
                        });
                        i = i + 1;
                        console.log('file[%s] is added.', file);
                    }
                });
            }
        });
        console.log('loading file is done.');
        folder.loaded = true;
    }

    function renderTracks(folder, req, res) {
        var tracks = [],
            result = {},
            idx;

        folders.some(function (f) {
            if (folder.id === f.id) {
                console.log('folder loaded=[%s]', f.loaded);
                if (f.loaded === false) {
                    loadFiles(f);
                }
                f.files.forEach(function (file) {
                    tracks.push({
                        id: file.id,
                        artist: file.artist,
                        track: file.track,
                        file: '/' + site.id + '/' + folder.id + '/' + file.id
                    });
                });
                return true; // break loop
            }
        });

        result = {
            found: tracks.length,
            tracks: tracks
        };

        if (res === undefined) {
            console.error('res is undefined.');
            return;
        }

        res.json(result);
    }

    function downloadTrack(folder, req, res) {
        var index = req.params.index,
            filename,
            i,
            j,
            f,
            file,
            fsstream;

        console.log('index=[%s]', index);

        if (index === 0) {
            console.log('index is 0');
            return;
        }

        for (i = 0; i < folders.length; i = i + 1) {
            f = folders[i];

            if (folder.id === f.id) {
                console.log('folder loaded=[%s]', f.loaded);
                if (f.loaded === false) {
                    loadFiles(f);
                }
                for (j = 0; j < f.files.length; j = j + 1) {
                    file = f.files[j];
                    //                    console.log('file.id=[%s],index=[%s]', file.id, index);
                    if (file.id === parseInt(index, 10)) {
                        filename = __dirname + '/' + folder.folder + '/' + file.name;
                        console.log('filename=[%s]', filename);
                        res.setHeader('Content-disposition', 'attachment; filename=' + file.artist + ' - ' + file.track + '.mp3');
                        res.setHeader('Content-Type', 'audio/x-mpeg');
                        fsstream = fs.createReadStream(filename);
                        fsstream.pipe(res);
                        break;
                    }
                }
                break;
            }
        }

    }
    // Add folders
    folders.forEach(function (folder) {
        engines.push({
            id: site.id + '-chart-' + folder.id,
            name: site.name + ' ' + folder.id,
            type: 'chart',
            path: '/api/' + site.id + '/chart/' + folder.id,
            callback: function (req, res) {
                renderTracks(folder, req, res);
            }
        }, {
            id: site.id + '-chart-' + folder.id + '-download',
            name: site.name + ' ' + folder.id,
            type: 'fs',
            path: '/' + site.id + '/' + folder.id + '/:index',
            callback: function (req, res) {
                downloadTrack(folder, req, res);
            }
        });
    });

    module.exports = {
        engines: engines
    };

}());
