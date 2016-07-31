(function() {

  'use strict';

  function SubakApiService(SubakConfig, $http, SettingsSvc) {
    var svc = this,
      settings = SettingsSvc,
      engines = [];

    function filterEngine(type) {
      var filtered = [];

      engines.forEach(function(engine) {
        if (engine.type === type) {
          filtered.push(engine);
        }
      });

      return filtered;
    }

    function getEngines(type, callbacks) {
      console.log('SubakApiService: get engines=', engines);

      if (engines.length === 0) {

        $http.get(settings.get('serverUrl', SubakConfig.serverBaseUrl) + '/api/engines').then(
          // success
          function(response) {
            engines = response.data;

            callbacks.success(filterEngine(type));
          },

          // error
          function(reason) {
            callbacks.fail(reason);
          }
        );
      } else {
        console.log('getEngines: use cachced');
        callbacks.success(filterEngine(type));
      }
    }

    function getTracks(engine, callbacks, params) {
      var url = settings.get('serverUrl', SubakConfig.serverBaseUrl) + engine.path;

      console.log('getTracks: SubakConfig=', SubakConfig);

      if (settings.get('serverUrl', SubakConfig.serverBaseUrl) === undefined) {
        throw 'getTracks: serverBaseUrl is undefined.';
      }

      if (engine === undefined) {
        throw 'getTracks: engine is undefined.';
      }

      if (engine.type === 'search') {
        console.log('getTracks: params=', params);
      }

      if (params !== undefined && params.keyword !== undefined) {
        url = url.replace(':keyword', params.keyword);
      }

      $http.get(url).success(function(data) {
        callbacks.success(data.tracks);
      }).error(function(data, status) {
        callbacks.fail('data=[%s], status=[%s]', data, status);
      })['finally'](function() { // to avoid jslint for reserved word
        callbacks.always();
      });
    }

    svc.getEngines = getEngines;
    svc.getTracks = getTracks;

    console.log('SubakApiService is created.');
  }

  SubakApiService.$inject = [
    'SubakConfig',
    '$http',
    'SettingsSvc'
  ];

  angular
    .module('starter')
    .service('SubakApiService', SubakApiService);

}());
