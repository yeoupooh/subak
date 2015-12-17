/*global console */
/*global require */
/*global describe, beforeEach, afterEach, it */

(function () {

    'use strict';

    var assert = require('assert'),
        request = require('supertest');

    describe('loading express', function () {
        var server;

        beforeEach(function () {
            server = require('../app/server');
        });

        afterEach(function () {
            if (server !== undefined) {
                server.close();
            }
        });

        it('responds to /', function testSlash(done) {
            request(server)
                .get('/')
                .end(function (err, res) {
                    /*jslint unparam: true */
                    assert(res.statusCode === 200);
                    done();
                });
        });

        it('responds to /api/engines', function testEngines(done) {
            request(server)
                .get('/api/engines')
                .end(function (err, res) {
                    /*jslint unparam: true */
                    // console.log('res.body=', res.body);
                    assert(res.statusCode === 200);
                    assert(res.body.length > 0);
                    done();
                });
        });

        describe('responds to all engines', function () {
            request(require('../app/server'))
                .get('/api/engines')
                .end(function (err, res) {
                    var engines = [];
                    /*jslint unparam: true */
                    // console.log('res.body=', res.body);
                    assert(res.statusCode === 200);
                    assert(res.body.length > 0);
                    engines = res.body;

                    engines.forEach(function (engine) {
                        describe('loading engine: ' + engine.name, function () {
                            it('responds to engine: ' + engine.name, function testEngines(done) {
                                var path = engine.path;

                                // console.log('engine.name=', engine.name);
                                if (path.indexOf(':keyword') > -1) {
                                    path = path.replace(/:keyword/g, '2000');
                                }

                                request(server)
                                    .get(path)
                                    .end(function (err, res) {
                                        // console.log(engine.name, 'es.body.tracks.length=', res.body.tracks.length);
                                        assert(res.statusCode === 200);
                                        assert(res.body !== undefined);
                                        // assert(res.body.length > 0);
                                        done();
                                    });
                            });
                        });
                    });
                });
        });

        it('unknown API path', function testUnknownApiPath(done) {
            request(server)
                .get('/api/404')
                .end(function (err, res) {
                    /*jslint unparam: true */
                    // console.log('res.statusCode=', res.statusCode);
                    assert(res.statusCode === 404);
                    done();
                });
        });
    });

}());
