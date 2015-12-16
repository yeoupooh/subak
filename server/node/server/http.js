/*global console, module */
/*global require */

(function () {

    'use strict';

    var syncHttp = require('sync-request');

    function get(url) {
        var resp, respBody, err;

        console.log('http: get url=[%s]', url);

        resp = syncHttp('GET', url, {
            headers: {
                'user-agent': 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36'
            },
            timeout: 10000
        });

        if (resp.statusCode >= 300) {
            err = new Error('Server responded with status code ' + resp.statusCode + ':\n' + resp.body.toString());
            err.statusCode = resp.statusCode;
            err.headers = resp.headers;
            err.body = resp.body;
            console.error('Error in get url. url=', url, 'err=', err);
            throw err;
        }

        respBody = resp.getBody('utf-8');

        return respBody;
    }

    module.exports = {
        get: get
    };

}());