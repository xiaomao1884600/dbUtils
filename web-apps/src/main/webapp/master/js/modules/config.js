/**
 * Created by doubeye(doubeye@sina.com) on 2016/7/15.
 */
'use strict';

angular.module('dbUtilsApp').config(['$locationProvider', '$routeProvider', function config($locationProvider, $routeProvider) {
    $routeProvider.when('/errorList', {
        template : '<error-list></error-list>'
    }).when('/etlCasesList', {
        template : '<etl-cases-list></etl-cases-list>'
    }).when('/studentInfo', {
        template : '<student-info></student-info>'
    }).when('/toBespokeInfo', {
        template : '<to-bespoke-info></to-bespoke-info>'
    }).when('/privilegeInfo', {
        template : '<privilege-info></privilege-info>'
    }).when('/generalUdateSQLBuilder', {
        template : '<general-update-s-q-l-builder></general-update-s-q-l-builder>'
    }).when('/doubleEleven', {
        template : '<double-eleven></double-eleven>'
    }).when('/student', {
        template : '<new-edu-student-info></new-edu-student-info>'
    }).when('/assignLog', {
        template : '<assign-log></assign-log>'
    }).when('/vote', {
        template : '<vote></vote>'
    }).when('/meSignOut', {
        template : '<me-sign-out></me-sign-out>'
    }).when('/jobManager', {
        template : '<job-manager></job-manager>'
    }).when('/compareJson', {
        template : '<compare-json></compare-json>'
    }).when('/caseList', {
        template : '<case-list></case-list>'
    }).when('/projectEnvironments', {
        template : '<project-environments></project-environments>'
    }).when('/changeUserName', {
        template : '<change-user-name></change-user-name>'
    }).when('/login', {
        template : '<login></login>'
    }).when('/eController', {
        template : '<e-controller></e-controller>'
    }).otherwise('/toBespokeInfo', {
        template : '<to-bespoke-info></to-bespoke-info>'
    });
}]);



