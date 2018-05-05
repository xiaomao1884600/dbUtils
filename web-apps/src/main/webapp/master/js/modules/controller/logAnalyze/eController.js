angular.module('eController', []).component('eController', {
    templateUrl : 'app/views/logAnalyze/eController.html',
    controller : ['httpService', function eControllerController(httpService) {
        var self = this;
        this.columnDefines = [{
            dataId : 'controllerName',
            label : 'controllerName'
        }, {
            dataId : 'times',
            label : '调用次数'
        }, {
            dataId : 'min',
            label : '最小返回时间'
        }, {
            dataId : 'max',
            label : '最大返回时间'
        }, {
            dataId : 'mean',
            label : '平均返回时间'
        }, {
            dataId : 'stdev',
            label : '标准差'
        }];
        httpService.sendRequest(self, 'generalRouter', {objectName : 'com.doubeye.log.analyzer.EControllerService', action : 'getEControllerAnalyzeResult'}, {
            dataPropertyName : 'data'
        }, {
            errorMessage : '获取统计信息出错，'
        });
    }]
});