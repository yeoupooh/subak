/*global require, console */
/*jslint nomen: true */
/*global __dirname */

(function () {

    'use strict';

    var express = require('express'),
        app = express(),
        fs = require("fs"),
        http = require('http'),
        syncHttp = require('sync-request'),
        server,
        engines,
        mimeTypes = [{
            ext: '.ico',
            mimeType: 'image/x-icon'
        }, {
            ext: '.png',
            mimeType: 'image/png'
        }];

    function log(msg) {
        console.log(msg);
    }

    function renderFile(filename, req, res) {
        var filestream,
            fullpath = __dirname + "/../web/" + filename,
            ext = filename.slice(filename.length - 4),
            mimeType;

        mimeTypes.forEach(function (item) {
            if (item.ext === ext) {
                mimeType = item.mimeType;
            }
        });

        if (mimeType !== undefined) {
            console.log('mimeType=', mimeType);
            res.setHeader('Content-Type', mimeType);
            filestream = fs.createReadStream(fullpath);
            filestream.pipe(res);
        } else {
            fs.readFile(fullpath, 'utf8', function (err, data) {
                res.end(data);
            });
        }
    }

    app.get('/', function (req, res) {
        renderFile('index.html', req, res);
    });

    app.get('/:filename', function (req, res) {
        var filename = req.params.filename;
        console.log('req file=[%s]', filename);
        renderFile(filename, req, res);
    });

    app.get('/api/engines', function (req, res) {
        console.log('req: engines');
        var result = [];
        engines.forEach(function (engine) {
            // Exclude fs
            if (engine.type !== 'fs') {
                result.push(engine);
            }
        });
        res.json(result);
    });

    function registerEngine(site) {
        site.engines.forEach(function (engine) {
            app.get(engine.path, engine.callback);
            console.info('engine[%s] is registered.', engine.name);
            console.info('engine=[%s]', JSON.stringify(engine));
            console.info('-----');
            engines.push(engine);
        });
    }

    engines = [];

    fs.readdirSync(__dirname + '/engines').forEach(function (file) {
        if (file.indexOf('.js') > -1) {
            console.log('engine file=[%s]', JSON.stringify(file));
            registerEngine(require('./engines/' + file));
        }
    });

    // WON'T IMPLEMENT
    // xiami (need to pay to download)
    // yuleting (downloading is not working even logged in)

    server = app.listen(8081, function () {
        var host = server.address().address,
            port = server.address().port;
        console.log("Subak Server is running at http://%s:%s", host, port);
    });

}()); // (function(){
