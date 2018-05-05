/**
 * Created by zhanglu1782 on 2017/3/10.
 */
angular.module('jobManager', []).component('jobManager', {
    templateUrl : 'app/views/jobManager.html',
    controller : ['httpService',function jobManagerController(httpService) {
        var self = this;
        self.getAllJobs = function () {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.productLine.monitor.MonitorService', action : 'getAllJobs'
            }, {
                dataPropertyName : 'jobs'
            }, {
                errorMessage : '获取任务出错'
            });
        };

        self.getAllJobs();
    }]
});