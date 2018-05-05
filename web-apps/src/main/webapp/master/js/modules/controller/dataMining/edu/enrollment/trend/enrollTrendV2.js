/**
 * 报名趋势分析
 */
angular.module('enrollTrendV2', []).component('enrollTrendV2', {
    templateUrl: 'app/views/dataMining/edu/enrollment/trend/enrollTrendV2.html',
    controller: ['httpService', '$cookies', 'dialogService', 'authorizationService', '$timeout',
        function enrollTrendV2Controller(httpService, $cookies, dialogService, authorizationService, $timeout) {
            var self = this;
            this.dataUrl = 'http://record.hxsd.local/api/record/report/get_enrollment_trend';
            this.user = authorizationService.getUser();
            this.chartWidth = (window.innerWidth - 300) - 10;
            this.chartHeight = (window.innerHeight - 100) / 2;
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
                this.__enrollCountChartConfig = [{
                    propertyName : 'enrollCount',
                    axisConfigs : [{
                        label: '总数',
                        propertyNameInCollection: 'enrollTotal',
                        chartType: 'bar',
                        stack : 'value',
                        defaultValue: 0
                    }]
                }];
                this.__salesChartConfig = [{
                    propertyName : 'sales',
                    axisConfigs : [{
                        label: '总数',
                        propertyNameInCollection: 'salesTotal',
                        chartType: 'bar',
                        stack : 'value',
                        defaultValue: 0
                    }]
                }];
                this.enrollDetailTitle = '报名占比';
                this.enrollSalesTitle = '班级定价总和占比';
            };
            /**
             * 趋势数据
             * @type {Array}
             */
            this.treadData = [];

            this.processResult = function(results) {
                self.clearPieChart();
                results.forEach(function (dateData) {
                    if (angular.isArray(dateData.data)) {
                        var enrollTotal = 0, salesTotal = 0;
                        dateData.data.forEach(function (data) {
                            if (!self.selectedTeam || self.selectedTeam === '-1') {
                                data.name = data.username;
                            }
                            //计算每一天的总数
                            enrollTotal += data.enrollment_total;
                            salesTotal += data.receivabletuition_total;
                        });
                        dateData.enrollTotal = enrollTotal;
                        dateData.salesTotal = salesTotal;
                    }
                });
                if (results.length > 0) {
                    self.showDetail({
                        dataIndex : 0,
                        name : results[0].date
                    });
                }

                com.doubeye.Utils.chart.processLineChartData(self.treadData, self, 'date', self.__enrollCountChartConfig);
                com.doubeye.Utils.chart.processLineChartData(self.treadData, self, 'date', self.__salesChartConfig);
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
                    dataPropertyName : 'treadData',
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
                    self.enrollDetailTitle = params.name + '报名占比';
                    self.enrollSalesTitle = params.name + '班级定价总和占比';
                    if (self.treadData.length > 0) {
                        com.doubeye.Utils.chart.processPieData(self.treadData[params.dataIndex].data, self, 'enrollDetail', 'enrollment_total', 'name', true);
                        com.doubeye.Utils.chart.processPieData(self.treadData[params.dataIndex].data, self, 'salesDetail', 'clazzpricing_total', 'name', true);
                    }
                });
            };
            this.clearPieChart = function () {
                self.salesDetail = {};
                self.enrollDetail = {};
            };
        }]
});
