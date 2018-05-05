/**
 * @see 本功能接口文档http://record_logs.mydoc.io/
 */
angular.module('scheduleRunningLog', []).component('scheduleRunningLog', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/console/scheduleRunningLog.html',
    controller: ['httpService', '$mdDialog', '$location',
        function scheduleRunningLogController(httpService, $mdDialog, $location) {

            var self = this;
            this.dataUrl = 'http://record.hxsd.local/api/record/logs/get_cron_logs';
            this.$onInit = function () {
                this.campus = [];
                this.logs = [];
                this.totalPage = 0;
                this.recordPerPage = 15;
                if ($location.search().operationType) {
                    this.operationType = $location.search().operationType;
                }
                if ($location.search().ip) {
                    this.ip = $location.search().ip;
                }



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
                    dataId : 'start_time',
                    label : '开始时间'
                }, {
                    dataId : 'end_time',
                    label : '结束时间'
                }, {
                    dataId : 'run_time',
                    label : '时长'
                }, {
                    dataId : 'total_num',
                    label : '任务总数'
                }, {
                    dataId : 'success_num',
                    label : '成功'
                }, {
                    dataId : 'fail_num',
                    label : '失败'
                }];
                /**
                 * @type {*[]}
                 */
                this.schedules = [];

                this.getData(1);
            };

            this.showText = function (record) {
                self.currentRecord = record;
                $mdDialog.show({
                    contentElement: '#textBrowser',
                    clickOutsideToClose: true
                });
            };


            this.getData = function (start, end, page) {
                httpService.sendRequest(self, self.dataUrl, {
                    operation_type : self.operationType,
                    client_ip : self.ip,
                    index : page,
                    start_time : self.startTime,
                    end_time : self.endTime,
                    size : 15
                }, {
                    dataPropertyName : 'logs',
                    resultPropertyName : 'data.DATA',
                    resultRecordCountPropertyName : 'data.TOTAL_RECORDS',
                    callback : function (data, customParams, totalRecords) {
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
                            element.lastExecuteInfo = '运行周期：' + element.start_time + ' - ' + element.end_time +
                                ' 运行时长：' + element.run_time + '秒 共' + element.total_num + '个任务，成功' + element.success_num + '个，失败' + element.fail_num +'个';
                        });
                        if (totalRecords > 0) {
                            self.currentPage = Math.ceil((start + 1) / self.recordPerPage);
                        } else {
                            self.currentPage = 0;
                        }
                        self.totalPage = Math.ceil(totalRecords / self.recordPerPage);
                    }
                }, {
                    errorMessage : '获取控制台信息出错，'
                });
            };
        }]
});
