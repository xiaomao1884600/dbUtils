/**
 * @see 本功能接口文档http://record_logs.mydoc.io/
 */
angular.module('recordScheduleConsole', []).component('recordScheduleConsole', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/console/recordScheduleConsole.html',
    controller: ['httpService', '$mdDialog', '$location', 'dialogService',
        function recordScheduleConsoleController(httpService, $mdDialog, $location, dialogService) {

            var self = this;
            this.dataUrl = 'http://record.hxsd.local/api/record/logs/get_cron';
            this.tableSizeObjectName = "com.doubeye.core.dataSource.services.meta.MySQL.MetadataService";
            this.tableSizeAction = "getTableSizeInfo";
            this.purgeLogObjectName = "com.hxsd.services.productLine.dataMining.recordAnalyze.LogManagerService";
            this.purgeLogAction = "purgeLog";
            this.tables = "record_logs_sync,record_logs_upload,record_logs_analyze_task_send,record_logs_analyze_info";
            this.schema = "record_app";
            this.dataSource = "RECORD-ANALYZE-PRODUCT";
            var now = new Date();
            this.purgeDate = new Date(now.setMonth(now.getMonth() - 1));
            this.$onInit = function () {
                this.campus = [];
                this.logs = [];
                this.__columnDefines = [{
                    dataId : 'operation_title',
                    label : '类型'

                }, {
                    dataId : 'client_title',
                    label : '实例名称'
                }, {
                    dataId : 'client_ip',
                    label : 'ip'
                }, {
                   dataId : 'current_status',
                   label : '当前状态',
                   value : function (value) {
                       return value === 1 ? '正在运行' : '已经结束'
                   } 
                }, {
                    dataId : 'statusCode',
                    color : 'color',
                    label : '状态'
                }, {
                    dataId : 'lastExecuteInfo',
                    label : '最后一次运行的信息'
                }];
                this.__operations = [{
                    text : '详情',
                    id : 'detail',
                    callback : function(record) {
                        var url = 'main.html#!/scheduleRunningLog?operationType=' + record.operation_type + '&ip=' + record.client_ip + "&jump=1";
                        window.open(url, '_blank');
                    }
                }, {
                    text : '异常日志',
                    id : 'fatal',
                    callback : function(record) {
                        var url = 'main.html#!/fatalLog?startTime=' + record.start_time + '&endTime=' + record.end_time + '&campusId=' + JSON.parse(record.task_config).campus_id + '&jump=1';
                        window.open(url, '_blank');
                    }
                }, {
                    text : '运行日志',
                    id : 'runningLog',
                    callback : function(record) {
                        var url;
                        if (record.operation_type !== 3) {
                            url = 'main.html#!/recordDbEtlLog?logOperationType=' + record.operation_type + '&startTime=' + record.start_time + '&endTime=' + record.end_time + '&jump=1';
                            window.open(url, '_blank');
                        } else if (record.operation_type === 3) {
                            url = 'main.html#!/uploadLog?startTime=' + record.start_time + '&endTime=' + record.end_time + '&campusId=' + JSON.parse(record.task_config).campus_id + '&jump=1';
                            window.open(url, '_blank');
                        }
                    }
                }];
                /**
                 * @type {*[]}
                 */
                this.schedules = [];

                this.getData();
                this.getLogTableInfo();
            };

            this.getLogTableInfo = function () {
                httpService.sendRequest(self, 'generalRouter', {
                    objectName : self.tableSizeObjectName,
                    action : self.tableSizeAction,
                    schema : self.schema,
                    tables : self.tables,
                    dataSource : self.dataSource
                }, {
                    dataPropertyName : 'logTableInfo'
                }, {
                    errorMessage : '获取日志表信息出错，'
                });
            };


            this.getData = function () {
                httpService.sendRequest(self, self.dataUrl, {
                }, {
                    dataPropertyName : 'schedules',
                    resultPropertyName : 'data.DATA',
                    callback : function (data) {
                        data.forEach(function (element) {
                            if (element.run_status === 1) {
                                element.statusCode = 'green';
                                element.color = 'green';
                            } else if (element.run_status === 2) {
                                element.statusCode = 'yellow';
                                element.color = '#FFCC66';
                            } else if (element.run_status === 3) {
                                element.statusCode = 'red';
                                element.color = 'red';
                            }
                            element._hideOperation = {
                                fatal : element.operation_type !== 3
                            };
                            element.lastExecuteInfo = '运行周期：' + element.start_time + ' - ' + element.end_time +
                                ' 运行时长：' + element.run_time + '秒 共' + element.total_num + '个任务，成功' + element.success_num + '个，失败' + element.fail_num +'个'
                        });
                    }
                }, {
                    errorMessage : '获取控制台信息出错，'
                });
            };
            this.doLogPurge = function () {
                var time = self.purgeDate.format('yyyy-MM-dd');
                dialogService.confirm('提示', '是否确定要删除' + time + '之前的日志？此操作不可恢复！！', {}, function () {
                    httpService.sendRequest(self, 'generalRouter', {
                        objectName : self.purgeLogObjectName,
                        action : self.purgeLogAction,
                        tables : self.tables,
                        dataSource : self.dataSource,
                        purgeDate : time
                    }, {
                        callback : function () {
                            self.getLogTableInfo();
                        }
                    }, {
                        errorMessage : '获取日志表信息出错，'
                    });
                });

            };
        }]
});
