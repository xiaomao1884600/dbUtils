/**
 * 电话数据趋势分析
 */
angular.module('assignByDayAnalyze', []).component('assignByDayAnalyze', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/report/assignByDayAnalyze.html',
    controller: ['httpService', '$cookies', 'dialogService', 'authorizationService', '$mdDialog',
        function assignByDayAnalyzeController(httpService, $cookies, dialogService, authorizationService, $mdDialog) {
            var self = this;
            this.user = authorizationService.getUser();
            this.developer = authorizationService.isDeveloper();
            this.$onInit = function () {
                this.dateGroup = 'lastDay7';
                this.campuses = [];
                this.teamDataUrl = "http://record.hxsd.local/api/record/report/get_assign_trend";
                //在线分配（包括在线建表，入学辅导建表，时长建表），ada自检表，跨校区转分
                this.interestingAssignType = ['zx_total' , 'ada_total', 'xxzf_total'];
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
                    dataId : 'assortTotal',
                    label : '合计接量数'
                }, {
                    dataId : 'assortAvg',
                    value : function(value, record) {
                        return !record.dayCount ? 0 : Math.round(record.assortTotal * 100 / record.dayCount) / 100;
                    },
                    label : '平均每日接量数'
                }];
                this.__teamDetailDefines = [{
                    dataId : 'username',
                    label : '团队'
                }, {
                    dataId : 'username',
                    label : 'ada'
                }, {
                    dataId : 'assortTotal',
                    label : '合计接量数'
                }, {
                    dataId : 'assortAvg',
                    value : function(value, record) {
                        return !record.dayCount ? 0 : Math.round(record.assortTotal * 100 / record.dayCount) / 100;
                    },
                    label : '平均每日接量数'
                }];
                this.__adaDetailDefines = [{
                    dataId : 'date',
                    label : '日期'
                }, {
                    dataId : 'assort.zx_total',
                    label : '在线分配'
                }, {
                    dataId : 'assort.ada_total',
                    label : '自建表'
                }, {
                    dataId : 'assort.xxzf_total',
                    label : '跨校区转分（转入）'
                }, {
                    dataId : 'recover.xxzf_total',
                    label : '跨校区转分（转出）'
                }, {
                    dataId : 'assortTotal',
                    label : '合计'
                }];
                this.__operations = [{
                    id : 'detail',
                    text : '详情',
                    callback : self.drillDown
                }];
                this.processResult = function(results, tab) {
                    //循环每一天
                    results.forEach(function (dateData) {
                        if (angular.isArray(dateData.data)) {
                            /**
                             * 计算每一天的入手量、掉量、报名量的总量
                             * @type {number}
                             */
                            var assort = 0, recover = 0, enrollment = 0;
                            //循环每个团队或每个成员
                            dateData.data.forEach(function (data) {
                                //计算每一天的总数
                                data.assortTotal = com.doubeye.Utils.objectRefactor.sumFields(data.assort, self.interestingAssignType);
                                assort += data.assortTotal;
                            });
                            dateData.assortTotal = assort;
                        }
                    });
                    //将日期，团队二维数组调整为团队为团队一维数组，并对分配和掉量加和
                    var gridData = [];
                    results.forEach(function (dateData) {
                        if (angular.isArray(dateData.data)) {
                            //循环每个团队或每个成员
                            dateData.data.forEach(function (data) {
                                var recordData;
                                if (tab.type === 'all') {
                                    recordData = com.doubeye.Utils.array.getObjectFromArray({team_num : data.team_num}, gridData);
                                } else if (tab.type === 'team') {
                                    recordData = com.doubeye.Utils.array.getObjectFromArray({userid : data.id}, gridData);
                                }
                                if (!recordData) {
                                    recordData = {
                                        team_num : data.team_num,
                                        name : data.username,
                                        assortTotal : data.assortTotal,
                                        username : data.username,
                                        campus_id : data.campus_id,
                                        campus_title : data.campus_title,
                                        dayCount : data.assortTotal ? 1 : 0
                                    };
                                    if (tab.type === 'team') {
                                        recordData.userid = data.id;
                                    }
                                    gridData.push(recordData);
                                } else {
                                    recordData.assortTotal += data.assortTotal;
                                    if (data.assortTotal) {
                                        recordData.dayCount ++;
                                    }
                                }
                            });
                        }
                    });
                    if (tab) {
                        tab.data = gridData;
                        tab.originData = results;
                    }
                };
                /**
                 * 获得查询参数
                 * @private
                 */
                self.__getParameters = function (tab) {
                    var parameters = {
                        start_time: self.startTime,
                        end_time: self.endTime
                    };
                    if (self.selectedCampus) {
                        parameters.campus_id = self.selectedCampus.campusid;
                    }
                    if (tab.type === 'team') {
                        var campus = com.doubeye.Utils.array.getObjectFromArray({campusid : tab.campus_id}, self.campuses);
                        var teamIds = authorizationService.getAllTeamMemberIdsByCampusIdAndTeamId(campus, tab.team_num);
                        parameters.ada_ids = com.doubeye.Utils.array.toString(teamIds);
                    } else if (tab.type === 'ada'){
                        parameters.userid = tab.userid;
                    }
                    return parameters;
                };

                this.getData = function (tab) {
                    if (!tab) {
                        tab = self.tabs[0];
                        if (self.tabs.length > 1) {
                            self.tabs.splice(1, self.tabs.length);
                        }
                    }
                    var parameters = self.__getParameters(tab);
                    httpService.sendRequest(self, self.teamDataUrl, parameters, {
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

            this.showAdaDetail = function (tab , data, userid) {
                var result = [];
                if (angular.isArray(data)) {
                    data.forEach(function (entry) {
                        var element = {date : entry.date};
                        var userDataEntry = com.doubeye.Utils.array.getObjectFromArray({id : userid}, entry.data);
                        if (userDataEntry) {
                            com.doubeye.Utils.objectRefactor.merge(element, userDataEntry);
                            result.push(element);
                        }

                    });
                }
                tab.data = result;
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
                        campus_id : record.campus_id
                    };
                } else if (currentTab.type === 'team'){
                    newTab = {
                        type : 'ada',
                        name : record.username,
                        userId : record.userid,
                        campusId : record.campus_id,
                        team_num : record.team_num
                    };
                    self.showAdaDetail(newTab, currentTab.originData, record.userid);
                    self.tabs.push(newTab);
                    self.selectedIndex = self.tabs.length - 1;
                    return;
                } else if (currentTab.type === 'ada') {
                    return;
                }
                self.tabs.push(newTab);
                self.selectedIndex = self.tabs.length - 1;
                self.getData(newTab);
            };
            this.remove = function (tab) {
                var index = self.tabs.indexOf(tab);
                self.tabs.splice(index, 1);
            };
        }]
});
