/**
 * 电话数据趋势分析
 */
angular.module('callOutAvgAnalyze', []).component('callOutAvgAnalyze', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/report/callOutAvgAnalyze.html',
    controller: ['httpService', '$cookies', 'dialogService', 'authorizationService', '$mdDialog',
        function callOutAvgAnalyzeController(httpService, $cookies, dialogService, authorizationService, $mdDialog) {
            var self = this;
            //以校区为条件查询所有团队
            this.teamDataUrl = 'http://record.hxsd.local/api/record/report/get_team_work';
            //以校区为条件查询指定一个团队
            this.teamDetailDataUrl = 'http://record.hxsd.local/api/record/report/get_team_user_work';
            //以校区团队和成员查询一个指定的人
            this.adaDetailDataUrl = 'http://record.hxsd.local/api/record/report/get_team_user_record';
            this.ossPrefix = 'http://hxsd-backup.oss-cn-beijing.aliyuncs.com/';
            this.user = authorizationService.getUser();
            this.developer = authorizationService.isDeveloper();
            this.$onInit = function () {
                this.dataGroup = 'yesterday';
                this.campuses = [];
                this.teamsUnderCampus = [];
                this.minValidCallOut = 8;
                this.minValidCallLength = 28;
                var now = new Date();
                this.endTime = now.format('yyyy-MM-dd');
                this.startTime = new Date(new Date().setDate(now.getDate() - 6)).format('yyyy-MM-dd');
                authorizationService.getChargeInfo(function (allCampus) {
                    self.campuses = allCampus;
                    if (self.campuses.length === 1) {
                        self.selectedCampus = self.campuses[0];
                    }
                    self.campuses.splice(0, 0, {
                        campusid : 0,
                        name : '所有校区'
                    });
                    self.teamsUnderCampus.push(authorizationService.getAllTeamObject());
                    self.selectedTeam = authorizationService.getAllTeamObject();
                });
                this.results = [];
                this.results.push({
                    name : '全部',
                    url : this.teamDataUrl,
                    type : 'all'
                });
                this.__teamDefines = [{
                    dataId : 'campus_title',
                    label : '校区'
                }, {
                    //dataId : 'team_title',
                    dataId : 'username',
                    label : '团队'
                }, {
                    dataId : 'record.avg_valid_calls_total',
                    label : '日均有效拨出电话数'
                }, {
                    //dataId : 'record.avg_billable_total_title',
                    dataId : 'record.avg_valid_billable_total',
                    label : '日均有效拨出时长（分钟）',
                    value : function (value) {
                        return Math.round(value / 60 * 100) / 100;
                    },
                    sortDataId : 'record.avg_valid_billable_total'
                }];

                this.__teamDetailDefines = [{
                    dataId : 'username',
                    label : 'ada'
                },{
                    dataId : 'record.avg_valid_calls_total',
                    label : '日均有效拨出电话数'
                }, {
                    //dataId : 'record.avg_billable_total_title',
                    dataId : 'record.avg_valid_billable_total',
                    label : '日均有效拨出时长（分钟）',
                    value : function (value) {
                        return Math.round(value / 60 * 100) / 100;
                    },
                    sortDataId : 'record.avg_billable_total'
                }, {
                    dataId : 'record.work_days',
                    label : '出勤天数'
                }, {
                    dataId : 'record.valid_calls_total',
                    label : '有效总拨出电话数'
                }, {
                    //dataId : 'record.billable_total_title',
                    dataId : 'record.valid_billable_total',
                    label : '有效总拨出电话时长（分钟）',
                    value : function (value) {
                        return Math.round(value / 60 * 100) / 100;
                    },
                    sortDataId : 'record.valid_billable_total'
                }, {
                    dataId : 'record.calls_total',
                    label : '总拨出电话数'
                }, {
                    //dataId : 'record.billable_total_title',
                    dataId : 'record.billable_total',
                    label : '总拨出电话时长（分钟）',
                    value : function (value) {
                        return Math.round(value / 60 * 100) / 100;
                    },
                    sortDataId : 'record.billable_total'
                }];

                this.__adaDetailDefines = [{
                    dataId : 'username',
                    label : 'ada'
                },{
                    dataId : 'datetime',
                    label : '通话时间'
                }, {
                    dataId : 'studentname',
                    label : '学生姓名'
                }, {
                    dataId : 'student_mobile',
                    label : '电话'
                }, {
                    dataId : 'billable_title',
                    label : '通话时长',
                    sortDataId : 'billable'
                }, {
                    dataId : 'no_work',
                    label : '当天是否出勤',
                    backgroundDataId : 'noWorkColor'
                }, {
                    dataId : 'valid',
                    label : '电话是否有效',
                    backgroundDataId : 'validColor'
                }];
                this.__operations = [{
                    id : 'detail',
                    text : '详情',
                    callback : self.drillDown
                }];
            };

            /**
             * 趋势数据
             * @type {Array}
             */
            this.data = [];

            this.__getCommonParameters = function () {
                return {
                    start_time: self.startTime,
                    end_time: self.endTime,
                    calls_benchmark : self.minValidCallOut,
                    billable_benchmark : self.minValidCallLength * 60
                };
            };
            /**
             * 获得查询参数
             * @private
             */
            this.__getParameters = function () {
                var parameters = self.__getCommonParameters();
                var campusIds = [];
                if (!self.selectedCampus || self.selectedCampus.campusid === 0) {
                    self.campuses.forEach(function (campus) {
                        if (campus.campusid > 0) {
                            campusIds.push(campus.campusid);
                        }
                    });
                } else {
                    campusIds.push(self.selectedCampus.campusid);
                }
                parameters.campus_id = campusIds;
                return parameters;
            };


            this.getData = function (tab, parameters) {
                if (!tab) {
                    tab = self.results[0];
                    if (self.results.length > 1) {
                        self.results.splice(1, self.results.length);
                    }
                }
                var params = parameters ? parameters : self.__getParameters();
                httpService.sendRequest(tab, tab.url, params, {
                    dataPropertyName : 'data',
                    resultPropertyName : 'data',
                    callback: function (data, customParams, totalRecords) {
                        self.selectedIndex = self.results.length - 1;
                    }
                }, {
                    errorMessage: '获取趋势数据出错'
                });
            };

            /*
            this.onCampusChanged = function () {
                if (self.selectedCampus) {
                    self.teamsUnderCampus = authorizationService.getAllTeamInChargeUnderCampus(self.campuses, self.selectedCampus.campusid, true);
                    self.selectedTeam = authorizationService.getAllTeamObject();
                }
            };
            */

            
            this.remove = function (tab) {
                var index = self.results.indexOf(tab);
                self.results.splice(index, 1);
            };

            self.getDataDetail = function (start, end, page) {
                var tab = self.results[self.selectedIndex];
                var parameters = self.__getCommonParameters();
                parameters.campus_id = tab.campus_id;
                parameters.team_num = tab.team_num;
                parameters.userid = tab.userId;
                parameters.index = page;
                parameters.size = 20;
                httpService.sendRequest(tab, tab.url, parameters, {
                    dataPropertyName : 'data',
                    resultPropertyName : 'data.DATA',
                    resultRecordCountPropertyName : 'data.TOTAL_RECORDS',
                    callback: function (data, customParams, totalRecords) {
                        data.forEach(function (record) {
                            if (record.no_work === 1) {
                                record.noWorkColor = '#f6a828';
                            }
                            record.no_work = record.no_work !== 1 ? '是' : '否';
                            if (record.valid === 0) {
                                record.validColor = '#f6a828';
                            }
                            record.valid = record.valid === 1 ? '是' : '否';
                        });

                        if (totalRecords > 0) {
                            tab.currentPage = Math.ceil((start + 1) / tab.recordPerPage);
                        } else {
                            tab.currentPage = 0;
                        }
                        tab.totalPage = Math.ceil(totalRecords / tab.recordPerPage);
                    }
                }, {
                    errorMessage: '获取趋势数据出错'
                });
            };

            this.drillDown = function (record) {
                self.currentRecord = record;
                var currentTab = self.results[self.selectedIndex];
                var newTab, parameters = self.__getCommonParameters();
                if (currentTab.type === 'all') {
                    newTab = {
                        type : 'team',
                        url : self.teamDetailDataUrl,
                        // name : record.team_title
                        name : '(' + record.username + ')'
                    };
                    parameters.campus_id = record.campus_id;
                    parameters.team_num = record.team_num;

                } else if (currentTab.type === 'team'){
                    newTab = {
                        type : 'ada',
                        url : self.adaDetailDataUrl,
                        name : record.username,
                        userId : record.userid,
                        campusId : record.campus_id,
                        team_num : record.team_num,
                        currentPage : 1,
                        recordPerPage : 20
                    };
                    self.results.push(newTab);
                    self.selectedIndex = self.results.length - 1;
                    self.getDataDetail(1);
                    return;
                } else if (currentTab.type === 'ada') {
                    record.ossPath = self.ossPrefix + record.oss_path;
                    $mdDialog.show({
                        contentElement: '#detailBrowser',
                        clickOutsideToClose: true
                    });
                    return;
                } else {
                    //都不符和，则不做查询，在控制台中输出警告
                    console.warn(currentTab);
                    return;
                }
                self.results.push(newTab);
                self.getData(newTab, parameters);
            };
        }]
});
