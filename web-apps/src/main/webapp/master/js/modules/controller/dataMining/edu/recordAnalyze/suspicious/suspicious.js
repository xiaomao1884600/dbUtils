/**
 * Created by zhanglu1782 on 2016/12/21.
 *
 */
angular.module('suspicious', []).component('suspicious', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/suspicious/suspicious.html',
    controller: ['httpService', '$cookies', '$mdDialog', 'dialogService', 'authorizationService',
        function suspiciousController(httpService, $cookies, $mdDialog, dialogService, authorizationService) {
            var self = this;
            this.campusUrl = 'http://record.hxsd.local/api/record/get_record_campus_list';
            this.contentAbnormalUrl = 'http://record.hxsd.local/api/record/get_abnormal_mobile';
            this.ossPrefix = 'http://hxsd-backup.oss-cn-beijing.aliyuncs.com/';
            this.$onInit = function () {
                this.dateGroup = 'yesterday';
                this.contentAbnormalTypes = [{
                    id : 0,
                    name : '所有'
                }, {
                    id : 1,
                    name : '空内容'
                }, {
                    id : 2,
                    name : '凑时长嫌疑'
                }];
                this.seletedContentAbnormalType = this.contentAbnormalTypes[0];
                this.__adaColumnDefines = [{
                    dataId : 'username',
                    label : 'ada'
                }, {
                    dataId : 'student_mobile',
                    label : '联系号码'
                }, {
                    dataId : 't',
                    label : '联系次数'
                }, {
                    dataId : 's',
                    label : '通话总时长'
                }, {
                    dataId : 'a',
                    label : '平均通话时长'
                }];
                this.__clientColumnDefines = [{
                        dataId : 'student_mobile',
                        label : '联系号码'
                    }, {
                        dataId : 'username',
                        label : 'ada'
                    }, {
                        dataId : 't',
                        label : '联系次数'
                    }, {
                        dataId : 's',
                        label : '通话总时长'
                    }, {
                        dataId : 'a',
                        label : '平均通话时长'
                }];
                this.__contentAbnormalColumnDefines = [{
                    dataId : 'username',
                    label : 'ADA'
                }, {
                    dataId : 'student_mobile',
                    label : '用户电话'
                }, {
                    dataId : 'calls_count',
                    label : '拨出次数'
                }, {
                    dataId : 'billable_total_title',
                    sortDataId : 'billable_total',
                    label : '总时长'
                }, {
                    dataId : 'avg_time_title',
                    sortDataId : 'avg_time',
                    label : '平均通话时长'
                }, {
                    dataId : 'serious_num',
                    label : '凑时长可疑电话数'
                }];
                this.__suspiciousOperations = [{
                    text : '详情',
                    callback : function (record) {
                        console.log(record);
                        var studentMobile = record.student_mobile;
                        var url = '/dbUtils/main.html#!/localRecordBrowser?jump=1&startTime=' + self.startTime + ' 00:00:00&endTime=' + self.endTime + ' 23:59:59&student=' + studentMobile + '&campusId=' + self.selectedCampus.campusid;
                        window.open(url, '_blank');
                    }
                }];
                this.__contentAbnormalOperations = [{
                    text : '详情',
                    callback : function (record) {
                        self.currentCallList = record.record_list;
                        self.currentAnalyzeResult = null;
                        $mdDialog.show({
                            contentElement: '#detailBrowser',
                            clickOutsideToClose: true
                        });
                    }
                }];
                this.__currentCallListColumnDefines = [{
                    dataId : 'datetime',
                    label : '通话时间'
                }, {
                    dataId : 'billable',
                    label : '通话时长'
                }, {
                    dataId : 'calltype_title',
                    label : '通话类型'
                }, {
                    dataId : 'serious_type_title',
                    label : '异常类型'
                }];
                this.__currentCallListOperations = [{
                    text : '识别结果',
                    callback : function (record) {
                        record.ossPath = self.ossPrefix + record.oss_path;
                        self.currentAnalyzeResult = record;
                        console.log(record);
                    }
                }]
            };
            this.campuses = [];
            authorizationService.getChargeInfo(function (campusesInCharge) {
                self.campuses = campusesInCharge;
                if (self.campuses.length === 1) {
                    self.selectedCampus = self.campuses[0];
                }
            });

            this.getData = function () {
                if (!this.selectedCampus) {
                    dialogService.alert('提示', '请选择校区');
                    return;
                }
                httpService.sendRequest(self, self.contentAbnormalUrl, {
                    startTime : self.startTime + ' 00:00:00',
                    endTime : self.endTime + ' 23:59:59',
                    type : self.seletedContentAbnormalType.id === 0 ? '' : self.seletedContentAbnormalType.id,
                    campus_id : self.selectedCampus.campusid
                }, {
                    dataPropertyName : 'contentAbnormalRecords',
                    resultPropertyName : 'data',
                    callback: function (data) {

                    }
                }, {
                    errorMessage: '获得空对话内容数据出错'
                });

                httpService.sendRequest(self, 'generalRouter', {
                    objectName : 'com.doubeye.record.service.SuspiciousService',
                    action : 'getSuspiciousInfo',
                    startTime : self.startTime + ' 00:00:00',
                    endTime : self.endTime + ' 23:59:59',
                    campusId : self.selectedCampus.campusid
                }, {
                    returnRawResponse : true,
                    callback: function (data) {
                        self.multiAda = data.suspicious.multiAda;
                        self.multiClient = data.suspicious.multiClient;
                    }
                }, {
                    errorMessage: '获得可疑电话出错'
                });
            };
            //this.getCampuses();
        }]
});
