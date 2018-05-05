/**
 * Created by doubeye
 * 专业日报名预警
 */
angular.module('specialtyDayEnrollWarning', []).component('specialtyDayEnrollWarning', {
    templateUrl: 'app/views/dataMining/edu/enrollment/trend/specialtyDayEnrollWarning.html',
    controller: ['httpService', 'projectManageUtils', 'mathUtils', 'authorizationService', 'collectionUtils', 'dialogService', '$mdDialog',
        function specialtyDayEnrollWarningController(httpService, projectManageUtils, mathUtils, authorizationService, collectionUtils, dialogService, $mdDialog) {
            var self = this;
            this.$onInit = function () {
                this.today = com.doubeye.Utils.dateTime.addDay(new Date(), -1);
                /*
                this.currentTermWarning = 15;
                this.nextTermWarning = 10;
                */
                /**
                 * 是否显示预测值
                 * @type {boolean}
                 */
                this.showExpectation = false;
                /*
                下一期结束的时间
                 */
                this.nextTermStartDate = '';
                /**
                 * 报警数值
                 * @type {number}
                 */
                this.warningCount = 15;
                this.developer = authorizationService.isDeveloper();
                this.userName = authorizationService.getUser().userName;
                this.dateGroup = 'lastDay7';
                this.dataUrl = 'http://record.hxsd.local/api/record/report/get_clazz_enrolled';
                this.saveNoteUrl = 'http://record.hxsd.local/api/edu/set_clazz_enrolled_remark';
                authorizationService.getChargeInfo(function (allCampus) {
                    self.campuses = allCampus;
                    if (self.campuses.length > 0) {
                        self.selectedCampus = self.campuses[0];
                    }
                });
                /**
                 * 处理后的结果数据中，包含的日期属性名
                 * @type {Array}
                 */
                this.datePropertyNames = [];
                this.getColumnDefines = function () {
                    self.datePropertyNames = [];
                    var columnDefines = [{
                        dataId : 'campus_title',
                        label : '校区'
                    }, {
                        dataId : 'clazz_title',
                        label : '班级'
                    }, {
                        dataId : 'planstudents',
                        label : '预算人数'
                    }];

                    if (this.startTime) {
                        var days = com.doubeye.Utils.dateTime.dayDiff(self.endTime, self.startTime);
                        for (var i = 0; i <= days; i ++) {
                            var date = com.doubeye.Utils.dateTime.addDay(new Date(self.startTime), i).format('yyyy-MM-dd');
                            columnDefines.push({
                                dataId : 'days.date_' + date + '.html',
                                sortDataId : 'days_date_' + date.replace(new RegExp('-', 'g'), '_') + '_students',
                                label : date,
                                html : '<span></span>'
                            });
                            self.datePropertyNames.push('date_' + date);
                        }
                    }
                    columnDefines.push({
                        dataId : 'students',
                        sortDataId : 'students',
                        label : '当前报名数'
                    });
                    columnDefines.push({
                        dataId : 'confirm_students',
                        sortDataId : 'confirm_students',
                        label : '确认入学数'
                    });
                    columnDefines.push({
                        dataId : 'fullPayment_students',
                        sortDataId : 'fullPayment_students',
                        label : '全款学生数'
                    });
                    columnDefines.push({
                        dataId : 'clazz_seats',
                        sortDataId : 'clazz_seats',
                        label : '班级容量',
                        hidden : !self.showExpectation
                    });
                    columnDefines.push({
                        dataId : 'expected',
                        sortDataId : 'expected',
                        label : '预计招生人数',
                        hidden : !self.showExpectation
                    });
                    columnDefines.push({
                        dataId : 'deExpected',
                        sortDataId : 'deExpected',
                        label : 'DE预测',
                        hidden : !self.showExpectation
                    });
                    columnDefines.push({
                        dataId : 'warningTcpi',
                        sortDataId : 'warningTcpi',
                        label : 'tcpi',
                        hidden : !self.showExpectation
                    });
                    self.__columnDefines = columnDefines;
                };


                this.processExceptation = function (value) {
                    if (angular.isArray(value)) {
                        for (var i = 0; i < value.length; i ++) {
                            self.data[i].deExpected = value[i];
                        }
                    }
                };

                this.onShowExpectationClick = function () {
                    self.getColumnDefines();
                    var expectationDays = com.doubeye.Utils.dateTime.dayDiff(self.selectedTerm.startdate, self.endTime);
                    if (self.showExpectation && angular.isArray(self.data) && expectationDays > 0) {
                        var values = [];
                        self.data.forEach(function (classEntry) {
                            if (angular.isArray(classEntry.date)) {
                                var classEnrolled = [];
                                classEntry.date.forEach(function (classDateEntry) {
                                    classEnrolled.push(classDateEntry.students);
                                });
                                values.push(classEnrolled);
                            }
                        });
                        httpService.sendRequest(self, 'generalRouter', {
                            objectName : 'com.doubeye.enroll.expectation.EnrollExpectation',
                            action : 'getBunchExpectation',
                            expectationDays : expectationDays,
                            enrolls : values
                        }, {
                            callback: self.processExceptation
                        }, {
                            errorMessage: '获得指数平滑预测值出错'
                        });
                    }
                };

                this.onNoteClick = function (row) {
                    self.currentClass = row;
                    $mdDialog.show({
                        contentElement: '#noteDetail',
                        clickOutsideToClose: true
                    });
                };

                self.__operations = [{
                    text : '备注',
                    render : function(record, config) {
                        if (record.remark_list && record.remark_list.length > 0) {
                            return config.text + '(' + record.remark_list.length + ')';
                        } else {
                            return config.text;
                        }
                    },
                    callback : self.onNoteClick
                }];




                this.changeColumnDefines = function () {
                    self.__columnDefines = self.getColumnDefines();
                };
                //如果所选期数的结束时间是否大于下一期的时间，则报警值为10，否则表示为当期，报警数字为15
                this.onTermChange = function(term) {
                    if (com.doubeye.Utils.dateTime.dayDiff(self.nextTermStartDate, term.startdate) >= 0) {
                        self.warningCount = 15;
                    } else {
                        self.warningCount = 10;
                    }
                } ;
                this.onTermLoaded = function (terms) {
                    self.terms = terms;
                    self.nextTermStartDate = '';
                    if (angular.isArray(self.terms)) {
                        self.terms.forEach(function (term) {
                            if (term.next) {
                                self.nextTermStartDate = term.startdate
                            }
                        });
                    }
                };
                this.getColumnDefines();
                this.data = [];
                this.computeWarning = function (enrollData) {
                    var dayLeft = com.doubeye.Utils.dateTime.dayDiff(self.selectedTerm.startdate, enrollData.date);
                    if (!enrollData.students) {
                        enrollData.students = 0;
                    }
                    //enrollData.countWarning = enrollData.students < self.warningCount;根据剩余天数来进行预警
                    if (dayLeft <= 14 && enrollData.students < 20) {
                        enrollData.countWarning = true;
                    } else if (dayLeft > 14 && dayLeft <= 21 && enrollData.students < 15) {
                        enrollData.countWarning = true;
                    } else if (dayLeft > 21 && enrollData.students < 10) {
                        enrollData.countWarning = true;
                    } else {
                        enrollData.countWarning = false;
                    }
                    enrollData.tcpi = projectManageUtils.calculateTcpiByBac(enrollData.planstudents, enrollData.students, dayLeft, 90);
                    enrollData.warningTcpi = projectManageUtils.calculateTcpiByBac(25, enrollData.students, dayLeft, 90);
                    enrollData.html = enrollData.students;
                    if (enrollData.countWarning) {
                        enrollData.html += '<span style="background-color: #ed0505; width: 16px; height: 16px; display: inline-block"></span>';
                    } else {
                        enrollData.html += '<span style="background-color: #8ccc0a; width: 16px; height: 16px; display: inline-block"></span>';
                    }
                    /*
                    if (enrollData.tcpi >= 1.1) {
                        enrollData.html += '<span style="background-color: firebrick; width: 16px; height: 16px; display: inline-block"></span>';
                    }
                    if (enrollData.warningTcpi >= 2) {
                        enrollData.html += '<span style="background-color: lawngreen; width: 16px; height: 16px; display: inline-block"></span>';
                    }
                    */
                };
                this.processResult = function (data) {
                    var fieldName, lastDateEntry, dayLeft = com.doubeye.Utils.dateTime.dayDiff(self.selectedTerm.startdate, self.endTime);
                    if (angular.isArray(data)) {
                        data.forEach(function (classData) {
                            classData.days = collectionUtils.arrayToObject(classData.date, 'date', 'date_', function(classRecord, dayRecord) {
                                self.computeWarning(dayRecord);
                                //将每天每个班级的报名数添加到班级的根节点中，用来做排序用
                                fieldName = ('days_date_' + dayRecord.date + '_students').replace(new RegExp('-', 'g'), '_');
                                classData[fieldName] = dayRecord.students;
                            });
                            if (angular.isArray(classData.date) && classData.date.length > 0) {
                                lastDateEntry = classData.date[classData.date.length - 1];
                                classData.expected = Math.round(projectManageUtils.calculateException(lastDateEntry.students, dayLeft, 90));
                                classData.warningTcpi = mathUtils.round(lastDateEntry.warningTcpi, 2);
                            }
                            //delete classData.date;
                        });
                    }
                };
                this.getData = function () {
                    if (!self.selectedCampus) {
                        dialogService.toast('请先选择校区');
                        return;
                    }
                    self.getColumnDefines();
                    httpService.sendRequest(self, self.dataUrl, {
                        termid : self.selectedTerm.termid,
                        campus_id : self.selectedCampus.campusid,
                        start_time : self.startTime,
                        end_time : self.endTime
                    }, {
                        dataPropertyName : 'data',
                        resultPropertyName : 'data',
                        callback: self.processResult
                    }, {
                        errorMessage: '获取班级报名人数出错'
                    });
                };

                this.saveNote = function () {
                    httpService.sendRequest(self, self.saveNoteUrl, {
                        clazzid : self.currentClass.clazzid,
                        remark : self.currentNote,
                        _noProcessingDialog : true
                    }, {
                        callback : function () {
                            self.currentClass.remark_list.splice(0, 0, {
                                updated_at : new Date().format('yyyy-MM-dd HH:mm:ss'),
                                remark : self.currentNote,
                                username : self.userName
                            });
                        }
                    }, {
                        errorMessage: '保存备注出错'
                    });
                };
            }//END OF $onInit
        }]//EDN OF CONTROLLER
});
