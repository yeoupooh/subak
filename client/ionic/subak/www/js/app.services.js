/*global angular */
/*global Media */
/*global console */

(function() {

  'use strict';

  var services = angular.module('subakApp.services', ['ionic', 'ngCordova', 'ngCordova.plugins', 'ngCordova.plugins.media', 'ionic.utils']);

  services.constant('MediaPlayerConstants', {
    Events: {
      STATE_CHANGED: 'STATE_CHANGED',
      MEDIA_ERROR: 'MEDIA_ERROR'
    }
  });

  services.service('MediaPlayerSvc', ['MediaPlayerConstants', function(MediaPlayerConstants) {
    var eventListeners = {},
      media,
      self = this,
      Events = MediaPlayerConstants.Events;

    // Notify event to listeners which may be controllers
    function notifyEvent(event) {
      var key;

      for (key in eventListeners) {
        if (eventListeners.hasOwnProperty(key)) {
          eventListeners[key].callback(event);
        }
      }
    }

    function changeState(newState) {
      self.state = newState;
      notifyEvent({
        id: Events.STATE_CHANGED,
        data: newState
      });
    }

    changeState(0); // Media.MEDIA_NONE

    this.resume = function() {
      if (media !== undefined) {
        media.play();
      }
    };

    this.pause = function() {
      if (media !== undefined) {
        media.pause();
      }
    };

    this.toggle = function() {
      if (this.state === Media.MEDIA_PAUSED) {
        this.resume();
      } else {
        this.pause();
      }
    };

    this.play = function(track) {
      console.log('play: track=', track);

      if (track === undefined) {
        console.error('track is undefined.');
        return;
      }

      if (media !== undefined) {
        media.stop();
        media.release();
        media = undefined;
      }

      this.track = track;

      // Not in device, media doesn't not support
      if (Media === undefined) {
        console.error('Media not supported.');
        notifyEvent({
          id: Events.MEDIA_ERROR,
          data: 'Media not supported'
        });
        return;
      }

      media = new Media(track.file, function() {
        console.log('media: success');
      }, function(error) {
        console.error('error in playing. error=', error);
        changeState(Media.MEDIA_STOPPED);
        notifyEvent({
          id: Events.MEDIA_ERROR,
          data: error
        });
      }, function(status) {
        console.log('media: status=', status);
        changeState(status);
      });

      media.play();
    };

    this.on = function(listener) {
      if (listener.name === undefined) {
        console.error('listener should have name. listener=', listener);
        return;
      }
      if (listener.callback === undefined) {
        console.error('listener should have callback. listener=', listener);
        return;
      }
      eventListeners[listener.name] = listener;
    };

    this.off = function(listener) {
      if (listener.name === undefined) {
        console.error('listener should have name. listener=', listener);
        return;
      }
      if (listener.callback === undefined) {
        console.error('listener should have callback. listener=', listener);
        return;
      }
      eventListeners[listener.name] = undefined;
    };

  }]);

  services.service('SettingsSvc', ['$localStorage', function($localStorage) {
    var listeners = {},
      settings = $localStorage.getObject('settings', {});

    function notify(key, value) {
      var k;

      for (k in listeners) {
        if (listeners.hasOwnProperty(k)) {
          console.log('listener=', k, listeners[k]);
          listeners[k].onChange(key, value);
        }
      }
    }

    this.on = function(name, listener) {
      if (listener.onChange === undefined) {
        console.error('onChange is undefined. So it is not registered.');
        return;
      }
      listeners[name] = listener;
      console.info('%s is registered.', name);
    };
    this.off = function(name) {
      listeners[name] = undefined;
      console.info('%s is unregistered.', name);
    };

    this.get = function(key, defaultValue) {
      if (settings[key] === undefined) {
        return defaultValue;
      }
      return settings[key];
    };

    this.set = function(key, value) {
      settings[key] = value;
      $localStorage.setObject('settings', settings);

      notify(key, value);
    };

  }]);

}());
