/**
 * Created by zhanglu1782 on 2016/12/21.
 */

angular.module('assignLog', []).component('assignLog', {
    templateUrl : 'app/views/assignLog.html',
    controller : ['httpService', '$scope', function assignLogController(httpService, $scope) {
        var self = this;


        $scope.value = {};
        $scope.config = {
            url : 'generalRouter',
            params : {
                objectName: 'com.doubeye.core.dataSource.services.DataSourceGroupService',
                action: 'getAllEDataSources'
            },
            textPropertyName : 'identifier',
            valuePropertyName : 'identifier',
            resultValuePropertyName : 'dataSource',
            componentName : 'dataSource'
        };


        self.init = function() {

        };


        self.assignLogByTimeRange = function() {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.e.toBespokeInfoService.AssignLogService',
                action: 'getAssignLogsByRange',
                dataSource: $scope.value.dataSource,
                start : $scope.start,
                end : $scope.end
            }, {
                callback : self.processAssignLogs
            }, {
                errorMessage : '获得时间段内的分配情况出错，'
            });
        };

        self.assignLogByStudentId = function() {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.e.toBespokeInfoService.AssignLogService',
                action: 'getAssignLogsByStudentId',
                dataSource: $scope.value.dataSource,
                studentId : self.studentId
            }, {
                callback : self.processAssignLogs
            }, {
                errorMessage : '获得学生的分配情况出错，'
            })
        };

        self.processAssignLogs = function (data) {
            self.assignLogDetails = [];
            data.forEach(function(element) {
                var assignLog = element.params;
                assignLog.onlineuser = com.doubeye.Utils.php.fixPhpFuckedObjectToArray(assignLog.onlineuser);
                var userId;
                var minAllocatedCount = Number.MAX_VALUE;
                var minAllocatedEcUsers = [];
                assignLog.onlineuser.forEach(function(user) {
                    userId = user.userid;
                    if (assignLog.nofeedback[userId]) {
                        user.noFeedback = true;
                    }
                    if (assignLog.daylimit[userId]) {
                        user.dayLimit = true;
                    }
                    if (assignLog.edulevel[userId]) {
                        user.eduLevel = true;
                    }
                    user.allocatedCount = assignLog.ranking[userId];
                    if (assignLog.ranking[userId] < minAllocatedCount) {
                        minAllocatedCount = assignLog.ranking[userId];
                        minAllocatedEcUsers = [];
                        minAllocatedEcUsers.push(userId);
                    } else if (assignLog.ranking[userId] == minAllocatedCount) {
                        minAllocatedEcUsers.push(userId);
                    }
                });

                minAllocatedEcUsers.forEach(function (user) {
                    var onlineUser;
                    for (var i = 0; i < assignLog.onlineuser.length; i ++) {
                        onlineUser = assignLog.onlineuser[i];
                        if (onlineUser.userid == user) {
                            onlineUser.minCount = true;
                            break;
                        }
                    }
                });
                self.assignLogDetails.push(assignLog);
            });

        };


        self.init();
    }]
});