/*global angular */
/*global console */

(function() {

  'use strict';

  var tabs = angular.module('subakApp.tabs', ['ionic']);

  tabs.config(function($stateProvider, $urlRouterProvider) {

    $stateProvider
      .state('tabs', {
        url: "/tab",
        abstract: true,
        templateUrl: "templates/tabs.html"
      })
      .state('tabs.player', {
        url: "/player",
        views: {
          'tab-player': {
            templateUrl: "templates/tab-player.html",
            controller: 'MediaPlayerCtrl'
          }
        }
      })
      .state('tabs.charts', {
        url: "/charts",
        views: {
          'tab-charts': {
            templateUrl: "templates/tab-charts.html",
            controller: 'ChartsCtrl'
          }
        }
      })
      .state('tabs.settings', {
        url: "/settings",
        views: {
          'tab-settings': {
            templateUrl: "templates/tab-settings.html",
            controller: 'SettingsCtrl'
          }
        }
      })
      .state('tabs.about', {
        url: "/about",
        views: {
          'tab-about': {
            templateUrl: "templates/tab-about.html"
          }
        }
      })
      .state('tabs.search', {
        url: "/search",
        views: {
          'tab-search': {
            templateUrl: "templates/tab-search.html",
            controller: 'SearchCtrl'
          }
        }
      });

    $urlRouterProvider.otherwise("/tab/charts");

  });

}());
