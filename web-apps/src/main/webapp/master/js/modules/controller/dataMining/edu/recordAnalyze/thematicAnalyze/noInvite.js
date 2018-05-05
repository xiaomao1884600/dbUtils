/**
 * Created by doubeye
 * 查找潜在邀约动作不足的主题分析
 */
angular.module('noInvite', []).component('noInvite', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/thematicAnalyze/noInvite.html',
    controller: ['httpService', 'dialogService',
        function noInviteController(httpService, dialogService) {
            var self = this;
            this.$onInit = function () {
                //this.campusTeamUserUrl = 'http://record.hxsd.local/api/record/get_campus_team';
                this.dataUrl = 'http://record.hxsd.local/api/edu/get_ada_student';
                this.recordPerPage = 20;
                this.assignDays = 7;
                //以下部分代码与localRecordBrowser代码相同，考虑将其合并 TODO
                /*
                this.processUserUnderCampus = function(campus) {
                    var users = [];
                    if (angular.isArray(campus.team_list)) {
                        campus.team_list.forEach(function (team) {
                            if (angular.isArray(team.team_user_list)) {
                                team.team_user_list.forEach(function (user) {
                                    var teamUser = {
                                        userid : user.userid,
                                        username : user.username
                                    };
                                    users.push(teamUser);
                                });
                            }
                        });
                    }
                    return users;
                };
                this.processCampusAndTeamUsers = function (data) {
                    self.campus = [];
                    self.teamUsers = [];
                    self.teamUsersUnderCampus = [];
                    data.forEach(function (campus) {
                        self.campus.push({
                            campusId : campus.campusid,
                            name : campus.name,
                            team_list : campus.team_list
                        });
                        var users = self.processUserUnderCampus(campus);
                        self.teamUsers = self.teamUsers.concat(users);
                        self.teamUsersUnderCampus = self.teamUsersUnderCampus.concat(users);
                    });
                    self.campus.splice(0, 0, {
                        campusId: 0,
                        name: '所有校区'
                    });
                    self.teamUsersUnderCampus.splice(0, 0, {
                        userid: 0,
                        username: '所有负责人'
                    });
                    if (self.jump === '1') {
                        if ($location.search().ada) {
                            self.ada = $location.search().ada;
                            self.selectedTeamUser = com.doubeye.Utils.array.getObjectFromArray({userid: self.ada}, self.teamUsers);
                            self.selectedCampus = self.campus[0];
                        }
                        self.getData(1);
                    }
                };
                this.getAuthority = function () {
                    httpService.sendRequest(self, this.campusTeamUserUrl, {
                        campus_id : self.campusInCharge
                    }, {
                        resultPropertyName : 'data',
                        callback : self.processCampusAndTeamUsers
                    }, {
                        errorMessage : '获取用户校区、负责ADA出错，'
                    });
                };

                this.getAuthority();

                this.onCampusChanged = function () {
                    if (self.selectedCampus && self.selectedCampus.campusId > 0) {
                        self.teamUsersUnderCampus = self.processUserUnderCampus(self.selectedCampus);
                    } else {
                        self.teamUsersUnderCampus = self.teamUsers;
                    }
                    self.teamUsersUnderCampus.splice(0, 0, {
                        userid: 0,
                        username: '所有负责人'
                    });
                    self.selectedTeamUser = null;
                };
                this.query = function (userName) {
                    if (userName) {
                        if (angular.isArray(self.teamUsersUnderCampus)) {
                            var result = [];
                            self.teamUsersUnderCampus.forEach(function (user) {
                                if (user.username.indexOf(userName) >= 0) {
                                    result.push(user);
                                }
                            });
                            return result;
                        }
                    } else {
                        return self.teamUsersUnderCampus;
                    }
                };
                */
                //以上部分代码与localRecordBrower代码相同，考虑将其合并 END TODO

                this.data = [];

                this.__columnDefines = [{
                    dataId : 'campus_title',
                    label : '校区'
                }, {
                    dataId : 'username',
                    label : 'ADA'
                }, {
                   dataId : 'studentname',
                   label : '学生姓名'
                }, {
                    dataId : 'mobile',
                    label : '学生手机号'
                }, {
                    dataId : 'feedback_level',
                    label : '咨询等级'
                }, {
                    dataId : 'ecdateline',
                    label : '负责开始时间'
                }];
                this.showRecords = function (record) {
                    var startTime = record.ecdateline;
                    var endTime = (new Date()).format('yyyy-MM-dd HH:mm:ss');
                    var url = '/dbUtils/main.html#!/localRecordBrowser?jump=1&startTime=' + startTime + '&endTime=' + endTime + '&ada=' + record.userid + '&student=' + record.mobile;
                    window.open(url, '_blank');
                };
                this.__operations = [{
                    id : 'detail',
                    text : '查询对话',
                    callback : self.showRecords
                }];
                this.doSearch = function (start, end, page) {
                    if (!self.selectedCampus || self.selectedCampus.campusId === 0) {
                        dialogService.toast('请先选择校区');
                        return;
                    }
                    httpService.sendRequest(self, self.dataUrl, {
                        campus_id : self.selectedCampus.campusId,
                        adaid : self.selectedTeamUser ? self.selectedTeamUser.userid : '',
                        assignDays : self.assignDays,
                        index : page,
                        size : self.recordPerPage
                    }, {
                        dataPropertyName : 'data',
                        resultPropertyName : 'data.DATA',
                        resultRecordCountPropertyName : 'data.TOTAL_RECORDS',
                        callback : function (data, customParams, totalRecords) {
                            self.totalRecords = totalRecords;
                            if (totalRecords > 0) {
                                self.currentPage = Math.ceil((start + 1) / self.recordPerPage);
                            } else {
                                self.currentPage = 0;
                            }
                            self.totalPage = Math.ceil(totalRecords / self.recordPerPage);
                        }
                    }, {
                        errorMessage: '获得未报名且为面访录音出错'
                    });
                };
            }//END OF $onInit
        }]//EDN OF CONTROLLER
});
