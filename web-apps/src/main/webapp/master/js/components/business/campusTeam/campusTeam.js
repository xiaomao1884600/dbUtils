/**
 * Created by doubeye
 * 期数组件
 */
angular.module('campusTeam', []).component('campusTeam', {
    templateUrl: 'master/js/components/business/campusTeam/campusTeam.html',
    bindings : {
        selectedCampus : '=',
        selectedTeamUser : '=',
        teamUsersUnderCampus : '=',
        onLoaded : '<'
    },
    controller: ['httpService',
        function campusTermController(httpService) {
            var self = this;
            this.$onInit = function () {
                this.campusTeamUserUrl = 'http://record.hxsd.local/api/record/get_campus_team';
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
                    if (angular.isFunction(self.onLoaded)) {
                        self.onLoaded(self.campus, self.teamUsers);
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
            }//END OF $onInit
        }]//EDN OF CONTROLLER
});
