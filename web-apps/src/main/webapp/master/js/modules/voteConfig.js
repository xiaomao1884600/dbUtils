/**
 * Created by zhanglu1782 on 2017/1/3.
 */
angular.module('dbUtilsApp').config(['$locationProvider', '$routeProvider', function config($locationProvider, $routeProvider) {
    $routeProvider.when('/vote', {
        template : '<vote></vote>'
    }).otherwise('/vote', {
        template : '<vote></vote>'
    });
}]);