angular.module('newEduStudentInfo', []).component('newEduStudentInfo', {
    templateUrl: 'app/views/newEduStudentInfo.html',
    controller: ['httpService', '$timeout', 'ngDialog', '$scope', function newEduStudentInfoController(httpService, $timeout, ngDialog, $scope) {
        var self = this;
        self.student = 28;
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
        self.processEtlCases = function(etlCases) {
            self.etlCases = etlCases;
        };

        self.doSearch = function() {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.productLine.e.StudentService',
                student : self.student,
                action : 'getStudent',
                dataSource : $scope.value.dataSource
            }, {
                dataPropertyName : 'students'
            }, {
                errorMessage : '获取学生信息出错，'
            });
        };

        self.deleteSecondMobile = function(studentId) {
            ngDialog.openConfirm({
                template: 'modalDialogId',
                className: 'ngdialog-theme-default',
                scope: $scope,
                data: '是否要清除第二个手机号？'
            }).then(function (value) {
                httpService.sendRequest(self, 'generalRouter', {
                    objectName : 'com.hxsd.services.productLine.e.StudentService',
                    action : 'resetSecondMobile',
                    dataSource : $scope.value.dataSource,
                    studentId : studentId}, {
                    callback : self.doSearch
                }, {
                    errorMessage : '删除学生第二个手机号出错，'
                });
            }, function (value) {

            });
        };

        self.deleteSecondQQ = function(studentId) {
            ngDialog.openConfirm({
                template: 'modalDialogId',
                className: 'ngdialog-theme-default',
                scope: $scope,
                data: '是否要清除第二个qq号？'
            }).then(function (value) {
                httpService.sendRequest(self, 'generalRouter', {
                    objectName : 'com.hxsd.services.productLine.e.StudentService',
                    action : 'resetSecondQQ',
                    dataSource : $scope.value.dataSource,
                    studentId : studentId}, {
                    callback : self.doSearch
                }, {
                    errorMessage : '删除学生第二个QQ号出错，'
                });
            }, function (value) {

            });
        };

        self.deleteBoth = function(studentId) {

            ngDialog.openConfirm({
                template: 'modalDialogId',
                className: 'ngdialog-theme-default',
                scope: $scope,
                data: '是否要清除第二个手机号和qq号？'
            }).then(function (value) {
                httpService.sendRequest(self, 'generalRouter', {
                    objectName : 'com.hxsd.services.productLine.e.StudentService',
                    action : 'resetBoth',
                    dataSource : $scope.value.dataSource,
                    studentId : studentId}, {
                    callback : self.doSearch
                }, {
                    errorMessage : '删除学生第二个手机号和QQ号出错，'
                });
            }, function (value) {

            });
        };

        self.assignToChannel = function(studentid) {
            $scope.studentid = studentid;
            if (!$scope.channelUsers || !$scope.channelManagers) {
                httpService.sendRequest($scope, 'generalRouter', {
                    objectName : 'com.hxsd.services.productLine.e.privilege.UsersService',
                    action : 'getAllChannelUsers',
                    dataSource : $scope.value.dataSource
                }, {
                    dataPropertyName : 'channelUsers',
                    callback : function(data) {
                        if (com.doubeye.Ext.isJSONArray(data)) {
                            $scope.channelUsers = data[0].channelUsers;
                            $scope.channelManagers = data[0].channelManagers;
                            self.showDistributionChannel();
                        }
                    }
                }, {
                    errorMessage : '获得渠道人员信息出错'
                });
            } else {
                self.showDistributionChannel();
            }
        };

        $scope.assign = function() {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.productLine.e.StudentService',
                action : 'assignStudentToChannel',
                studentid : $scope.studentid,
                channelUserId : self.channelUser,
                channelManagerId : self.channelManager,
                dataSource : $scope.value.dataSource
            }, {
                callback : function () {

                    ngDialog.open({
                        template : '<div>操作成功</div>',
                        plain : true,
                        className: 'ngdialog-theme-plain custom-width'
                    });

                }
            }, {
                errorMessage : '将学生指定到渠道出错'
            });
        };

        this.unAssignToChannel = function(studentid) {
            $scope.studentid = studentid;
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.productLine.e.StudentService',
                action : 'unassignStudentToChannel',
                studentid : $scope.studentid,
                dataSource : $scope.value.dataSource
            }, {
                callback : function () {

                    ngDialog.open({
                        template : '<div>操作成功</div>',
                        plain : true,
                        className: 'ngdialog-theme-plain custom-width'
                    });

                    self.doSearch();
                }
            }, {
                errorMessage : '取消学生的渠道标记失败'
            });
        };

        self.showDistributionChannel = function () {
            ngDialog.open({
                template:
                    '<div><label>请选择渠道负责人：</label><select ng-model="$ctrl.channelUser" ng-options="user.userid as user.username for user in channelUsers"></select></div>' +
                    '<div><label>请选择渠道经理：</label><select ng-model="$ctrl.channelManager" ng-options="user.userid as user.username for user in channelManagers"></select></div>' +
                        '<button ng-click="assign()">确定</button>' +
                    '</div>',
                plain : true,
                className: 'ngdialog-theme-plain custom-width',
                scope : $scope
            });
        };
    }]
});