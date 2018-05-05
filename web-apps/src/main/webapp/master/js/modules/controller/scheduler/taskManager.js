/**
 * Created by zhanglu1782 on 2016/12/21.
 */

angular.module('taskManager', []).component('taskManager', {
    templateUrl : 'app/views/scheduler/taskManager.html',
    controller : ['httpService', 'ngDialog', '$scope', '$mdDialog', function taskManagerController(httpService, ngDialog, $scope, $mdDialog) {
        var self = this;

        this.currentScheduleId = null;
        this.editingSchedule = null;
        this.dataSources = [{
            name : 'test1',
            identifier : 'first'
        }, {
            name : 'test2',
            identifier : 'second'
        }];

        this.showInitConfigEditor = function (schedule) {
            self.currentScheduleId = schedule.id;
            self.editingSchedule = angular.copy(schedule);
            $mdDialog.show({
                contentElement: '#configDialog',
                //parent: angular.element(document.body),
                clickOutsideToClose:true
            });
        };


        this.run = function (schedule) {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.doubeye.core.scheduler.SchedulerService',
                action: 'run',
                scheduleId : schedule.id
            }, {
                // callback : todo 改变运行状态
            }, {
                errorMessage : '运行计划任务出错，'
            });
        };

        this.$onInit = function (){
            this.columnDefines = [{
                dataId : 'id',
                label : 'id'
            }, {
                dataId : 'name',
                label : '名称'
            }, {
                dataId : 'identifier',
                label : '标识符'
            }, {
                dataId : 'scheduleType',
                label : '任务类型'
            }, {
                dataId : 'singletonType',
                label : '单例控制类型'
            }, {
                dataId : 'information',
                label : '信息'
            }, {
                dataId : 'operations',
                label : '操作'
            }];

            this.scheduleOprations = [{
                text : '初始化配置',
                callback : this.showInitConfigEditor
            }, {
                text : '运行',
                callback : this.run
            }];

            this.schedules = [];
        };

        this.getData = function () {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.doubeye.core.scheduler.SchedulerService',
                action: 'getAllSchedules'
            }, {
                dataPropertyName : 'schedules'
            }, {
                errorMessage : '获得计划任务列表出错，'
            });
        };

        this.saveSchedule = function(schedule) {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.doubeye.core.scheduler.SchedulerService',
                action: 'saveSchedule',
                schedule : self.editingSchedule
            }, {
                callback : function (data) {
                    self.editingSchedule.id = data.id;
                    if (self.currentScheduleId == null) {
                        self.schedules.push(self.editingSchedule);
                    } else {
                        self.schedules.forEach(function (schedule) {
                            if (schedule.id === self.currentScheduleId) {
                                angular.merge(schedule, self.editingSchedule);
                            }
                        });
                    }
                    $mdDialog.hide();
                }
            }, {
                errorMessage : '获得计划任务列表出错，'
            });
        };

        this.confirmInitConfigChange  = function () {
            //todo save config and or schedule
            self.saveSchedule();
        };

        this.cancelInitConfigChange  = function () {
            $mdDialog.hide();
        };

        this.newSchedule = function () {
            var newSchedule = {
                initConfig : {}
            };
                self.showInitConfigEditor(newSchedule)
        };

        this.getData();
    }]
});