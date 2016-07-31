/*global angular */
/*global Media */
/*global console, alert */

(function() {

  'use strict';

  var dirs = angular.module('subakApp.directives', ['ionic', 'ngCordova']);

  dirs.directive('mediaPlayButton', function() {
    var playerState;

    return {
      restrict: 'E',
      //require: '^ngModel',
      link: function(scope, element, attrs) {
        console.log(scope);
        attrs.$observe('playerState', function(value) {
          console.log('mediaplaybutton: value=', value);
          // if (value) {
          playerState = parseInt(value, 10);
          console.log('mediaplaybutton: playerState=', playerState);
          if (window.Media !== undefined) {
            console.log('mediaplaybutton: Media.MEDIA_RUNNING=', Media.MEDIA_RUNNING, playerState === Media.MEDIA_RUNNING);
          }

          // https://www.npmjs.com/package/cordova-plugin-media
          if (window.Media !== undefined && playerState === Media.MEDIA_RUNNING) {
            element.addClass('ion-pause');
            element.removeClass('ion-play');
          } else {
            element.addClass('ion-play');
            element.removeClass('ion-pause');
          }
          // }
        });

        angular.element(element).on('click', function() {
          console.log('click: playerState=', playerState);
        });
      }
    };

  });

  dirs.directive('message', function() {
    return {
      restrict: 'E',
      templateUrl: 'templates/message.html'
    };
  });

}());
