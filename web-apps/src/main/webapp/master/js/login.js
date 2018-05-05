angular.module('login', []).component('login', {
    templateUrl : 'app/views/login.html',
    controller : ['httpService', 'ngDialog', '$location', '$cookies', 'ssSideNav', '$scope', '$window', 'authorizationService',
        function loginController(httpService, ngDialog, $location, $cookies, ssSideNav, $scope, $window, authorizationService) {
        var self = this;

        this.processMenus = function (menus) {
            com.doubeye.Utils.application.renderMenu(menus, true, authorizationService);
        };


        this.processLoginResult = function (result) {
            if (result.wrongUserNameOrPassword) {
                ngDialog.open({
                    template: '<p>用户名密码错误</p>',
                    plain: true
                });
            } else if (result.errorMassage) {
                ngDialog.open({
                    template: '<p>' + result.errorMassage + '</p>',
                    plain: true
                });
            } else {
                // 登录成功,记录登录的identity
                $cookies.put("_userId", result._userId);
                $cookies.put("_expiredDate", result._expiredDate);
                $cookies.put("_token", result._token);
                $cookies.put("_eduUserId", result._eduUserId);
                $cookies.put("_eduUserName", result._eduUserName);
                $cookies.put("_userGroup", JSON.stringify(result._userGroup));

                httpService.sendRequest(self, 'generalRouter', {
                    objectName: 'com.doubeye.core.authorization.service.MenuService',
                    action: 'getAllMenusByUserId',
                    _noProcessingDialog : true
                }, {
                    dataPropertyName : 'menuItem',
                    callback : self.processMenus
                }, {
                    errorMessage : '获得菜单出错，'
                });
            }
        };
        self.doLogin = function () {
            authorizationService.clear();
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.doubeye.core.authorization.service.LDAPLoginService',
                action : 'login',
                username : self.username,
                password : self.password,
                _noProcessingDialog : true
            }, {
                returnRawResponse : true,
                callback : self.processLoginResult
            }, {
                errorMessage : '登录出错'
            });
        };
    }]
});