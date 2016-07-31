/*global angular */
/*global console, alert */

(function () {

    'use strict';

    function TracksController($rootScope, $scope, MediaPlayerSvc, $localStorage, SettingsSvc, SubakApiService) {
        var tracksName,
            settings = SettingsSvc,
            engine;

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
            $localStorage.setObject('tracks-' + tracksName, tracks);
        }

        function clear() {
            setTracks([]);
        }

        setLoading(false);

        $scope.initTracks = function (name) {
            tracksName = name;
            setTracks($localStorage.getObject('tracks-' + tracksName, []));
            console.info('initTracks: name=', name);
        };

        $scope.setListener = function (name) {
            console.info('setListener: name=', name);
            $rootScope.$on(name, function (event, args) {
                /*jslint unparam: true */
                // console.log('on: ', name, event, args);
                engine = args;
                console.log('new engine=', engine);
                $scope.loadTracks();
            });
        };

        $scope.doRefresh = function () {
            $scope.loadTracks();
        };

        $scope.loadTracks = function () {
            console.log('load tracks');

            setLoading(true);
            clear();

            SubakApiService.getTracks(engine, {
                success: function (tracks) {
                    setTracks(tracks);
                },
                fail: function (reason) {
                    console.error('error in loading track. reason=', reason);
                    showError('Failed to load tracks due to ' + reason);
                },
                always: function () {
                    setLoading(false);
                    // Stop the ion-refresher from spinning
                    $scope.$broadcast('scroll.refreshComplete');
                }
            }, {
                keyword: $scope.keyword
            });
        };

        $scope.search = function () {
            console.log('search: keyword=', $scope.keyword);

            // subakAppService.getTracks(engine);
            $scope.loadTracks();
        };

        $scope.loadDummy = function () {
            var tracks = [],
                i;

            for (i = 0; i < 10; i = i + 1) {
                tracks.push({
                    track: 'dummy title' + i,
                    artist: 'dummy artist' + i,
                    file: 'http://localhost/test.mp3'
                });
            }
            setTracks(tracks);
        };

        $scope.download = function (item) {
            // Loads in the system browser (using inappbrowserplugin)
            // window.open(item.file, '_system');
            cordova.InAppBrowser.open(item.file, '_system');
        };

        $scope.player = MediaPlayerSvc;
    }

    TracksController.$inject = [
        '$rootScope',
        '$scope',
        'MediaPlayerSvc',
        '$localStorage',
        'SettingsSvc',
        'SubakApiService'
    ];

    angular
        .module('starter')
        .controller('TracksController', TracksController);

}());
