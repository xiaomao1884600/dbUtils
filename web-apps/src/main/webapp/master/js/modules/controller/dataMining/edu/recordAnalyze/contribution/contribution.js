/**
 * Created by doubeye
 * 有效呼出贡献度
 */
angular.module('contribution', []).component('contribution', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/contribution/contribution.html',
    controller: ['httpService', '$cookies', 'dialogService', 'authorizationService',
        function contributionController(httpService, $cookies, dialogService, authorizationService) {
            var self = this;
            this.adaDataUrl = 'http://record.hxsd.local/api/record/report/get_personal_contribute';
            this.teamDataUrl = 'http://record.hxsd.local/api/record/report/get_teams_contribute';
            this.feedbackLevelUrl = "http://record.hxsd.local/api/record/report/get_record_feedback_level_contribute";

            /**
             * 由于存在多个Ajax，且真个界面以原来的data为主，因此需要用ajaxGroup概念进行协调
             * @type {{data: boolean, feedbackLevel: boolean}}
             * @private
             */
            this._ajaxFlag = {
                data : false,
                feedbackLevel : false
            };
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
                this.dateGroup = 'yesterday';
                this.developer = authorizationService.isDeveloper();
                this.__callOutCountContributionColumnDefine = [{
                    dataId : 'student_name',
                    label : '学生姓名'
                }, {
                    dataId : 'student_mobile',
                    label : '联系号码'
                }, {
                    dataId : 'call_count',
                    label : '有效联系次数'
                }, {
                    dataId : 'call_count_total',
                    label : '联系总次数'
                }];
                this.__callOutLengthContributionColumnDefine = [{
                    dataId : 'student_name',
                    label : '学生姓名'
                }, {
                    dataId : 'student_mobile',
                    label : '联系号码'
                }, {
                    dataId : 'call_count',
                    label : '有效通话时长（单位分钟）'
                }, {
                    dataId : 'call_count_total',
                    label : '通话总时长（单位分钟）'
                }, {
                    dataId : 'count',
                    label : '有效联系次数'
                }, {
                    dataId : 'total',
                    label : '联系总次数'
                }];
                this.__intervalContributionColumnDefine = [{
                    dataId : 'hour',
                    label : '时段'
                }, {
                    dataId : 'call_count',
                    label : '有效拨出电话数'
                }, {
                    dataId : 'call_count_total',
                    label : '拨出电话总数'
                }, {
                    dataId : 'call_length',
                    label : '有效通话时长（单位分钟）'
                }, {
                    dataId : 'call_length_total',
                    label : '通话总时长（单位分钟）'
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
                    if (angular.isArray(self.campuses) && (self.campuses.length === 1)) {
                        self.selectedCampus = self.campuses[0];
                    }
                    if (campusesInCharge.length > 0) {
                        self.selectedCampus = campusesInCharge[0];
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
            this.__callOutChartConfigs = [{
                propertyName : 'callOut',
                axisConfigs : [{
                    label : '有效拨出次数',
                    propertyNameInCollection : 'call_count',
                    chartType : 'line',
                    defaultValue : 0
                }, {
                    label : '拨出总次数',
                    propertyNameInCollection : 'call_count_total',
                    chartType : 'bar',
                    defaultValue : 0
                }]
            }];

            this.__callLengthChartConfigs = [{
                propertyName : 'callOutLength',
                    axisConfigs : [{
                    label : '有效拨出时长',
                    propertyNameInCollection : 'call_count',
                    chartType : 'line',
                    defaultValue : 0
                }, {
                    label : '拨出总时长',
                    propertyNameInCollection : 'call_count_total',
                    chartType : 'bar',
                    defaultValue : 0
                }]
            }];

            this.__callRangeChartConfigs = [{
                propertyName : 'range',
                axisConfigs : [{
                    label : '有效拨出次数',
                    propertyNameInCollection : 'call_count',
                    chartType : 'line',
                    defaultValue : 0
                }, {
                    label : '拨出总次数',
                    propertyNameInCollection : 'call_count_total',
                    chartType : 'bar',
                    defaultValue : 0
                },{
                    label : '有效拨出时长',
                    propertyNameInCollection : 'call_length',
                    chartType : 'line',
                    defaultValue : 0
                },{
                    label : '拨出总时长',
                    propertyNameInCollection : 'call_length_total',
                    chartType : 'bar',
                    defaultValue : 0
                }]
            }];

            this.__teamCallOutChartConfigs = [{
                propertyName : 'callOut',
                axisConfigs : [{
                    label : '拨出人数',
                    propertyNameInCollection : 'data.person_call_number.length',
                    chartType : 'line',
                    defaultValue : 0
                }, {
                    label : '拨出次数',
                    propertyNameInCollection : 'data.callNumber',
                    chartType : 'line',
                    defaultValue : 0
                }, {
                    label : '拨出时长',
                    propertyNameInCollection : 'data.callLength',
                    chartType : 'line',
                    defaultValue : 0
                }]
            }];
            this.__teamCallOutRangeConfigs = [{
                propertyName : 'rangeStats',
                axisConfigs : [{
                    labelPropertyName: 'name',
                    propertyNameInCollection: 'callLength',
                    chartType: 'line',
                    defaultValue: 0
                }]
            }];

            this.feedbackLevelData = [];
            this.__feedbackLevelColumnDefine = [{
                dataId : 'feedback_level',
                label : '等级'
            }, {
                dataId : 'valid_calls_total',
                label : '有效联系次数'
            }, {
                dataId : 'valid_billable_total_minute',
                sortId : 'data.valid_billable_total',
                label : '有效通话时长（单位分钟）'
            }];

            this.processResult = function() {
                if (!self.__ajaxFlag.data || !self.__ajaxFlag.feedbackLevel){
                    return;
                }
                if (self.contributions.length > 0) {
                    //将反馈等级占比和原来的贡献度融合
                    if (angular.isArray(self.feedbackLevelData)) {
                        self.feedbackLevelData.forEach(function (value) {
                            var contributionEntry = com.doubeye.Utils.array.getObjectFromArray({
                                //id : !isNaN(value.userid) ? parseInt(value.userid) : value.userid
                                id : !isNaN(value.id) ? parseInt(value.id) : value.id
                            }, self.contributions);
                            if (contributionEntry) {
                                contributionEntry.feedbackLevel = value
                            }
                        });

                    }
                    if (!self.__range) {
                        self.__range = {};
                        var entry = self.contributions[0];
                        if (angular.isArray(entry.data.person_call_interval)) {
                            var index = 0;
                            entry.data.person_call_interval.forEach(function (range) {
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
                    self.noRangeRending = false;
                    self.renderChart(self.contributions[0]);
                }
            };
            self.setDisplayTeamStat = function(display) {
                self.displayTeamStat = display;
                self.showTeamButtonTitle = display ? '收起团队统计' : '显示团队统计';
            };
            this.getParameter = function () {
                var params = {
                    start_time : self.startTime,
                    end_time : self.endTime,
                    campus_id : self.selectedCampus.campusid
                };
                if (self.mode !== '个人') {
                    var index = 0;
                    if (angular.isArray(self.selectedCampus.team_list)) {
                        self.selectedCampus.team_list.forEach(function (team) {
                            params['team_num[' + index + ']'] = team.team_num;
                            index ++;
                        });
                    }
                }
                return params;
            };

            this.getData = function () {
                self.__ajaxFlag = {
                    data : false,
                    feedbackLevel : false
                };
                self.__callOutChartConfigs.forEach(function (entry) {
                    entry.maxDataCount = self.mode === '团队' ? 50 : null;
                });
                self.__callLengthChartConfigs.forEach(function (entry) {
                    entry.maxDataCount = self.mode === '团队' ? 50 : null;
                });
                self.currentUser = {};
                self.noRangeRending = true;
                self.contributions = [];
                self.team = null;
                self.setDisplayTeamStat(false);

                if (!self.selectedCampus) {
                    dialogService.alert('提示', '请选择校区');
                    return;
                }
                var url , params = self.getParameter();
                if (self.mode === '个人') {
                    url = self.adaDataUrl;
                } else {
                    url = self.teamDataUrl;
                }
                httpService.sendRequest(self, url, params, {
                    dataPropertyName : 'contributions',
                    resultPropertyName : 'data',
                    callback: function () {
                        self.__ajaxFlag.data = true;
                        self.processResult();
                    }
                }, {
                    errorMessage: '获取贡献度数据出错'
                }, {}, 'GET');
                httpService.sendRequest(self, self.feedbackLevelUrl, params, {
                    dataPropertyName : 'feedbackLevelData',
                    resultPropertyName : 'data',
                    callback: function () {
                        self.__ajaxFlag.feedbackLevel = true;
                        self.processResult();
                    }
                }, {
                    errorMessage: '获取贡献度数据出错'
                }, {}, 'GET');
            };

            this.onMobileClick = function(params) {
                var mobile = params.name;
                var url = '/dbUtils/main.html#!/localRecordBrowser?jump=1&startTime=' + self.startTime + ' 00:00:00&endTime=' + self.endTime + ' 23:59:59&student=' + mobile + self.__getUserPart() + '&campusId=' + self.selectedCampus.campusid;
                window.open(url, '_blank');
            };

            this.onRangeClick = function (params) {
                var date = com.doubeye.Utils.String.getStringBefore(self.startTime, ' ');
                var startTime = date + ' ' + params.name;
                var hour = parseInt(com.doubeye.Utils.String.getStringBefore(params.name, ':'), 10) + 1;
                if (hour < 10) {
                    hour = '0' + String(hour);
                }
                var endTime = date + ' ' + hour + ':00:00';
                var url = '/dbUtils/main.html#!/localRecordBrowser?jump=1&startTime=' + startTime + '&endTime=' + endTime + self.__getUserPart() + '&campusId=' + self.selectedCampus.campusid;
                window.open(url, '_blank');
            };

            this.renderChart = function (entry) {
                if (!self.noRangeRending) {
                    self.currentUser = entry;
                    if (self.currentUser.data) {
                        com.doubeye.Utils.chart.processLineChartData(self.currentUser.data.person_call_number, self.currentUser, 'student_mobile', self.__callOutChartConfigs);
                        com.doubeye.Utils.chart.processLineChartData(self.currentUser.data.person_call_length, self.currentUser, 'student_mobile', self.__callLengthChartConfigs);
                        com.doubeye.Utils.chart.processLineChartData(self.currentUser.data.person_call_interval, self.currentUser, 'hour', self.__callRangeChartConfigs);
                        if (self.mode === '团队') {
                            dialogService.toast('为了提高显示速度，图中仅显示了前50个电话');
                        }
                    }
                    //绘制等级饼图
                    com.doubeye.Utils.chart.processPieData(self.currentUser.feedbackLevel.data, self.currentUser, 'callCountPie', 'valid_calls_total', 'feedback_level', true);
                    com.doubeye.Utils.chart.processPieData(self.currentUser.feedbackLevel.data, self.currentUser, 'callLengthPie', 'valid_billable_total_minute', 'feedback_level', true);
                    /* 帮助测试查看数据是否能对上，如果需要则打开注释
                    if (self.developer) {
                        self.__debug = {
                            c: {totalCallCountValid: 0, totalCallLengthValid: 0},
                            d: {totalCallCountValid: 0, totalCallLengthValid: 0}
                        };
                        var call = 0, length = 0;
                        if (angular.isArray(self.currentUser.data.person_call_length)) {
                            self.currentUser.data.person_call_length.forEach(function (entry) {
                                call += entry.count;
                                length += entry.call_count_second;
                            });
                            self.__debug.c.totalCallCountValid = call;
                            self.__debug.c.totalCallLengthValid = length;
                        }
                        call = 0;
                        length = 0;
                        if (angular.isArray(self.currentUser.feedbackLevel.data)) {
                            self.currentUser.feedbackLevel.data.forEach(function (entry) {
                                call += entry.valid_calls_total;
                                length += entry.valid_billable_total
                            });
                            self.__debug.d.totalCallCountValid = call;
                            self.__debug.d.totalCallLengthValid = length;
                        }
                    }
                    */
                }
            };

            this.__getUserPart = function () {
                var userPart = '';
                if (self.mode === '个人') {
                    userPart = '&ada=' + self.currentUser.id
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
                        if (angular.isArray(user.data.person_call_length)) {
                            var callLength = 0;
                            user.data.person_call_length.forEach(function (call) {
                                callLength += parseFloat(call.call_count_second);
                            });
                            user.data.callLength = Math.round(callLength * 100 / 60) / 100;
                        }
                        if (angular.isArray(user.data.person_call_number)) {
                            var callNumber = 0;
                            user.data.person_call_number.forEach(function (call) {
                                callNumber += parseInt(call.call_count, 10);
                            });
                            user.data.callNumber = callNumber;
                        }

                        //计算时段团队统计
                        if (angular.isArray(user.data.person_call_interval)) {
                            user.data.person_call_interval.forEach(function (call) {
                                rangeStats[call.hour].push({
                                    name : user.name,
                                    callLength : call.call_length
                                });
                            });
                        }

                    });
                    self.team = {

                    };
                    rangeStats = com.doubeye.Utils.objectRefactor.objectToArray(rangeStats, 'range');
                    com.doubeye.Utils.chart.processLineChartData(self.contributions, self.team, 'name', self.__teamCallOutChartConfigs);
                    com.doubeye.Utils.chart.processLineChartDataWithDynaticValueProperty(rangeStats, self.team, 'range', null, self.__teamCallOutRangeConfigs);
                }
            }
        }]
});
