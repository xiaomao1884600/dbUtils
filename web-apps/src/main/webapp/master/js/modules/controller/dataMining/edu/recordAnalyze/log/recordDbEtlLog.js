/**
 * @see 本功能接口文档http://record_logs.mydoc.io/
 */
angular.module('recordDbEtlLog', []).component('recordDbEtlLog', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/log/recordDbEtlLog.html',
    controller: ['httpService', '$mdDialog', '$cookies', '$location',
        function recordDbEtlLogController(httpService, $mdDialog, $cookies, $location) {

            var self = this;

            this.$onInit = function () {
                this.campusUrl = 'http://record.hxsd.local/api/recordinfo/getusercampusectask';
                this.dataUrl = 'http://record.hxsd.local/api/record/logs/get_run_logs';
                this.loginName = $cookies.get("_userId");
                this.loginName = this.loginName.substr(0, this.loginName.indexOf('@'));
                if ($location.search().logOperationType) {
                    this.logOperationType = $location.search().logOperationType;
                }
                this.totalPage = 0;
                this.recordPerPage = 15;
                this.campus = [];
                if ($location.search().compusId) {
                    this.compusId = $location.search().compusId;
                }
                if ($location.search().startTime) {
                    this.startTime = $location.search().startTime;
                    this.dataGroup = 'custom';
                }
                if ($location.search().endTime) {
                    this.endTime = $location.search().endTime;
                }
                this.logs = [];
                this.__columnDefines = [{
                    dataId : 'campus_title',
                    label : '校区'

                }, {
                    dataId : 'file_name',
                    label : '录音文件名'
                }, {
                    dataId : 'logs_message',
                    label : '错误信息'
                }, {
                    dataId : 'logs_created_date',
                    label : '时间'
                }];
                this.__operations = [{
                    text : '日志详情',
                    callback : function(record) {
                        self.showText(record);
                    }
                }];
                this.resultTypes = [{
                    id : -1,
                    name : '所有'
                }, {
                    id : '1',
                    name : '成功'
                }, {
                    id : '2',
                    name : '失败'
                }];
                this.selectedResultType = this.resultTypes[0];
                this.processCampusAndTeamUsers = function (data) {
                    if (data.campusList) {
                        self.campus = data.campusList;
                    }
                    self.campus.splice(0, 0, {
                        campusid : 0,
                        name : '所有校区'
                    });
                };
                httpService.sendRequest(self, this.campusUrl, {
                    loginname : self.loginName
                }, {
                    callback : self.processCampusAndTeamUsers
                }, {
                    errorMessage : '获取用户校区出错'
                });
                this.getData(1);
            };

            this.showText = function (record) {
                self.currentRecord = record;
                self.currentRecord.displayText = JSON.stringify(record, null, '\t');
                $mdDialog.show({
                    contentElement: '#textBrowser',
                    clickOutsideToClose: true
                });
            };


            this.getData = function (start, end, page) {
                httpService.sendRequest(self, self.dataUrl, {
                    campus_id : self.selectedCampus ? self.selectedCampus.campusid : '',
                    logs_type : self.selectedResultType.id === -1 ?  '' : self.selectedResultType.id,
                    start_time : self.startTime,//日志开始时间
                    end_time : self.endTime,//日志结束间,
                    cron_type : self.logOperationType? self.logOperationType : '',
                    index : page
                }, {
                    dataPropertyName : 'logs',
                    resultPropertyName : 'data.DATA',
                    resultRecordCountPropertyName : 'data.TOTAL_RECORDS',
                    callback : function (data, customParams, totalRecords) {
                        self.totalRecords = totalRecords;
                        if (totalRecords > 0) {
                            self.currentPage = Math.ceil((start + 1) / self.recordPerPage);
                        } else {
                            self.currentPage = 0;
                        }
                        self.totalPage = Math.ceil(totalRecords / self.recordPerPage);
                    }
                }, {
                    errorMessage : '获取日志出错，'
                });
            };
        }]
});
