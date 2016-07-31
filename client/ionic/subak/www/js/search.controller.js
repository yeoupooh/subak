(function () {

    'use strict';

    function SearchController($rootScope, SubakApiService) {
        var vm = this;

        vm.chartList = [{
            "id": "bobborst-chart-billboard-top-year",
            "name": "Bobborst Billboard Top Songs",
            "type": "chart",
            "path": "/api/bobborst/chart/billboard/top/year/:keyword"
        }, {
            "id": "melon-search-song",
            "name": "Melon Search by Track",
            "type": "search",
            "path": "/api/melon/search/song/:keyword"
        }];
        vm.selectedChart = '';

        SubakApiService.getEngines('search', {
            success: function (engines) {
                console.log('SearchController: engines=', engines);
                vm.engines = engines;
            },
            fail: function (reason) {
                console.error('failed to get engiens due to ', reason);
            }
        });

        function onSearchEngineChange() {
            var chart = JSON.parse(vm.selectedChart);
            console.log('onSearchEngineChange: selected engine=', vm.selectedChart);
            console.log('onSearchEngineChange: chart.type=', chart.type);
            $rootScope.$broadcast('searchEngineChanged', chart);
        }

        vm.onSearchEngineChange = onSearchEngineChange;
    }

    SearchController.$inject = ['$rootScope', 'SubakApiService'];

    angular
        .module('starter')
        .controller('SearchController', SearchController);

}());
