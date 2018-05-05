/**
 * Created by doubeye
 * 校区目标完成
 */
angular.module('campusGoalAchievement', []).component('campusGoalAchievement', {
    templateUrl: 'app/views/dataMining/edu/enrollment/goal/campusGoalAchievement.html',
    controller: ['httpService', 'dialogService', 'authorizationService',
        function campusGoalAchievementController(httpService, dialogService, authorizationService) {
            var self = this;
            this.$onInit = function () {
                this.dataUrl = "http://record.hxsd.local/api/record/report/get_campus_term_enrolled";
                this.remarkSaveUrl = "http://record.hxsd.local/api/edu/set_campus_term_enrolled_remark";
                this.chartWidth = 900;
                this.chartHeight = 600;
                this.results = [];
                this.__columnDefines = [];
                this.developer = authorizationService.isDeveloper();
                this.getRemarkConfig = function () {
                    var height = 20 + 14 * (self.chartRemark.split('\n').length);
                    self.chartHeight = 600 + height;
                    if (self.chartRemark) {
                        return chartRemarkConfig = {
                            left : '10%',
                            bottom : 30,
                            shape : {
                                width : 800,
                                height : height
                            },
                            text : self.chartRemark
                        };
                    } else {
                        return null;
                    }
                };
                self.campusCount = -1;
                this.getDataId = function(termId, propertyName) {
                    return 'term_' + termId + '_' + propertyName;
                };

                this.getColumnDefines = function () {
                    self.datePropertyNames = [];
                    var columnDefines = [{
                        dataId : 'campus_title',
                        label : '校区'
                    }];
                    var goalDataId, achieveDataId, percentDataId;
                    if (self.selectedStartTerm && self.selectedEndTerm) {
                        var startTerm = parseInt(self.selectedStartTerm.title, 10);
                        var endTerm = parseInt(self.selectedEndTerm.title, 10);
                        for (var i = startTerm; i <= endTerm; i++) {
                            goalDataId = self.getDataId(i, 'goal');
                            achieveDataId = self.getDataId(i, 'achieve');
                            percentDataId = self.getDataId(i, 'percent');
                            columnDefines.push({
                                dataId: goalDataId,
                                sortDataId: goalDataId,
                                label: i + '期目标'
                            });
                            columnDefines.push({
                                dataId: achieveDataId,
                                sortDataId: achieveDataId,
                                label: i + '期完成'
                            });
                            columnDefines.push({
                                dataId: percentDataId,
                                sortDataId: percentDataId,
                                label: i + '期完成比率',
                                value : function (value) {
                                    return value + '%'
                                }
                            });
                        }
                    }
                    columnDefines.push({
                        dataId: 'enrolled_goal',
                        sortDataId : 'enrolled_goal',
                        label: '总体目标'
                    });
                    columnDefines.push({
                        dataId: 'students',
                        sortDataId: 'students',
                        label: '总体完成'
                    });
                    columnDefines.push({
                        dataId: 'enrolled_rate',
                        sortDataId: 'enrolled_rate',
                        label: '总体完成比率',
                        value : function (value) {
                            return value + '%'
                        }
                    });
                    this.__columnDefines = columnDefines;
                };
                this.getColumnDefines();

                this.__chartConfigs = [{
                    propertyName : 'achieves',
                    filter : function (data) {
                        return data.campus_id === 0;
                    },
                    axisConfigs : [{
                        label: '完成',
                        chartType : 'bar',
                        propertyNameInCollection: 'enrolled_rate',
                        defaultValue: 0,
                        barMaxWidth : 20,
                        color : '#60c0dd',
                        itemStyle : {
                            normal: {
                                barBorderRadius: 0,
                                label: {
                                    show: true,
                                    textStyle: {
                                        color: "rgba(0,0,0,1)"
                                    },
                                    position: 'top',
                                    formatter: function (p) {
                                        return p.value + '%';
                                    }
                                }
                            }
                        }
                    }, {
                        label: '平均值',
                        chartType : 'line',
                        propertyNameInCollection: 'avg',
                        defaultValue: 0,
                        itemStyle : {
                            normal: {
                                barBorderRadius: 0,
                                label: {
                                    show: true,
                                    textStyle: {
                                        color: "rgba(0,0,0,1)"
                                    },
                                    position: 'top',
                                    formatter: function (p) {
                                        return (p.dataIndex === self.campusCount - 1) ? p.value + '%' : '';
                                    }
                                }
                            }
                        }
                    }]
                }];

                this.processResult = function (data) {
                    self.getColumnDefines();
                    //校区数等于数组长度 - 1（全国合计）
                    self.campusCount = data.length - 1;
                    var term, chartData = [], summary = '全国', campusEntry;
                    if (angular.isArray(data)) {
                        var avg = data[data.length - 1].total.enrolled_rate;
                        for (var i = 0; i < data.length; i ++) {
                            campusEntry = data[i];
                            if (angular.isArray(campusEntry.term)) {
                                campusEntry.term.forEach(function (termEntry) {
                                    term = termEntry.term_title;
                                    campusEntry[self.getDataId(term, 'goal')] = termEntry.enrolled_goal;
                                    campusEntry[self.getDataId(term, 'achieve')] = termEntry.students;
                                    campusEntry[self.getDataId(term, 'percent')] = termEntry.enrolled_rate;
                                    if (campusEntry.campus_id === 0) {
                                        summary += term + '期目标：' + termEntry.enrolled_goal + '，完成：' + termEntry.students + '，目标完成率：' + termEntry.enrolled_rate + '%，';
                                    }
                                });
                                campusEntry.enrolled_goal = campusEntry.total.enrolled_goal;
                                campusEntry.students = campusEntry.total.students;
                                campusEntry.enrolled_rate = campusEntry.total.enrolled_rate;
                                chartData.push({
                                    campus_id : campusEntry.campus_id,
                                    campus_title : campusEntry.campus_title,
                                    enrolled_rate : campusEntry.enrolled_rate,
                                    avg : avg
                                });
                                if (campusEntry.campus_id === 0) {
                                    summary += '总体目标：' + campusEntry.enrolled_goal + '，总体完成：' + campusEntry.students + '，总体目标完成率：' + campusEntry.enrolled_rate + '%';
                                    data.pop();
                                }
                            }
                        }
                    }
                    self.summary = summary;
                    com.doubeye.ArraySortor.doSort(chartData, [{
                        dataId : 'enrolled_rate',
                        order : com.doubeye.constant.DB.ORDER_BY.DESC
                    }]);

                    com.doubeye.Utils.chart.processLineChartData(chartData, self, 'campus_title', self.__chartConfigs);
                };
                
                this.getData = function () {
                    if (self.selectedStartTerm.termid > self.selectedEndTerm.termid) {
                        dialogService.toast("开始期数应该小于结束期数");
                    } else {
                        self.getColumnDefines();

                        httpService.sendRequest(self, self.dataUrl, {
                            start_termid : self.selectedStartTerm.termid,
                            end_termid : self.selectedEndTerm.termid
                        }, {
                            // resultPropertyName : 'data',
                            // dataPropertyName : 'results',
                            returnRawResponse : true,
                            callback : function (data) {
                                var result = data.data, remark = result.remark;
                                if (remark && angular.isArray(remark.remark_list) && (remark.remark_list.length > 0)) {
                                    self.chartRemark = remark.remark_list[remark.remark_list.length - 1].remark;
                                    self.chartRemarkConfig = self.getRemarkConfig();
                                    self.remarkList = remark.remark_list;
                                } else {
                                    self.chartRemark = '';
                                    self.remarkList = [];
                                }
                                self.results = result.campus;
                                self.processResult(result.campus);

                            }
                        });

                    }
                };



                this.chartYAxisFormatter = function (value) {
                    return value + '%';
                };

                this.saveChartRemark = function () {
                    httpService.sendRequest(self, self.remarkSaveUrl, {
                        start_termid : self.selectedStartTerm.termid,
                        end_termid : self.selectedEndTerm.termid,
                        remark : self.chartRemark
                    }, {
                        callback : function (data) {
                            self.chartRemarkConfig = self.getRemarkConfig();
                        }
                    });

                };

                this.showAllRemark = function () {
                    $mdDialog.show({
                        contentElement: '#noteDetail',
                        clickOutsideToClose: true
                    });
                };
            }//END OF $onInit
        }]//EDN OF CONTROLLER
});
