/*global angular, console */
(function () {

    'use strict';

    var subakApp = angular.module('subakApp', ['ui.bootstrap', 'mediaPlayer']);

    // extract the audio for making the player easier to test
    // https://github.com/angular/peepcode-tunes/blob/master/public/js/Tunes.js
    subakApp.factory('audio', function ($document) {
        var audio = $document[0].createElement('audio');
        return audio;
    });

    // https://github.com/angular/peepcode-tunes/blob/master/public/js/Tunes.js
    subakApp.factory('player', function (audio) {
        var player = {},
            playing = false;
        player = {
            play: function (url) {
                console.log('player: audio=', audio);
                audio.src = url;
                audio.play();
                playing = true;
            },
            pause: function () {
                audio.pause();
                playing = false;
            },
            next: function () {
                // TODO
            },
            previous: function () {
                // TODO
            }
        };

        console.log('player service is create.');

        return player;
    });

    subakApp.directive('panelSearchForm', function () {
        return {
            templateUrl: 'panel-search-form.html'
        };
    });

    subakApp.directive('panelMediaPlayer', function () {
        return {
            templateUrl: 'panel-media-player.html'
        };
    });

    subakApp.directive('panelTrackList', function () {
        return {
            templateUrl: 'panel-track-list.html'
        };
    });

    subakApp.controller('mainController', function ($scope, $http, player) {

        var tracks;

        $scope.isLoading = false;

        $http.get('/api/engines').success(function (res) {
            console.log('engines=', res);
            $scope.engines = res;
            $scope.selectedEngine = $scope.engines[0];
        });

        $scope.engineChanged = function () {
            console.log('engine changed:', $scope.selectedEngine);
        };

        $scope.search = function () {
            console.log('search: keyword=', $scope.keyword);
            var url = $scope.selectedEngine.path.replace(':keyword', $scope.keyword);
            console.log('url=', url);
            $scope.isLoading = true;
            $http.get(url)
                .success(function (response) {
                    tracks = $scope.tracks = response.tracks;
                    $scope.isLoading = false;
                });
        };

        $scope.player = player;

        $scope.play = function ($event, id) {
            var track;
            console.log('play: id=', id);
            tracks.forEach(function (t) {
                if (id === t.id) {
                    track = t;
                }
            });
            console.log('play: track=', track);
            console.log('mediaPlayer=', $scope.mediaPlayer);
            $scope.mediaPlayer.load({
                src: track.file,
                type: 'audio/mpeg'
            }, true);

            $event.preventDefault();
        };

        $scope.setKeyword = function (keyword) {
            $scope.keyword = keyword;
        };

    });

}()); // (function () {
