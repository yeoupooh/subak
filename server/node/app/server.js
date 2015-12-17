/*global module, require, console */
/*jslint nomen: true */
/*global __dirname */

(function () {

    'use strict';

    var express = require('express'),
        app = express(),
        fs = require("fs"),
        // http = require('http'),
        // syncHttp = require('sync-request'),
        server,
        engines,
        mimeTypes = [{
            ext: '.ico',
            mimeType: 'image/x-icon'
        }, {
            ext: '.png',
            mimeType: 'image/png'
        }],
        PUBLIC_PATH = '../public/';

    // function log(msg) {
    //     console.log(msg);
    // }

    function renderFile(filename, req, res) {
        /*jslint unparam: true */
        var filestream,
            fullpath = __dirname + '/' + PUBLIC_PATH + filename,
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
                if (err !== null) {
                    console.error('Erorr in renderFile: err=', err);
                }
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
        // console.log('req: engines: req=' + req);
        var result = [];
        engines.forEach(function (engine) {
            // Exclude fs
            if (engine.type !== 'fs') {
                result.push(engine);
            }
        });
        res.json(result);
    });

    function loadModule(name) {
        return require(name);
    }

    function registerEngine(site) {
        site.engines.forEach(function (engine) {
            app.get(engine.path, function (req, res) {
                engine.callback(req, res, {
                    loadModule: loadModule
                });
            });
            console.info('engine[%s] is registered.', engine.name);
            console.info('engine=[%s]', JSON.stringify(engine));
            console.info('-----');
            engines.push(engine);
        });
    }

    engines = [];

    // FIXME readdirSync
    /*jslint node: true, stupid: true */
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

    // For Unit Testing
    module.exports = server;

}()); // (function(){
