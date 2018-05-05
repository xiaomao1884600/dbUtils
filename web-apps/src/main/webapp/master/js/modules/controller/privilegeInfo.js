/**
 * Created by zhanglu1782 on 2016/10/30.
 */
angular.module('privilegeInfo', []).component('privilegeInfo', {
    templateUrl: 'app/views/privilegeInfo.html',
    controller: ['httpService', 'ngDialog', '$cookies', '$scope', function privilegeInfoController(httpService, ngDialog, $cookies, $scope) {
        var self = this;

        self.init = function () {
            self.info = [];
            self.userInfoes = '';
        };

        self.getAllDefaultUsers = function () {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.e.PrivilegeInfoService',
                action: 'getDefaultPasswordUser',
                dataSource : $scope.value.dataSource
            }, {
                dataPropertyName: 'resettedUsers'
            }, {
                errorMessage: '获得已被重置密码的用户出错,'
            });
        };

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
            componentName : 'dataSource',
            afterChange : self.getAllDefaultUsers
        };


        self.getPrivileges = function () {
            self.usersUnderGroup = [];
            if (!self.userInfo) {
                ngDialog.open({
                    template: '<p>请输入要查询的用户名</p>',
                    plain: true
                });
                return;
            }
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.e.PrivilegeInfoService',
                action: 'getPermissionsByUserInfo',
                dataSource : $scope.value.dataSource,
                userInfo: self.userInfo,
                _noProcessingDialog : true
            }, {
                dataPropertyName: 'infoes'
            }, {
                errorMessage: '获得权限信息出错,'
            });
        };

        self.getUsersUnderGroup = function () {
            self.infoes = [];
            alert(self.userGroup);
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.e.PrivilegeInfoService',
                action: 'getUsersUnderGroup',
                dataSource : $scope.value.dataSource,
                groupInfo: self.groupInfo
            }, {
                dataPropertyName: 'usersUnderGroup'
            }, {
                errorMessage: '获取用户组下的用户失败,'
            });
        };


        self.setDefaultPassword = function (user) {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.e.PrivilegeInfoService',
                action: 'resetUserPassword',
                dataSource : $scope.value.dataSource,
                userId: user.userid,
                _noProcessingDialog : true
            }, {
                callback: self.getAllDefaultUsers
            }, {
                errorMessage: '设置默认的密码出错,'
            });
        };

        self.restorePassword = function(user) {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.e.PrivilegeInfoService',
                action: 'restorePassword',
                originDataSource : user.datasource,
                userId: user.userid,
                _noProcessingDialog : true
            }, {
                callback: self.getAllDefaultUsers
            }, {
                errorMessage: '还原密码出错,'
            });
        };
    }]
});