/**
 * Created by zhanglu1782 on 2016/11/7.
 */
angular.module('generalUpdateSQLBuilder', []).component('generalUpdateSQLBuilder', {
    templateUrl : 'app/views/generalUpdateSQLBuilder.html',
    controller : ['$http', '$scope',/* '$modal', */ function generalUpdateSQLBuilderController($http, $scope) {
        var self = this;
        $scope.comboBoxUrl = "generalRouter?objectName=com.doubeye.core.dataSource.services.DataSourceGroupService&action=getAllEDataSources";
        $scope.config = {
            url : 'generalRouter',
            params : {
                objectName: 'com.doubeye.core.dataSource.services.DataSourceGroupService',
                action: 'getAllEDataSources'
            },
            textPropertyName : 'identifier',
            valuePropertyName : 'identifier',
            resultValuePropertyName : 'dataSource',
            componentName : 'dataSource'
        };
    }]
});