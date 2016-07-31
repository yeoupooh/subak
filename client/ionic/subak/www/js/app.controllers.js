/*global angular */
/*global console, alert */

(function () {

    'use strict';

    var ctrls = angular.module('subakApp.controllers', ['ionic', 'subakApp.services']);

    ctrls.controller('TrackListCtrl', ['$scope', '$http', 'subakConfig', 'MediaPlayerSvc', '$localStorage', 'SettingsSvc', function ($scope, $http, subakConfig, MediaPlayerSvc, $localStorage, SettingsSvc) {
        var chartName,
            apiUrl = subakConfig.serverBaseUrl + '/api/sorinara/chart/top/weekly',
            settings = SettingsSvc;

        $scope.settings = settings;

        settings.on('chart-settings', {
            onChange: function (item, value) {
                console.log('TrackListCtrl: onchange: ', item, value);
            }
        });

        function setLoading(loading) {
            $scope.isLoading = loading;
        }

        function showError(msg) {
            $scope.message = msg;
        }

        function setTracks(tracks) {
            $scope.tracks = tracks;
            $localStorage.setObject('tracks-' + chartName, tracks);
        }

        function clear() {
            setTracks([]);
        }

        setLoading(false);

        $scope.initTracks = function (name) {
            chartName = name;
            setTracks($localStorage.getObject('tracks-' + chartName, []));
        };

        $scope.doRefresh = function () {
            $scope.loadTracks();
        };

        $scope.loadTracks = function () {
            console.log('load tracks: url=', apiUrl);

            setLoading(true);
            clear();

            $http.get(apiUrl).success(function (data) {
                console.log('data=', data);
                setTracks(data.tracks);
            }).error(function (data, status) {
                console.error('error in loading track. err=', data, 'status=', status);
                showError('Failed to load tracks. msg=[' + status + ']');
            })['finally'](function () { // to avoid jslint for reserved word
                setLoading(false);
                // Stop the ion-refresher from spinning
                $scope.$broadcast('scroll.refreshComplete');
            });
        };

        $scope.search = function () {
            console.log('search: keyword=', $scope.keyword);
            apiUrl = subakConfig.serverBaseUrl + '/api/mp3fox/search/' + $scope.keyword;
            $scope.loadTracks();
        };

        $scope.loadDummy = function () {
            var tracks = [],
                i;

            for (i = 0; i < 10; i = i + 1) {
                tracks.push({
                    track: 'dummy title' + i,
                    artist: 'dummy artist' + i,
                    file: 'http://pleer.com/browser-extension/files/4614316tvrN.mp3'
                });
            }
            setTracks(tracks);
        };

        $scope.download = function (item) {
            // Loads in the system browser (using inappbrowserplugin)
            window.open(item.file, '_system');
        };

        $scope.player = MediaPlayerSvc;
    }]);

    ctrls.controller('MediaPlayerCtrl', ['$scope', 'MediaPlayerSvc', function ($scope, MediaPlayerSvc) {

        $scope.test = function () {
            console.log('test');
        };

        $scope.player = MediaPlayerSvc;

        $scope.player.on({
            name: 'player',
            callback: function (event) {
                console.log('MediaPlayerCtrl: event=', event);
                console.log('MediaPlayerCtrl: $scope.player.state=', $scope.player.state);
                if (event.id === 'STATE_CHANGED') {
                    $scope.state = event.data;
                    $scope.$apply();
                }
            }
        });

    }]);

    ctrls.controller('SettingsCtrl', ['$scope', 'SettingsSvc', function ($scope, SettingsSvc) {
        var settings = SettingsSvc;

        $scope.load = function () {
            console.log('load settings here');
            $scope.isDebug = settings.get('isDebug');
            $scope.isDeveloper = settings.get('isDeveloper');
        };

        $scope.onChange = function (item, value) {
            settings.set(item, value);
        };
    }]);

}());