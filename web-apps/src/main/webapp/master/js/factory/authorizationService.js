/**
 * @author doubeye
 * @version 1.0.0
 身份认证服务
 */
app.factory('authorizationService', ['$cookies', 'httpService', function($cookies, httpService){
    var user = null, userGroups, campusIdsInCharge = [], campusesInCharge = [], developer = false, allCampuses = [], teamsInCharge = [], skipStudentInfo = false;
    var allCampusUrl = 'http://record.hxsd.local/api/record/get_campus_team';
    var __processAuthorization = function () {
        var userId = $cookies.get('_userId');
        campusIdsInCharge = [];
        user = {
            loginName : userId.substr(0, userId.indexOf('@')),
            userName : $cookies.get('_eduUserName'),
            userId : $cookies.get('_eduUserId')
        };
        userGroups = JSON.parse($cookies.get('_userGroup'));
        if (angular.isArray(userGroups)) {
            userGroups.forEach(function (userGroup) {
                if (userGroup.identifier === 'OFFLINE_ADA_LEADER' || userGroup.identifier === 'ONLINE_ADA_LEADER') {
                    campusIdsInCharge =  campusIdsInCharge.concat(userGroup.values);
                } else if (userGroup.identifier === 'DEVELOPER') {
                    developer = true;
                } else if (userGroup.identifier === 'SKIP_STUDENT_INFO') {
                    skipStudentInfo = true;
                }
            });
        }
    };
    var __processCampusAndTeamUsers = function (data, callback) {
        allCampuses = data;
        if (!user) {
            __processAuthorization();
        }
        teamsInCharge = [];
        campusesInCharge = [];
        campusIdsInCharge.forEach(function (campusIdInCharge) {
            var campus = com.doubeye.Utils.array.getObjectFromArray({campusid : campusIdInCharge}, allCampuses);
            campusesInCharge.push(campus);
            if (campus && angular.isArray(campus.team_list)) {
                teamsInCharge = teamsInCharge.concat(campus.team_list);
            }
        });
        if (angular.isFunction(callback)) {
            var campus = angular.copy(campusesInCharge);
            callback(campus, teamsInCharge, allCampuses);
        }
    };
    var __getAllCampusTeam = function (callback) {
        httpService.sendRequest(self, allCampusUrl, {
        }, {
            resultPropertyName : 'data',
            callback : function (data) {
                __processCampusAndTeamUsers(data, callback);
            }
        }, {
            errorMessage : '获取用户校区、负责ADA出错，'
        });
    };
    return {
        getUser : function () {
            if (!user) {
                __processAuthorization();
            }
            return user;
        },
        getUserGroups : function () {
            if (!user) {
                __processAuthorization();
            }
            return userGroups;
        },
        isDeveloper : function () {
            if (!user) {
                __processAuthorization();
            }
            return developer;
        },
        isSkipStudentInfo : function () {
            if (!user) {
                __processAuthorization();
            }
            return skipStudentInfo;
        },
        getCampusesInCharge : function() {
            if (!user) {
                __processAuthorization();
            }
            return campusIdsInCharge;
        },
        /**
         * 获得所有负责的用户
         * @param callback 回调函数，第一个参数为所有负责的校区对象，第二个参数为所有负责任的用户组对象数组，第三个参数为所有校区
         */
        getChargeInfo : function (callback) {
            if (!teamsInCharge || teamsInCharge.length === 0) {
                __getAllCampusTeam(callback);
            } else {
                var campus = angular.copy(campusesInCharge);
                callback(campus, teamsInCharge, allCampuses);
            }
        },
        clear : function () {
            user = null;
            teamsInCharge = [];
        },
        /**
         * 获得所有团队对象，方便构建下拉列表
         * @returns {{team_mun: number, name: string}}
         */
        getAllTeamObject : function () {
            return {
                team_num : '-1',
                team_title : '所有团队',
                username : '所有团队'
            }
        },
        /**
         * 获得校区下被授权的所有的团队数组
         * @param allCampus {Array<Campus>} 所有负责的校区
         * @param campusId  {int | Array<int>} 校区编号，只有符合指定编号的校区下的团队才被返回，
         *  可以是单一校区id，也可以是校区id的数组，如果此值没有指定，则返回所有负责的团队，而不区分校区
         * @param addAllTeam {boolean} 是否添加所有团队
         * @returns {Array} 被授权的团队数组
         */
        getAllTeamInChargeUnderCampus : function(allCampus, campusId, addAllTeam) {
            var result = [];
            var campusIds;
            if (angular.isArray(campusId)) {
                campusIds = campusId;
            } else if (angular.isNumber(campusId)){
                campusIds = [];
                campusIds.push(campusId);
            } else {
                campusIds = this.getCampusesInCharge();
            }
            var campus;
            campusIds.forEach(function (entry) {
                campus = com.doubeye.Utils.array.getObjectFromArray({campusid : entry}, allCampuses);
                if (campus) {
                    if (angular.isArray(campus.team_list)) {
                        result = result.concat(campus.team_list);
                    }
                }
            });
            if (addAllTeam) {
                result.splice(0, 0, this.getAllTeamObject());
            }
            return result;
        },
        getAllTeamMembersFromTeam : function(team) {
            var teams, members = [];
            if (angular.isArray(team)) {
                teams = team;
            } else {
                teams = [];
                teams.push(team);
            }
            teams.forEach(function (entry) {
                if (entry.team_user_list) {
                    members = members.concat(com.doubeye.Utils.objectRefactor.objectToArray(entry.team_user_list, null));
                }
            });
            return members;
        },
        getAllTeamMemberIdsByCampusIdAndTeamId : function (campusObject, teamId) {
            var result = [];
            if (campusObject) {
                var team = com.doubeye.Utils.array.getObjectFromArray({team_num : '' + teamId}, campusObject.team_list);
                if (team && angular.isArray(team.team_user_list)) {
                    team.team_user_list.forEach(function (ada) {
                        result.push(ada.userid);
                    });
                }
            }
            return result;
        }
    };
}]);