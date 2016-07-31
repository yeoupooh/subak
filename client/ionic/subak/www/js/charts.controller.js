(function () {

    'use strict';

    function ChartsController($rootScope, SubakApiService) {
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

        SubakApiService.getEngines('chart', {
            success: function (engines) {
                vm.engines = engines;
            },
            fail: function (reason) {
                console.error('failed to get engiens due to ', reason);
            }
        });

        function onChartEngineChange() {
            var chart = JSON.parse(vm.selectedChart);
            console.log('onChartEngineChange: selected chart=', vm.selectedChart);
            console.log('onChartEngineChange: chart.type=', chart.type);
            $rootScope.$broadcast('chartEngineChanged', chart);
        }

        vm.onChartEngineChange = onChartEngineChange;
    }

    ChartsController.$inject = ['$rootScope', 'SubakApiService'];

    angular
        .module('starter')
        .controller('ChartsController', ChartsController);

}());
