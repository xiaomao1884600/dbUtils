/**
 * Created by doubeye(doubeye@sina.com) on 2016/7/15.
 */
'use strict';

angular.module('dbUtilsApp').config(['$locationProvider', '$routeProvider', function config($locationProvider, $routeProvider) {
    $routeProvider.when('/toBespokeInfo', {
        template : '<to-bespoke-info></to-bespoke-info>'
    }).otherwise('/toBespokeInfo', {
        template : '<to-bespoke-info></to-bespoke-info>'
    });
}]);