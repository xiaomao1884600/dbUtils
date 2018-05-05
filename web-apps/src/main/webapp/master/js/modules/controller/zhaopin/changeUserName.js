angular.module('changeUserName', []).component('changeUserName', {
    templateUrl : 'app/views/zhaopin/changeUserName.html',
    controller : ['httpService', 'ngDialog','$scope', function changeUserNameController(httpService, ngDialog, $scope) {
        var self = this;
        this.getUserInfo = function() {
            this.users = [];
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.zhaopin.UserNameService',
                action: 'getUserByName',
                userName : self.userName
            }, {
                dataPropertyName : 'users'
            }, {
                errorMessage : '获取用户信息出错，'
            });
        };

        this.changeUserName = function (user) {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.zhaopin.UserNameService',
                action: 'changeUserName',
                id : user.id,
                userName : user.nameToBeChange
            }, {
                callback : function () {
                    user.username = user.nameToBeChange;
                    ngDialog.open({
                        template: '<p>操作成功</p>',
                        plain: true
                    });
                }
            }, {
                errorMessage : '获取用户信息出错，'
            });
        };

        this.onChangeClick = function (user) {
            if (!user.nameToBeChange) {
                alert('请先指定要更改的名称');
                return;
            }
            ngDialog.openConfirm({
                template: 'modalDialogId',
                className: 'ngdialog-theme-default',
                scope: $scope,
                data: '是否确认要将' + user.username + '改为' + user.nameToBeChange + '?'
            }).then(function (value) {
                self.changeUserName(user);
            }, function (value) {

            });
        };
    }]
});