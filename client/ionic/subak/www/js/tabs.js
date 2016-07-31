/*global angular */
/*global console */

(function () {

    'use strict';

    var tabs = angular.module('subakApp.tabs', ['ionic']);

    tabs.config(function ($stateProvider, $urlRouterProvider) {

        $stateProvider
            .state('tabs', {
                url: "/tab",
                abstract: true,
                templateUrl: "templates/tabs.html"
            })
            .state('tabs.player', {
                url: "/player",
                views: {
                    'player-tab': {
                        templateUrl: "templates/player.html",
                        controller: 'MediaPlayerCtrl'
                    }
                }
            })
            .state('tabs.charts', {
                url: "/charts",
                views: {
                    'charts-tab': {
                        templateUrl: "charts/charts.html"
                    }
                }
            })
            .state('tabs.settings', {
                url: "/settings",
                views: {
                    'settings-tab': {
                        templateUrl: "templates/settings.html",
                        controller: 'SettingsCtrl'
                    }
                }
            })
            .state('tabs.about', {
                url: "/about",
                views: {
                    'about-tab': {
                        templateUrl: "templates/about.html"
                    }
                }
            })
            .state('tabs.search', {
                url: "/search",
                views: {
                    'search-tab': {
                        templateUrl: "search/search.html"
                    }
                }
            });

        $urlRouterProvider.otherwise("/tab/charts");

    });

}());