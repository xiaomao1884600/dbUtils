/**
 * 报名趋势分析
 */
angular.module('enrollTrend', []).component('enrollTrend', {
    templateUrl: 'app/views/dataMining/edu/enrollment/trend/enrollTrend.html',
    controller: ['httpService', '$cookies', 'dialogService', 'authorizationService',
        function enrollTrendController(httpService, $cookies, dialogService, authorizationService) {
            var self = this;
            this.dataUrl = 'http://record.hxsd.local/api/record/report/get_enrollment_trend';
            this.user = authorizationService.getUser();
            this.chartHeight = (window.innerHeight - 100) / 2 - 20;
            this.chartWidth = (window.innerWidth - 300) - 10;
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
                    if (self.campuses.length === 1) {
                        self.selectedCampus = self.campuses[0];
                    }
                });
                this.__enrollCountChartConfig = [{
                    propertyName : 'enrollCount',
                    axisConfigs : [{
                        labelPropertyName: 'name',
                        propertyNameInCollection: 'enrollment_total',
                        chartType: 'bar',
                        stack : 'value',
                        defaultValue: 0
                    }]
                }];
                this.__salesChartConfig = [{
                    propertyName : 'sales',
                    axisConfigs : [{
                        labelPropertyName: 'name',
                        propertyNameInCollection: 'receivabletuition_total',
                        chartType: 'bar',
                        stack : 'value',
                        defaultValue: 0
                    }]
                }];
            };
            /**
             * 趋势数据
             * @type {Array}
             */
            this.treadData = [];

            this.processResult = function(results) {
                if (!self.selectedTeam || self.selectedTeam === '-1') {
                    results.forEach(function (dateData) {
                        if (angular.isArray(dateData.data)) {
                            dateData.data.forEach(function (data) {
                                data.name = data.username;
                            });
                        }
                    });
                }
                com.doubeye.Utils.chart.processLineChartDataWithDynaticValueProperty(self.treadData, self, 'date', 'data', self.__enrollCountChartConfig);
                console.log(self.enrollCount);
                com.doubeye.Utils.chart.processLineChartDataWithDynaticValueProperty(self.treadData, self, 'date', 'data', self.__salesChartConfig);
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
                    var teamLeaderName = params.seriesName;
                    var team = com.doubeye.Utils.array.getObjectFromArray({username : teamLeaderName}, self.selectedCampus.team_list);
                    if (team) {
                        self.selectedTeam = team.team_num;
                        self.getData();
                    }

                }
            };
        }]
});
