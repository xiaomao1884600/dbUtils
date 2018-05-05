/**
 * @see 本功能接口文档http://record_logs.mydoc.io/
 */
angular.module('fatalLog', []).component('fatalLog', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/log/fatalLog.html',
    controller: ['httpService', '$mdDialog', '$cookies', '$sce', 'dialogService', '$location',
        function fatalLogController(httpService, $mdDialog, $cookies, $sce, dialogService, $location) {

            var self = this;

            this.$onInit = function () {
                this.campusUrl = 'http://record.hxsd.local/api/recordinfo/getusercampusectask';
                this.dataUrl = 'http://record.hxsd.local/api/record/task/get_upload_human';
                this.processLogUrl ='http://record.hxsd.local/api/record/task/set_upload_human';
                this.redoUploadUrl = 'http://record.hxsd.local/api/record/task/set_retry_upload';
                this.bunchUploadUrl = 'http://record.hxsd.local/api/record/task/set_batch_retry_upload';
                this.loginName = $cookies.get("_userId");
                this.loginName = this.loginName.substr(0, this.loginName.indexOf('@'));
                this.totalPage = 0;
                this.page = 1;
                this.recordPerPage = 15;
                this.campus = [];
                this.logs = [];
                this.uploading = false;
                this.ignoreTypes = [{
                    id : -1,
                    name : '所有'
                }, {
                    id : '0',
                    name : '未处理'
                }, {
                    id : '1',
                    name : '已处理'
                }, {
                    id : '2',
                    name : '处理失败'
                }];
                this.selectedIgnoreType = this.ignoreTypes[1];
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
                    dataId : 'logs_create_date',
                    label : '时间'
                }, {
                    dataId : 'dispose_type',
                    label : '处理方式'
                }];
                this.__operations = [{
                    text : '日志详情',
                    callback : function(record) {
                        self.showText(record);
                        //dialogService.showContent('标题', '提示信息', '内容文本');
                    }
                }, {
                    text : '忽略',
                    id : 'ignore',
                    callback : function(record) {
                        httpService.sendRequest(self, self.processLogUrl, {
                            human_id : record.human_id,
                            dispose_type : 1
                        }, {
                            resultPropertyName : 'data.DATA',
                            callback : function() {
                                self.getData(self.page);
                            }
                        }, {
                            errorMessage : '标记处理结果出错'
                        });
                    }
                }, {
                    text : '重传',
                    id : 'redoUpload',
                    callback : function (record) {
                        var records = [];
                        records.push(record);
                        self.redoUpload(records);
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

            this.getParameters  = function() {
                return {
                    campus_id : self.selectedCampusId ? self.selectedCampusId : '',
                    ignoreType : self.selectedIgnoreType.id,
                    dispose_state : self.selectedIgnoreType.id >= 0 ? self.selectedIgnoreType.id : '',
                    start_time : self.startTime,//日志开始时间
                    end_time : self.endTime//日志结束间
                }
            };


            this.getData = function (start, end, page) {
                self.page = page;
                var params = self.getParameters();
                params.index = page;
                httpService.sendRequest(self, self.dataUrl, params, {
                    dataPropertyName : 'logs',
                    resultPropertyName : 'data.DATA',
                    resultRecordCountPropertyName : 'data.TOTAL_RECORDS',
                    callback : function (data, customParams, totalRecords) {
                        //根据选择的处理类型来决定是否显示处理方式列
                        self.__columnDefines[4].hidden = (self.selectedIgnoreType.id !== '1');
                        self.uploading = false;
                        data.forEach(function (element) {
                            if (self.selectedIgnoreType.id === '1') {
                                element._hideOperation = {
                                    redoUpload : true,
                                    ignore : true
                                };
                            }
                        });
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
            
            this.redoUpload = function (records) {
                self.uploading = true;
                if (!records) {
                    records = [];
                    self.logs.forEach(function (element) {
                        if (element.isSelected === true) {
                            records.push(element);
                        }
                    });
                }
                var ids = [];
                records.forEach(function (element) {
                    ids.push(element.human_id);
                });
                httpService.sendRequest(self, self.redoUploadUrl, {
                    human_id : ids
                }, {
                    callback : function() {
                        self.getData(self.page ? self.page : 1);
                    }
                }, {
                    errorMessage : '重传录音文件出错'
                });
            };

            this.bunchUpload = function() {
                var params = self.getParameters();
                httpService.sendRequest(self, self.bunchUploadUrl, params, {
                    callback : function() {
                        self.getData(1);
                    }
                }, {
                    errorMessage : '批量重传录音文件出错'
                });
            }
        }]
});
