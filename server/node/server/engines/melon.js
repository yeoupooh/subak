/*global module, console */
/*global require */
/*global Buffer */
/*global httpGet */

(function () {

    'use strict';

    var site = {
            id: 'melon',
            name: 'Melon'
        },
        charts = [],
        searches = [],
        engines = [];

    searches = [
        {
            id: 'song',
            name: 'Track'
        },
        {
            id: 'artist',
            name: 'Artist'
        }
    ];

    charts = [
        {
            id: ['top', 'weekly'],
            name: 'Weekly Top',
            url: 'http://www.melon.com/chart/week/index.htm'
        },
        {
            id: ['top', 'monthly'],
            name: 'Monthly Top',
            url: 'http://www.melon.com/chart/month/index.htm'
        }
    ];

    function parseChartTracks(resBody) {
        var cheerio = require('cheerio'),
            $,
            tracks = [],
            searchElem;

        $ = cheerio.load(resBody);
        searchElem = $('.d_song_list tr.lst50');
        searchElem.each(function (i, elem) {
            var albumArt,
                file,
                artist,
                title;

            console.log('--------------');
            console.log('element=[%s]', elem);

            albumArt = $(this).find('.image_type15 img').attr('src');
            title = $(this).find('.rank01 a').text();
            artist = $(this).find('.rank02 span a').text();
            file = $(this).find('.icon_2_on a').attr('href');

            if (artist !== undefined) {
                artist = artist.trim();
            }
            if (title !== undefined) {
                title = title.trim();
            }

            console.log('albumart=', albumArt);
            console.log('artist=', artist);
            console.log('title=', title);
            console.log('file=', file);

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
    }

    function parseSearchTracks(resBody) {
        var cheerio = require('cheerio'),
            $,
            tracks = [],
            searchElem;

        $ = cheerio.load(resBody);
        searchElem = $('.d_song_list tr');
        searchElem.each(function (i, elem) {
            var albumArt,
                file,
                artist,
                title;

            console.log('--------------');
            console.log('element=[%s]', elem);

            title = $(this).find('.t_left .ellipsis a.fc_gray').text();
            artist = $(this).find('.t_left .checkEllipsisSongdefaultList').text();

            if (artist !== undefined) {
                artist = artist.trim();
            }
            if (title !== undefined) {
                title = title.trim();
            }

            console.log('albumart=', albumArt);
            console.log('artist=', artist);
            console.log('title=', title);
            console.log('file=', file);

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
    }

    function request(res, url, parseTracks) {
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

    function renderSearch(search, req, res, parseTracks) {
        var keyword = req.params.keyword;

        request(res, 'http://www.melon.com/search/song/index.htm?q=' + encodeURIComponent(keyword) + '&section=' + search.id, parseTracks);
    }

    function renderChart(chart, req, res, parseTracks) {
        console.log('req.url=[%s]', req.url);
        request(res, chart.url, parseTracks);
    }

    // Add search engines
    searches.forEach(function (search) {
        engines.push({
            id: site.id + '-search-' + search.id,
            name: site.name + ' Search by ' + search.name,
            type: 'search',
            path: '/api/' + site.id + '/search/' + search.id + '/:keyword',
            callback: function (req, res) {
                renderSearch(search, req, res, parseSearchTracks);
            }
        });
    });

    // Add charts
    charts.forEach(function (chart) {
        var id = chart.id.join('-'),
            path = chart.id.join('/');

        engines.push({
            id: site.id + '-chart-' + id,
            name: site.name + ' ' + chart.name,
            type: 'chart',
            path: '/api/' + site.id + '/chart/' + path,
            callback: function (req, res) {
                renderChart(chart, req, res, parseChartTracks);
            }
        });
    });

    module.exports = {
        engines: engines
    };

}());