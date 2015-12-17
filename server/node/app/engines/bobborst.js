/*global module, console */
/*global require */
/*global Buffer */
/*global httpGet */

(function () {

    'use strict';

    var charts = [],
        engines = [];

    charts = [
        {
            id: ['billboard', 'top', 'year'],
            name: 'Billboard Top Songs'
        }
    ];


    function parseTracks(resBody) {
        var cheerio = require('cheerio'),
            $,
            tracks = [],
            searchElem;

        $ = cheerio.load(resBody);
        searchElem = $('.sortable tbody tr');
        searchElem.each(function (i, elem) {
            var albumArt,
                file,
                artist,
                title,
                rank;

            // console.log('element=[%s]', elem);

            rank = $(this).find('td').eq(0).text();
            title = $(this).find('td').eq(2).text();
            artist = $(this).find('td').eq(1).text();

            if (artist !== undefined) {
                artist = artist.trim();
            }
            if (title !== undefined) {
                title = title.trim();
            }

            // console.log('albumart=', albumArt);
            // console.log('artist=', artist);
            // console.log('title=', title);
            // console.log('file=', file);
            // console.log('--------------');

            if (title !== undefined && title.length > 0) {
                tracks.push({
                    id: i,
                    artist: artist,
                    track: title,
                    file: file,
                    albumart: albumArt
                });
            } else {
                console.error('track is not added.');
            }
        });

        return tracks;

    } /* parseTracks*/

    function request(res, url) {
        var http = require('../http'),
            resBody,
            result = {},
            tracks;

        resBody = http.get(url);
        tracks = parseTracks(resBody);

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

    // keyword -> (base64 encoding) -> (to hex string)
    function toBase64HexStr(text) {
        return new Buffer(new Buffer(text).toString('base64')).toString('hex');
    }

    function renderChart(chart, req, res) {
        var keyword = req.params.keyword;

        request(res, 'http://www.bobborst.com/popculture/top-100-songs-of-the-year/?year=' + keyword);
    }

    // Add charts
    charts.forEach(function (chart) {
        var id = chart.id.join('-'),
            path = chart.id.join('/');

        engines.push({
            id: 'bobborst-chart-' + id,
            name: 'Bobborst ' + chart.name,
            type: 'search',
            path: '/api/bobborst/year/' + path + '/:keyword',
            callback: function (req, res) {
                renderChart(chart, req, res);
            }
        });
    });

    module.exports = {
        engines: engines
    };

}());
