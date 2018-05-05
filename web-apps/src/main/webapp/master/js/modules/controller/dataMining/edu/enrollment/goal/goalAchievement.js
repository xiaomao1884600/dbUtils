/**
 * Created by doubeye
 * 项目中用的组件模板
 * 注意！！！不要将此文件加入到main.html的js加载中
 */
angular.module('goalAchievement', []).component('goalAchievement', {
    templateUrl: 'app/views/dataMining/edu/enrollment/goal/goalAchievement.html',
    controller: ['httpService',
        function goalAchievementController(httpService) {
            var self = this;
            this.$onInit = function () {
                //this.termUrl = "http://record.hxsd.local/api/record/get_term";
                this.teamUrl = "http://record.hxsd.local/api/edu/get_term_team";
                this.__columnDefines = [{
                    dataId : 'manager_username',
                    label : '团队经理'
                }, {
                    dataId : 'ada_username',
                    label : 'ada'
                }, {
                    dataId : 'cnt',
                    label : '已确认入学数'
                }, {
                    dataId : 'goal',
                    label : '目标数'
                }, {
                    dataId : 'avgPerDay',
                    label : '平均每日入学数'
                }, {
                    dataId : 'tcpi',
                    label : '完工尚需绩效指数'
                }];
                /*
                this.getTerm = function () {
                    httpService.sendRequest(self, self.termUrl, {
                        start_term_id : 166
                    }, {
                        dataPropertyName : 'terms',
                        resultPropertyName : 'data',
                        callback : function (data) {
                            if (angular.isArray(self.terms)) {
                                for (var i = 0; i < self.terms.length; i ++) {
                                    if (self.terms[i].next === 1 || self.terms[i].termid === 167) {
                                        self.selectedTerm = self.terms[i];
                                        self.onTermChange();
                                        break;
                                    }
                                }
                            }
                        }
                    });
                };
                this.getTerm();
                */
                this.getTeam = function () {
                    httpService.sendRequest(self, self.teamUrl, {

                    }, {
                        resultPropertyName : 'data',
                        callback : function (data) {
                            self.teams = [{
                                manager_userid : 0,
                                manager_username : '所有'
                            }];
                            self.teams = self.teams.concat(data);
                        }
                    });
                };
                this.getTeam();
                this.onTermChange = function (selectedTerm) {
                    var termStartDate = new Date(selectedTerm.startdate);
                    self.endDate = new Date(termStartDate.setDate(termStartDate.getDate() + 5)).format('yyyy-MM-dd');
                    var today = new Date().format('yyyy-MM-dd');
                    var dayLeft = com.doubeye.Utils.dateTime.dayDiff(self.endDate, today);
                };
                this.results = [];
                this.getData = function () {
                    httpService.sendRequest(self, 'generalRouter', {
                        objectName : 'com.hxsd.services.productLine.e.enrollment.goal.EnrollmentGoalService',
                        action : 'getGoalAchievement',
                        term : self.selectedTerm.termid,
                        managerUserId : self.selectedTeam ? self.selectedTeam.manager_userid : 0
                    }, {
                        dataPropertyName : 'results',
                        callback : function (data) {
                            if (angular.isArray(data)) {
                                var today = new Date().format('yyyy-MM-dd');
                                var dayLeft = com.doubeye.Utils.dateTime.dayDiff(new Date(self.endDate).format('yyyy-MM-dd'), today), dayPast, totalDay;
                                var lastTerm = com.doubeye.Utils.array.getObjectFromArray({termid : self.selectedTerm.termid - 1}, self.terms);
                                if (lastTerm) {
                                    dayPast = com.doubeye.Utils.dateTime.dayDiff(today, lastTerm.startdate);
                                    totalDay = dayPast + dayLeft;
                                }
                                data.forEach(function (entry) {
                                    if (dayLeft > 0 && entry.goal >= entry.cnt) {
                                        entry.avgPerDay = Math.round((entry.goal - entry.cnt) * 100 / dayLeft) / 100;
                                        if (totalDay > 0) {
                                            entry.tcpi = Math.round((entry.goal - entry.cnt) * 100 / (entry.goal * dayLeft / totalDay)) / 100;
                                        }
                                    } else {
                                        entry.avgPerDay = 0;
                                        entry.tcpi = 0;
                                    }
                                });
                            }
                        }
                    });
                };
                this.onTermLoaded = function (terms) {
                    self.terms = terms;
                };
            }//END OF $onInit
        }]//EDN OF CONTROLLER
});
