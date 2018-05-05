/**
 * Created by doubeye
 * 项目中用的组件模板
 * 注意！！！不要将此文件加入到main.html的js加载中
 */
angular.module('onlineAuth', []).component('onlineAuth', {
    templateUrl: 'app/views/authorization/online/onlineAuth.html',
    controller: ['httpService', 'dialogService',
        function onlineAuthController(httpService, dialogService) {
            var self = this;
            this.$onInit = function () {
                this.user = {};
                /**
                 * 用户是否已经存在标记
                 * @type {boolean}
                 */
                this.userExists = false;
                this.teams = [{
                    id : 101,
                    title : '销售1组'
                }, {
                    id : 102,
                    title : '销售2组'
                }];
                this.selectedTeam = this.teams[0];

                this.checkUser = function () {
                    httpService.sendRequest(self, 'generalRouter', {
                        objectName: 'com.doubeye.core.authorization.service.SimpleUserService',
                        action: 'checkUser',
                        user : self.user
                    }, {
                        returnRawResponse : true,
                        callback : function (result) {
                            self.userExists = result.userExists;
                        }
                    }, {
                        errorMessage: '获得已被重置密码的用户出错,'
                    });
                };

                this.save = function() {
                    if (!self.user.domainName || !self.user.userName || !self.user.mobile || !self.user.team || !self.user.userId) {
                        dialogService.toast('请填写除分机号外所有的内容');
                    } else {
                        self.user.teamName = self.user.team.title;
                        self.user.teamId = self.user.team.id;
                        httpService.sendRequest(self, 'generalRouter', {
                            objectName: 'com.doubeye.core.authorization.service.SimpleUserService',
                            action: 'addOnlineAda',
                            user : self.user
                        }, {
                            callback : function () {
                                dialogService.toast("用户添加成功");
                                self.user = {};
                            }
                        }, {
                            errorMessage: '获得已被重置密码的用户出错,'
                        });
                    }
                };
            }//END OF $onInit
        }]//EDN OF CONTROLLER
});
