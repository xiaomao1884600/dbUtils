/**
 * Created by zhanglu1782 on 2016/12/21.
 */

angular.module('runningTasks', []).component('runningTasks', {
    templateUrl : 'app/views/scheduler/runningTasks.html',
    controller : ['httpService', function runningTasksController(httpService) {
        var self = this;
        this.$onInit = function (){
            this.columnDefines = [{
                dataId : 'identifier',
                label : 'id'
            }, {
                dataId : 'name',
                label : '名称'
            }, {
                dataId : 'process',
                label : '进度'
            }, {
                dataId : 'errorCount',
                label : '错误数'
            }, {
                dataId : 'costs',
                label : '耗时'
            }, {
                dataId : 'information',
                label : '信息'
            }];
            this.runningTasks = [];
        };

        this.getData = function () {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.doubeye.core.scheduler.SchedulerService',
                action: 'getRunningTask'
            }, {
                dataPropertyName : 'runningTasks'
            }, {
                errorMessage : '获得正在运行的任务出错，'
            });
        };


        this.run = function () {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.doubeye.core.scheduler.TestService',
                action: 'runTongchengJobGetter'
            }, {
                dataPropertyName : 'runningTasks'
            }, {
                errorMessage : '获得正在运行的任务出错，'
            });
        };

        this.getData();
    }]
});