/**
 * 报名趋势分析
 */
angular.module('assignmentTrend', []).component('assignmentTrend', {
    templateUrl: 'app/views/dataMining/edu/assignment/trend/assignmentTrend.html',
    controller: ['httpService', '$cookies', 'dialogService', 'authorizationService', '$timeout',
        function assignmentTrend(httpService, $cookies, dialogService, authorizationService, $timeout) {
            var self = this;
            this.dataUrl = 'http://record.hxsd.local/api/record/report/get_assign_trend';
            this.user = authorizationService.getUser();
            /*
            计算图标大小，趋势柱状图横向铺满，纵向减去饼图的大小
            饼图为正方向，宽高为可用区域的1/3
             */
            this.chartWidth = (window.innerWidth - 300) - 10;
            //this.pieChartWidth = Math.floor(this.chartWidth / 3);
            this.pieChartHeight = Math.floor(this.chartWidth / 3);
            this.chartHeight = (window.innerHeight - 100 - this.pieChartHeight);


            this.developer = authorizationService.isDeveloper();
            this.selectedTeam = null;
            this.$onInit = function () {
                this.dateGroup = 'lastDay7';
                this.campuses = [];
                this.teamsUnderCampus = [];
                authorizationService.getChargeInfo(function (allCampus) {
                    self.campuses = allCampus;
                    self.teamsUnderCampus.push(authorizationService.getAllTeamObject());
                    self.selectedTeam = authorizationService.getAllTeamObject().team_num;
                    if (self.campuses.length > 0) {
                        self.selectedCampus = self.campuses[0];
                        self.onCampusChanged();
                    }
                });
                this.__chartConfig = [{
                    propertyName : 'trendData',
                    axisConfigs : [{
                        label: '新增持有量',
                        propertyNameInCollection: 'assortTotal',
                        chartType: 'bar',
                        defaultValue: 0
                    }, {
                        label: '掉量',
                        propertyNameInCollection: 'recoverTotal',
                        chartType: 'bar',
                        defaultValue: 0
                    }, {
                        label: '报名量',
                        propertyNameInCollection: 'enrollmentTotal',
                        chartType: 'bar',
                        defaultValue: 0
                    }]
                }];
                this.__assortDetailChartConfig = [{
                    labelPropertyName : 'name',
                    chartName : '团队/成员',
                    dataPropertyName : 'assortTotal',
                    skip0 : true,
                    children : {
                        chartConfigs : [{
                            label : '在线分配',
                            dataPropertyName : 'assort.zx_total',
                            skip0 : true
                        }, {
                        label : '指定分配',
                        dataPropertyName : 'assort.zd_total',
                        skip0 : true
                    }, {
                        label : '跨校区转分',
                        dataPropertyName : 'assort.xxzf_total',
                        skip0 : true
                    }, {
                        label : '自建表',
                        dataPropertyName : 'assort.ada_total',
                        skip0 : true
                    }, {
                        label : '旧量',
                        dataPropertyName : 'assort.old_total',
                        skip0 : true
                    }],
                    chartName : '来源'
                    }
                }];
                this.__recoverSubTypeConfig = {
                    chartName : '来源',
                    chartConfigs : [{
                        label : '在线分配',
                        dataPropertyName : 'zx_total',
                        skip0 : true
                    }, {
                        label : '指定分配',
                        dataPropertyName : 'zd_total',
                        skip0 : true
                    }, {
                        label : '跨校区转分',
                        dataPropertyName : 'xxzf_total',
                        skip0 : true
                    }, {
                        label : '自建表',
                        dataPropertyName : 'ada_total',
                        skip0 : true
                    }, {
                        label : '旧量',
                        dataPropertyName : 'old_total',
                        skip0 : true
                    }]
                };
                this.__getRecoverSubTypeConfig = function (prefix) {
                    var result = angular.copy(self.__recoverSubTypeConfig);
                    result.chartConfigs.forEach(function (value) {
                        value.dataPropertyName = prefix + value.dataPropertyName;
                    });
                    return result;
                };
                this.__recoverDetailChartConfig = [{
                    labelPropertyName : 'name',
                    chartName : '团队/成员',
                    dataPropertyName : 'recoverTotal',
                    skip0 : true,
                    children : {
                        chartConfigs : [{
                            label : '30天',
                            dataPropertyName : 'recover.nofeedback_total',
                            skip0 : true,
                            children : self.__getRecoverSubTypeConfig('recover_assort.nofeedback.')
                        }, {
                            label : '48小时',
                            dataPropertyName : 'recover.level_total',
                            skip0 : true,
                            children : self.__getRecoverSubTypeConfig('recover_assort.level.')
                        }, {
                            label : '指定转分',
                            dataPropertyName : 'recover.zd_total',
                            skip0 : true,
                            children : self.__getRecoverSubTypeConfig('recover_assort.zd.')
                        }, {
                            label : '跨校区转分',
                            dataPropertyName : 'recover.xxzf_total',
                            skip0 : true,
                            children : self.__getRecoverSubTypeConfig('recover_assort.xxzf.')
                        }],
                        chartName : '掉量类型'
                    }
                }]
            };
            /**
             * 趋势数据
             * @type {Array}
             */
            this.data = [];

            this.processResult = function(results) {
                self.clearPieChart();
                results.forEach(function (dateData) {
                    if (angular.isArray(dateData.data)) {
                        /**
                         * 计算每一天的入手量、掉量、报名量的总量
                         * @type {number}
                         */
                        var assort = 0, recover = 0, enrollment = 0;
                        dateData.data.forEach(function (data) {
                            //计算每一天的总数
                            data.assortTotal = com.doubeye.Utils.objectRefactor.sumFields(data.assort);
                            data.recoverTotal = com.doubeye.Utils.objectRefactor.sumFields(data.recover);
                            data.enrollTotal = com.doubeye.Utils.objectRefactor.sumFields(data.enrollment);
                            assort += data.assortTotal;
                            recover += data.recoverTotal;
                            enrollment += data.enrollTotal;
                        });
                        dateData.assortTotal = assort;
                        dateData.recoverTotal = recover;
                        dateData.enrollmentTotal = enrollment;
                    }
                });
                if (results.length > 0) {
                    self.showDetail({
                        dataIndex : 0,
                        name : results[0].date
                    });
                }
                com.doubeye.Utils.chart.processLineChartData(self.data, self, 'date', self.__chartConfig);
                console.log(results);
            };
            /**
             * 获得查询参数
             * @private
             */
            self.__getParameters = function () {
                var parameters = {
                    start_time: self.startTime,
                    end_time: self.endTime,
                    campus_id: self.selectedCampus.campusid
                };
                if (!self.selectedTeam || self.selectedTeam === '-1') {
                    var selectedTeams = authorizationService.getAllTeamInChargeUnderCampus(self.campuses, self.selectedCampus.campusid, false), selectedTeamIds = '';
                    parameters.team_num = com.doubeye.Utils.array.toString(selectedTeams, 'team_num');
                } else {
                    var selectedTeamObject = com.doubeye.Utils.array.getObjectFromArray({team_num : self.selectedTeam}, self.selectedCampus.team_list);
                    var adas = authorizationService.getAllTeamMembersFromTeam(selectedTeamObject), adaIds = '';
                    parameters.ada_ids = com.doubeye.Utils.array.toString(adas, 'userid');
                }
                return parameters;
            };

            this.getData = function () {
                if (!self.selectedCampus) {
                    dialogService.toast('请先选择校区');
                    return;
                }
                var parameters = self.__getParameters();
                httpService.sendRequest(self, self.dataUrl, parameters, {
                    dataPropertyName : 'data',
                    resultPropertyName : 'data',
                    callback: self.processResult
                }, {
                    errorMessage: '获取趋势数据出错'
                }, {}, 'GET');
            };

            this.onCampusChanged = function () {
                if (self.selectedCampus) {
                    self.teamsUnderCampus = authorizationService.getAllTeamInChargeUnderCampus(self.campuses, self.selectedCampus.campusid, true);
                    self.selectedTeam = authorizationService.getAllTeamObject().team_num;
                }
            };

            this.onChartClick = function (params) {
                if (!self.selectedTeam || self.selectedTeam === '-1') {
                    var teamLeaderName = params.name;
                    var team = com.doubeye.Utils.array.getObjectFromArray({username : teamLeaderName}, self.selectedCampus.team_list);
                    if (team) {
                        self.selectedTeam = team.team_num;
                        self.getData();
                    }
                }
            };
            this.showDetail = function (params) {
                $timeout(function () {
                    self.clearPieChart();
                    self.currentDate = params.name;
                    self.assortDetailTitle = params.name + '新增量来源占比';
                    self.recoverDetailTitle = params.name + '掉量占比';
                    self.enrollDetailTitle = params.name + '报名占比';
                    if (self.data.length > 0) {
                        com.doubeye.Utils.chart.processMultiDimensionPieData(self.data[params.dataIndex].data, self, 'assortDetail', self.__assortDetailChartConfig, {});
                        //com.doubeye.Utils.chart.processPieData(self.data[params.dataIndex].data, self, 'recoverDetail', 'recoverTotal', 'name', true);
                        com.doubeye.Utils.chart.processMultiDimensionPieData(self.data[params.dataIndex].data, self, 'recoverDetail', self.__recoverDetailChartConfig, {});
                        console.log(self.recoverDetail);
                        com.doubeye.Utils.chart.processPieData(self.data[params.dataIndex].data, self, 'enrollDetail', 'enrollTotal', 'name', true);
                    }
                });
            };
            this.clearPieChart = function () {
                self.assortDetail = {};
                self.recoverDetail = {
                    legend : [],
                    data : []
                };
                self.enrollDetail = {
                    legend : [],
                    data : []};
            };
        }]
});
