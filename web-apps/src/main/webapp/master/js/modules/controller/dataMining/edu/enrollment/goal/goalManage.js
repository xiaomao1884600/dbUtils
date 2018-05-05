/**
 * Created by doubeye
 * ADA招生任务目标设置
 */
angular.module('goalManager', []).component('goalManager', {
    templateUrl: 'app/views/dataMining/edu/enrollment/goal/goalManage.html',
    controller: ['httpService',
        function goalManagerController(httpService) {
            var self = this;
            this.$onInit = function () {
                //this.termUrl = "http://record.hxsd.local/api/record/get_term";
                this.teamUrl = "http://record.hxsd.local/api/edu/get_term_team";
                this.dataUrl = "http://record.hxsd.local/api/edu/get_user_enrolled_goal";
                this.saveUrl = "http://record.hxsd.local/api/edu/set_user_enrolled_goal";
                /**
                 * 期数
                 * @type {Array}
                 */
                self.terms = [];
                self.selectedTerm = {};
                /**
                 * 团队
                 * @type {Array}
                 */
                self.teams = [];
                self.selectedTeam = [];
                /**
                 * 目标
                 * @type {Array}
                 */
                self.goals = [];
                this.__goalColumnDefines = [{
                    dataId : 'username',
                    label : 'ada'
                }, {
                    dataId : 'goal',
                    label : '目标',
                    html : '<input>'
                }];
                /*
                this.getTerm = function () {
                    httpService.sendRequest(self, self.termUrl, {
                        start_term_id : 167
                    }, {
                        dataPropertyName : 'terms',
                        resultPropertyName : 'data',
                        callback : function (data) {
                            if (angular.isArray(self.terms)) {
                                for (var i = 0; i < self.terms.length; i ++) {
                                    if (self.terms[i].next === 1 || self.terms[i].termid === 167) {
                                        self.selectedTerm = self.terms[i];
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
                                username : '所有'
                            }];
                            self.teams = self.teams.concat(data);
                        }
                    });
                };
                this.getTeam();
                this.save = function (record) {

                    var goals = [{
                        termid : self.selectedTerm.termid,
                        adaid : record.userid,
                        ada_username : record.ada_username,
                        enrolled_goal : record.enrolled_goal
                    }];
                    httpService.sendRequest(self, self.saveUrl, {
                        goals : goals,
                        _noProcessingDialog : true
                    }, {

                    });

                };
                this.getData = function () {
                    httpService.sendRequest(self, self.dataUrl, {
                        termid : self.selectedTerm.termid,
                        manager_userid : self.selectedTeam && self.selectedTeam.manager_userid > 0 ? self.selectedTeam.manager_userid : ''
                    }, {
                        dataPropertyName : 'goals',
                        resultPropertyName : 'data'
                    });
                };
            }//END OF $onInit
        }]//EDN OF CONTROLLER
});
