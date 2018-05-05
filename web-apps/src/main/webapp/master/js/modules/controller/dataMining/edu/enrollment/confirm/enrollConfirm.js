/**
 * Created by doubeye
 * 报名确认
 */
angular.module('enrollConfirm', []).component('enrollConfirm', {
    templateUrl: 'app/views/dataMining/edu/enrollment/confirm/enrollConfirm.html',
    controller: ['httpService', 'dialogService',
        function enrollConfirmController(httpService, dialogService) {
            var self = this;
            this.classTypes = [{
                id : '0',
                name : '所有班级类型'
            }, {
                id : '1',
                name : '专业班'
            }, {
                id : '2',
                name : '基础班'
            }];
            this.selectedClassType = this.classTypes[1];
            this.createTypes = [{
                id : '0',
                name : '所有建表类型'
            }, {
                id : '1',
                name : '渠道'
            }, {
                id : '2',
                name : '线下'
            }];
            this.selectedCreateType = this.createTypes[2];
            this.$onInit = function () {
                this.etlUrl = "http://record.hxsd.local/api/etl/sync_enrolled_feedback";
                //this.termUrl = "http://record.hxsd.local/api/record/get_term";
                this.generateData = function () {
                    if (!self.selectedTerm) {
                        dialogService.alert('请选择期数');
                        return;
                    }
                    dialogService.confirm('确认', '是否要生成' + self.selectedTerm.title + '期的确认数据，此过程大约要持续5分钟', {}, function () {
                        httpService.sendRequest(self, self.etlUrl, {
                            startTermid : self.selectedTerm.termid,
                            endTermid : self.selectedTerm.termid
                        }, {
                            callback : function () {
                                httpService.sendRequest(self, 'generalRouter', {
                                    objectName : 'com.hxsd.services.productLine.e.enrollment.confirm.EnrollmentConfirmService',
                                    action : 'generateConfirmService',
                                    term : self.selectedTerm.termid
                                }, {
                                    returnRawResponse : true,
                                    callback : function (data) {
                                        console.log(data);
                                        self.dataGeneratedTime = data.dataGeneratedTime;
                                    }
                                });
                            }
                        });
                    });
                };
                this.getAuthority = function () {
                    return '&'
                };
                this.downloadDetail = function () {
                    if (!self.selectedTerm) {
                        dialogService.alert('请选择期数');
                        return;
                    }
                    window.open("generalRouter?objectName=com.hxsd.services.productLine.e.enrollment.confirm.EnrollmentConfirmService&action=getDetailFile&term=" + self.selectedTerm.termid + "&termTitle=" + self.selectedTerm.title);
                };

                this.downloadResignedEnrollment = function() {
                    if (!self.selectedTerm) {
                        dialogService.alert('请选择期数');
                        return;
                    }
                    window.open("generalRouter?objectName=com.hxsd.services.productLine.e.enrollment.confirm.EnrollmentConfirmService&action=getResignedEnrollment&term=" + self.selectedTerm.termid + "&termTitle=" + self.selectedTerm.title);
                };
                this.__sharedColumnDefines = [{
                    dataId : 'total',
                    label : '总报名数'
                }, {
                    dataId : 'confirm',
                    label : '准时入学'
                }, {
                    dataId : 'drop',
                    label : '不能准时入学'
                }, {
                    dataId : 'unknown',
                    label : '未知'
                }, {
                    dataId : 'confirmPercent',
                    label : '准时入学占比'
                }, {
                    dataId : 'dropPercent',
                    label : '不能准时入学占比'
                }, {
                    dataId : 'unknownPercent',
                    label : '未知占比'
                }];
                this.__specialtyColumnDefines = [{
                    dataId : 'faculty',
                    label : '专业'
                }, {
                    dataId : 'specialty',
                    label : '班级名称'
                }];
                this.__specialtyColumnDefines = this.__specialtyColumnDefines.concat(this.__sharedColumnDefines);
                this.__adaColumnDefines = [{
                    dataId : 'manager',
                    label : '团队经理'
                }, {
                    dataId : 'ada',
                    label : 'ada'
                }];
                this.__adaColumnDefines = this.__adaColumnDefines.concat(this.__sharedColumnDefines);
                this.statisticData = {};
                this.getStatistic = function () {
                    if (!self.selectedTerm) {
                        dialogService.alert('请选择期数');
                        return;
                    }
                    httpService.sendRequest(self, 'generalRouter', {
                        objectName : 'com.hxsd.services.productLine.e.enrollment.confirm.EnrollmentConfirmService',
                        action : 'getStatistic',
                        classType : this.selectedClassType ? this.selectedClassType.id : 0,
                        createType : this.selectedCreateType ? this.selectedCreateType.id : 0,
                        term : self.selectedTerm.termid
                    }, {
                        returnRawResponse : true,
                        callback : function (data) {
                            self.statisticData.campuses = data.campuses;
                            self.statisticData.all = data.all;
                            console.log(self.statisticData);
                        }
                    });
                };
                this.getDataGeneratedTime = function (selectedTerm) {

                    httpService.sendRequest(self, 'generalRouter', {
                        objectName : 'com.hxsd.services.productLine.e.enrollment.confirm.EnrollmentConfirmService',
                        action : 'getDataGeneratedTime',
                        term : selectedTerm.termid
                    }, {
                        returnRawResponse : true,
                        callback : function (data) {
                            self.dataGeneratedTime = data.dataGeneratedTime;
                        }
                    });

                };
                /*
                this.getTerm = function () {
                    httpService.sendRequest(self, self.termUrl, {
                        start_term_id : 167
                    }, {
                        dataPropertyName : 'terms',
                        resultPropertyName : 'data',
                        callback : function (data) {
                            if (angular.isArray(self.terms)) {
                                for (var i = 0; i < self.terms.length; i ++) {
                                    if (self.terms[i].next === 1 || self.terms[i].termid === 167) {
                                        self.selectedTerm = self.terms[i];
                                        break;
                                    }
                                }
                                self.getDataGeneratedTime();
                            }
                        }
                    });
                };
                this.getTerm();
                */
            }
        }]
});