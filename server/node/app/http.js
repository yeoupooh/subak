/*global console, module */
/*global require */
/*global XMLHttpRequest */

(function () {

    'use strict';

    function getUsingXmlHttpRequest(url) {
        var XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest,
            xhr = new XMLHttpRequest(),
            err;

        console.log('xhr: url=' + url);

        // sync call
        xhr.open('GET', url, false);
        xhr.setRequestHeader('User-Agent', 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36');
        xhr.send(null);

        if (xhr.status !== 200) {
            err = new Error('Server responded with status code ' + xhr.status + ':\n' + xhr.responseText);
            err.statusCode = xhr.status;
            console.error('Error in get url. url=', url, 'err=', err);
            throw err;
        }

        console.log(xhr.responseText);

        return xhr.responseText;
    }

    function request(url, options) {
        var syncHttp = require('sync-request'),
            resp,
            err;

        console.log('http: get url=[%s]', url);

        resp = syncHttp('GET', url, options);

        // console.log('http: response headers: ', resp.headers);

        if (resp.statusCode >= 300) {
            err = new Error('Server responded with status code ' + resp.statusCode + ':\n' + resp.body.toString());
            err.statusCode = resp.statusCode;
            err.headers = resp.headers;
            err.body = resp.body;
            console.error('Error in get url. url=', url, 'err=', err);
            throw err;
        }

        return resp;
    }

    function getUsingSyncRequest(url) {
        var resp,
            respBody;

        resp = request(url, {
            headers: {
                'User-Agent': 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36'
            },
            timeout: 10000
        });

        respBody = resp.getBody('utf-8');

        return respBody;
    }

    module.exports = {
        getUsingXmlHttpRequest: getUsingXmlHttpRequest,
        getUsingSyncRequest: getUsingSyncRequest,
        get: getUsingSyncRequest,
        request: request
    };

}());
