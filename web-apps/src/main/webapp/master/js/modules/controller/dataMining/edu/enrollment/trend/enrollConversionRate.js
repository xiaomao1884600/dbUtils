/**
 * 报名转化率报表
 */
angular.module('enrollConversionRate', []).component('enrollConversionRate', {
    templateUrl: 'app/views/dataMining/edu/enrollment/trend/enrollConversionRate.html',
    controller: ['httpService', '$cookies', 'dialogService', 'authorizationService', 'mathUtils', '$mdDialog', 'ngDialog',
        function enrollConversionRateController(httpService, $cookies, dialogService, authorizationService, mathUtils, $mdDialog, ngDialog) {
            var self = this;
            this.chartWidth = 400;
            this.chartHeight = 300;
            this.user = authorizationService.getUser();
            this.developer = authorizationService.isDeveloper();
            this.$onInit = function () {
                this.tabWidth = (window.innerWidth - 300) - 10;
                this.showTrendChart = false;
                this.mode = '自然日';
                this.dateGroup = 'lastDay7';
                this.campuses = [];
                this.teamDayDataUrl = "http://record.hxsd.local/api/record/report/get_team_enrolled_rate";
                this.teamTermDataUrl = "http://record.hxsd.local/api/record/report/get_team_term_enrolled_rate";
                this.enrollTrendDayUrl = "http://record.hxsd.local/api/record/report/get_enrollment_trend";
                authorizationService.getChargeInfo(function (allCampus) {
                    self.campuses = allCampus;
                });
                this.results = [];
                this.tabs = [{
                    type : 'all',
                    name : '全部'
                }];

                this.__teamDefines = [{
                    dataId : 'campus_title',
                    label : '校区'
                }, {
                    dataId : 'username',
                    label : '团队'
                }, {
                    dataId : 'enrolled_rate',
                    value : function(value) {
                        return value + '%';
                    },
                    label : '转化率'
                }, {
                    dataId : 'enrolled_total',
                    label : '报名量数'
                }, {
                    dataId : 'assign_total',
                    label : '接量合计'
                }];
                this.__teamDetailDefines = [{
                    dataId : 'username',
                    label : '团队'
                }, {
                    dataId : 'username',
                    label : 'ada'
                }, {
                    dataId : 'enrolled_rate',
                    value : function(value) {
                        return value + '%';
                    },
                    label : '转化率'
                }, {
                    dataId : 'enrolled_total',
                    label : '报名量数'
                }, {
                    dataId : 'assign_total',
                    label : '接量合计'
                }];

                this.__operations = [{
                    id : 'detail',
                    text : '详情',
                    callback : self.drillDown
                }];
                this.__adaEndrollTrendOperations = [{
                    id : 'enrollTrend',
                    text : '报名趋势',
                    callback : self.showEnrollTrend
                }];
                this.processResult = function(results, tab) {
                    if (tab) {
                        tab.data = results;
                        if (angular.isArray(results)) {
                            var enrolled = 0, assigned = 0, rate = 0;
                            results.forEach(function (entry) {
                                enrolled += entry.enrolled_total;
                                assigned += entry.assign_total;
                            });
                            rate = mathUtils.round(enrolled * 100 / assigned, 2);
                            var team = tab.type === 'all' ? '所有校区' : (tab.leader_name + '团队');
                            tab.summaries = team + '平均转化率' + rate + '%, 总报名量：' + enrolled + '，接量合计：' + assigned;
                        }
                    }
                };
                /**
                 * 获得查询参数
                 * @private
                 */
                self.__getParameters = function (tab) {
                    var parameters = {};
                    if (self.mode === '自然日') {
                        parameters.start_time = self.startTime;
                        parameters.end_time = self.endTime;
                    }
                    if (self.mode === '期') {
                        parameters.termid = self.selectedTerm.termid;
                    }
                    if (self.selectedCampus) {
                        parameters.campus_id = self.selectedCampus.campusid;
                    }
                    if (tab.type === 'team') {
                        //parameters.team_num = tab.team_num;
                        parameters.manager_userid = tab.manager_userid;
                    }
                    return parameters;
                };

                this.getData = function (tab) {
                    self.showTrendChart = false;
                    if (!tab) {
                        tab = self.tabs[0];
                        if (self.tabs.length > 1) {
                            self.tabs.splice(1, self.tabs.length);
                        }
                    }
                    var parameters = self.__getParameters(tab);
                    var url = self.mode === '自然日' ? self.teamDayDataUrl : self.teamTermDataUrl;
                    httpService.sendRequest(self, url, parameters, {
                        dataPropertyName : 'results',
                        resultPropertyName : 'data',
                        callback: function(results) {
                            self.processResult(results, tab);
                        }
                    }, {
                        errorMessage: '获取分配数据出错'
                    }, {}, 'POST');
                };
            };


            this.drillDown = function (record) {
                self.currentRecord = record;
                var currentTab = self.tabs[self.selectedIndex];
                var newTab;
                if (currentTab.type === 'all') {
                    newTab = {
                        type : 'team',
                        name : '(' + record.username + ')',
                        team_num : record.team_num,
                        leader_name : record.username,
                        campus_id : record.campus_id,
                        manager_userid : record.manager_userid
                    };
                }
                self.tabs.push(newTab);
                self.selectedIndex = self.tabs.length - 1;
                self.getData(newTab);
            };
            this.remove = function (tab) {
                var index = self.tabs.indexOf(tab);
                self.tabs.splice(index, 1);
            };

            this.showEnrollTrend = function (record) {
                var adaIds = [];
                self.chartTitle = record.username + '报名趋势';
                    httpService.sendRequest(self, self.enrollTrendDayUrl, {
                    start_time: self.startTime,
                    end_time: self.endTime,
                    campus_id : self.tabs[self.selectedIndex].campus_id,
                    ada_ids : record.userid,
                    _noProcessingDialog : true
                }, {
                    dataPropertyName : 'treadData',
                    resultPropertyName : 'data',
                    callback: self.processEnrollTrend
                }, {
                    errorMessage: '获取趋势数据出错'
                }, {}, 'GET');
            };


            this.__enrollTrendChartConfig = [{
                propertyName : 'enrollTrendData',
                axisConfigs : [{
                    label: '报名总数',
                    propertyNameInCollection: 'enrolled',
                    chartType: 'line',
                    defaultValue: 0
                }]
            }];
            this.processEnrollTrend = function (data) {
                var result = [];
                if (angular.isArray(data)) {
                    data.forEach(function (dateEntry) {
                        if (angular.isArray(dateEntry.data)) {
                            result.push({
                                date : dateEntry.date,
                                enrolled : dateEntry.data[0].enrollment_total
                            })
                        }
                    });
                }
                com.doubeye.Utils.chart.processLineChartData(result, self, 'date', self.__enrollTrendChartConfig);
                self.showTrendChart = true;
                /*
                $mdDialog.show({
                    contentElement: '#enrollTrend',
                    clickOutsideToClose: true
                });
                */
            };
        }]
});
