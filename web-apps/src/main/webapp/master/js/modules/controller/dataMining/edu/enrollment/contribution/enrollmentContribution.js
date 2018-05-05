/**
 * Created by doubeye
 * 报名贡献度
 */
angular.module('enrollmentContribution', []).component('enrollmentContribution', {
    templateUrl: 'app/views/dataMining/edu/enrollment/contribution/enrollmentContribution.html',
    controller: ['httpService', '$cookies', 'dialogService', 'authorizationService',
        function enrollmentContributionController(httpService, $cookies, dialogService, authorizationService) {
            var self = this;
            this.adaDataUrl = 'http://record.hxsd.local/api/record/report/get_person_enrollment_contribute';
            this.teamDataUrl = 'http://record.hxsd.local/api/record/report/get_team_enrollment_contribute';
            this.mode = '个人';
            /**
             * 为了防止数据改变时重绘图表，加入一个控制量，在获得数据前暂停绘制，当数据获得后，并显示第一个人的数据时，将此值设置为false
             * @type {boolean}
             */
            this.noRangeRending = true;
            this.showTeamButtonTitle = '显示团队统计';
            this.displayTeamStat = false;
            /**
             * 时间段范围数组，改数组从服务端获得，用来展示团队数据的横轴，减少循环
             * @type {array}
             * @private
             */
            this.__range = null;
            this.user = authorizationService.getUser();
            this.chartWidth = (window.innerWidth - 300) / 2 - 10;
            this.$onInit = function () {
                this.dateGroup = 'lastDay30';
                this.developer = authorizationService.isDeveloper();
                this.__enrollListAda = [{
                    dataId : 'studentname',
                    label : '学生姓名'
                }, {
                    dataId : 'student_mobile',
                    label : '联系号码'
                }, {
                    dataId : 'enrollment_datetime',
                    label : '报名日期'
                }, {
                    dataId : 'contact_number',
                    label : '拨打电话人数'
                }, {
                    dataId : 'ada_enrolled_calls_total',
                    label : '本人播出次数'
                }, {
                    dataId : 'ada_enrolled_minute_total',
                    label : '本人播出时长（分钟）',
                    sortDataId : 'ada_enrolled_billable_total'
                }, {
                    dataId : 'enrolled_calls_total',
                    label : '播出次数'
                }, {
                    dataId : 'enrolled_minute_total',
                    label : '播出时长（分钟）',
                    sortDataId : 'enrolled_billable_total'
                }];
                this.__intervalContributionColumnDefine = [{
                    dataId : 'hour',
                    label : '时段'
                }, {
                    dataId : 'enrollment_total',
                    label : '报名个数'
                }];
                this.__enrollListTeam = [{
                    dataId : 'studentname',
                    label : '学生姓名'
                }, {
                    dataId : 'ada_username',
                    label : 'ada'
                }, {
                        dataId : 'student_mobile',
                        label : '联系号码'
                }, {
                    dataId : 'enrollment_datetime',
                    label : '报名日期'
                }, {
                    dataId : 'contact_number',
                    label : '拨打电话人数'
                }, {
                    dataId : 'ada_enrolled_calls_total',
                    label : '本人播出次数'
                }, {
                    dataId : 'ada_enrolled_minute_total',
                    label : '本人播出时长（分钟）',
                    sortDataId : 'ada_enrolled_billable_total'
                }, {
                    dataId : 'enrolled_calls_total',
                    label : '播出次数'
                }, {
                    dataId : 'enrolled_minute_total',
                    label : '播出时长（分钟）',
                    sortDataId : 'enrolled_billable_total'
                }];
                this.__enrollList = this.__enrollListAda;
                this.__intervalContributionColumnDefine = [{
                    dataId : 'hour',
                    label : '时段'
                }, {
                    dataId : 'enrollment_total',
                    label : '报名个数'
                }];
                this.currentUser = null;
                this.campuses = [];
                /**
                 *
                 * @type {Array}
                 */
                this.teamsInCharge = [];
                this.processChargeInfo = function (campusesInCharge, teamsInCharge) {
                    self.campuses = campusesInCharge;
                    self.teamsInCharge = teamsInCharge;
                    if (angular.isArray(self.campuses) && (self.campuses.length > 0)) {
                        self.selectedCampus = self.campuses[0];
                    }
                };
                authorizationService.getChargeInfo(self.processChargeInfo);
            };
            /**
             * 贡献度结果，每个元素为一个人，下面细分为以下几种贡献度
             *  callOutCount 拨出个数贡献度，类型为对象数组，包括对端号码， 播出个数，播出个数倒序排序
             *  callOutLength 播出时长贡献度，类型为对象数组，包括对端号码，通话总时长，播出总时长倒序排序
             *  timeRange 时段贡献度，类型为对象数组，包括时段，对端号码数，通话总时长，时段正序排序
             * @type {Array}
             */
            this.contributions = [];

            this.__enrollListChartConfigs = [{
                propertyName : 'callOutLength',
                axisConfigs : [{
                    label : '本人播出次数',
                    propertyNameInCollection : 'ada_enrolled_calls_total',
                    chartType : 'line',
                    defaultValue : 0
                }, {
                    label : '本人播出时长',
                    propertyNameInCollection : 'ada_enrolled_minute_total',
                    chartType : 'line',
                    defaultValue : 0
                }, {
                    label : '所有人播出次数',
                    propertyNameInCollection : 'enrolled_calls_total',
                    chartType : 'bar',
                    defaultValue : 0
                }, {
                    label : '所有人播出时长',
                    propertyNameInCollection : 'enrolled_minute_total',
                    chartType : 'bar',
                    defaultValue : 0
                }]
            }];

            this.__enrollIntervalChartConfigs = [{
                propertyName : 'range',
                axisConfigs : [{
                    label : '报名人数',
                    propertyNameInCollection : 'enrollment_total',
                    chartType : 'bar',
                    defaultValue : 0
                }]
            }];

            this.__teamEnrollChartConfigs = [{
                propertyName : 'teamEnroll',
                axisConfigs : [{
                    label : '报名人数',
                    propertyNameInCollection : 'data.person_enrollment_day.length',
                    chartType : 'line',
                    defaultValue : 0
                }]
            }];
            this.__teamEnrollRangeConfigs = [{
                propertyName : 'rangeStats',
                axisConfigs : [{
                    labelPropertyName: 'name',
                    propertyNameInCollection: 'enrollCount',
                    chartType: 'line',
                    defaultValue: 0
                }]
            }];

            this.processResult = function(results) {
                if (self.contributions.length > 0) {
                    self.noRangeRending = false;
                    self.renderChart(self.contributions[0]);
                    if (!self.__range) {
                        self.__range = {};
                        var entry = self.contributions[0];
                        if (angular.isArray(entry.data.person_enrollment_hour)) {
                            var index = 0;
                            entry.data.person_enrollment_hour.forEach(function (range) {
                                self.__range[range.hour] = index;
                                index ++;
                            });
                        }
                    }
                    var mobile;
                    self.contributions.forEach(function (contribution) {
                        if (self.mode === '团队') {
                            contribution.name = contribution.username;
                        }
                        if (angular.isArray(contribution.data.person_call_length)) {
                            contribution.data.person_call_length.forEach(function (call) {
                                mobile = call.student_mobile;
                                var callCount = com.doubeye.Utils.array.getObjectFromArray({student_mobile : mobile}, contribution.data.person_call_number);
                                if (callCount) {
                                    call.count = callCount.call_count;
                                    call.total = callCount.call_count_total;
                                }
                            });
                        }
                    });
                }
            };
            self.setDisplayTeamStat = function(display) {
                self.displayTeamStat = display;
                self.showTeamButtonTitle = display ? '收起团队统计' : '显示团队统计';
            };
            this.getData = function () {
                self.currentUser = null;
                self.noRangeRending = true;
                self.contributions = [];
                self.team = null;
                self.setDisplayTeamStat(false);

                if (!self.selectedCampus) {
                    dialogService.alert('提示', '请选择校区');
                    return;
                }
                var url, params = {
                    start_time : self.startTime,
                    end_time : self.endTime,
                    campus_id : self.selectedCampus.campusid
                };
                if (self.mode === '个人') {
                    url = self.adaDataUrl;
                    self.__enrollList = self.__enrollListAda;
                } else {
                    self.__enrollList = self.__enrollListTeam;
                    url = self.teamDataUrl;
                    var index = 0;
                    if (angular.isArray(self.selectedCampus.team_list)) {
                        params.team_num = com.doubeye.Utils.array.toString(self.selectedCampus.team_list, 'team_num');
                    }
                }
                httpService.sendRequest(self, url, params, {
                    dataPropertyName : 'contributions',
                    resultPropertyName : 'data',
                    callback: self.processResult
                }, {
                    errorMessage: '获取贡献度数据出错'
                }, {}, 'GET');
            };

            this.onMobileClick = function(params) {
                var mobile = params.name;
                var url = '/dbUtils/main.html#!/localRecordBrowser?jump=1&startTime=2000-01-01 00:00:00&endTime=' + self.endTime + ' 23:59:59&student=' + mobile + self.__getUserPart() + '&campusId=' + self.selectedCampus.campusid;
                window.open(url, '_blank');
            };


            this.renderChart = function (entry) {
                if (!self.noRangeRending) {
                    self.currentUser = entry;
                    if (self.currentUser.data) {
                        com.doubeye.Utils.chart.processLineChartData(self.currentUser.data.person_enrollment_day,
                            self.currentUser, 'student_mobile', self.__enrollListChartConfigs);
                        com.doubeye.Utils.chart.processLineChartData(self.currentUser.data.person_enrollment_hour,
                            self.currentUser, 'hour', self.__enrollIntervalChartConfigs);
                        console.log(self.currentUser);
                    }
                }
            };

            this.__getUserPart = function () {
                var userPart = '';
                if (self.mode === '个人') {
                    userPart = '&ada=' + self.currentUser.userid
                }
                return userPart;
            };

            this.showTeamStat = function () {
                self.setDisplayTeamStat(!self.displayTeamStat);
                if (self.displayTeamStat && !self.team) {
                    var rangeStats = {};
                    var fields = com.doubeye.Utils.objectRefactor.getFields(self.__range);
                    fields.forEach(function (entry) {
                        rangeStats[entry] = [];
                    });
                    self.contributions.forEach(function (user) {
                        //计算时段团队统计
                        if (angular.isArray(user.data.person_enrollment_hour)) {
                            user.data.person_enrollment_hour.forEach(function (enroll) {
                                rangeStats[enroll.hour].push({
                                    name : user.name,
                                    enrollCount : enroll.enrollment_total
                                });
                            });
                        }

                    });
                    self.team = {

                    };
                    rangeStats = com.doubeye.Utils.objectRefactor.objectToArray(rangeStats, 'range');
                    com.doubeye.Utils.chart.processLineChartData(self.contributions, self.team, 'name', self.__teamEnrollChartConfigs);
                    com.doubeye.Utils.chart.processLineChartDataWithDynaticValueProperty(rangeStats, self.team, 'range', null, self.__teamEnrollRangeConfigs);
                }
            }

        }]
});
