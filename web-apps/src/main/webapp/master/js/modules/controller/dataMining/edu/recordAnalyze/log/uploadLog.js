/**
 * @see 本功能接口文档http://record_logs.mydoc.io/
 */
angular.module('uploadLog', []).component('uploadLog', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/log/uploadLog.html',
    controller: ['httpService', '$mdDialog', '$cookies', '$location',
        function uploadLogController(httpService, $mdDialog, $cookies, $location) {

            var self = this;

            this.$onInit = function () {
                this.campusUrl = 'http://record.hxsd.local/api/recordinfo/getusercampusectask';
                this.dataUrl = 'http://record.hxsd.local/api/record/logs/get_upload';
                this.loginName = $cookies.get("_userId");
                this.loginName = this.loginName.substr(0, this.loginName.indexOf('@'));
                this.totalPage = 0;
                this.recordPerPage = 15;
                this.campus = [];
                this.logs = [];
                this.logTypes = [{
                    id : -1,
                    name : '所有'
                }, {
                    id : 0,
                    name : '成功'
                }, {
                    id : 1,
                    name : '失败，已自动重试'
                }, {
                    id : 2,
                    name : '失败，需要干预'
                }, {
                    id : 3,
                    name : '上传成功，但回调失败'
                }];
                this.selectedLogType = this.logTypes[0];
                this.__columnDefines = [{
                    dataId : 'campus_title',
                    label : '校区'
                }, {
                    dataId : 'file_name',
                    label : '录音文件名'
                }, {
                    dataId : 'logs_type',
                    value : function (value) {
                        var type = com.doubeye.Utils.array.getObjectFromArray({
                            id : value
                        }, self.logTypes);
                        if (type) {
                            return type.name
                        } else {
                            return value;
                        }
                    },
                    label : '日志类型'
                }, {
                    dataId : 'logs_message',
                    label : '信息'
                }, {
                    dataId : 'logs_create_date',
                    label : '时间'
                }, {
                    dataId : 'upload_time',
                    label : '上传时长（秒）'
                }];


                this.__operations = [{
                    text : '日志详情',
                    callback : function(record) {
                        self.showText(record);
                    }
                }];
                this.processCampusAndTeamUsers = function (data) {
                    if (data.campusList) {
                        self.campus = data.campusList;
                    }
                    self.campus.splice(0, 0, {
                        campusid : 0,
                        name : '所有校区'
                    });
                    if ($location.search().campusId) {
                        self.selectedCampusId = parseInt($location.search().campusId, 10);
                    }
                    self.getData(1);
                };
                httpService.sendRequest(self, this.campusUrl, {
                    loginname : self.loginName
                }, {
                    callback : self.processCampusAndTeamUsers
                }, {
                    errorMessage : '获取用户校区'
                });
                if ($location.search().startTime) {
                    this.startTime = $location.search().startTime;
                    this.dataGroup = 'custom';
                }
                if ($location.search().endTime) {
                    this.endTime = $location.search().endTime;
                }
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
                    campus_id : self.selectedCampusId ? self.selectedCampusId : '',
                    logs_type : self.selectedLogType.id < 0 ? '' : self.selectedLogType.id,
                    start_time : self.startTime,//日志开始时间
                    end_time : self.endTime,//日志结束间
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
