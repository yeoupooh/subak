/*global angular */
/*global console, alert */

(function () {

    'use strict';

    function TracksDirective() {
        return {
            restrict: 'E',
            templateUrl: 'templates/tracks.html'
        };
    }

    angular
        .module('starter')
        .directive('trackList', TracksDirective);

}());
