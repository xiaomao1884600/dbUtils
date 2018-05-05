/**
 * Created by zhanglu1782 on 2016/12/21.
 *
 */
angular.module('mainFrame', []).component('mainFrame', {
    templateUrl: 'master/js/modules/controller/mainFrame/mainFrame.html',
    controller: ['$scope', '$mdSidenav', '$timeout', '$rootScope', 'ssSideNav', 'httpService', '$cookies', '$location', 'authorizationService',
        function mainFrameController($scope, $mdSidenav, $timeout, $rootScope, ssSideNav, httpService, $cookies, $location, authorizationService) {

            var self = this;
            com.doubeye.Utils.application.registerMenu(ssSideNav, $scope, $cookies, $location);
            $scope.user = {};

            this.processMenus = function (menus) {
                com.doubeye.Utils.application.renderMenu(menus, false, authorizationService);
                //self.user = authorizationService.getUser();
            };

            this.getMenus = function () {
                httpService.sendRequest(self, 'generalRouter', {
                    objectName: 'com.doubeye.core.authorization.service.MenuService',
                    action: 'getAllMenusByUserId'
                }, {
                    callback : self.processMenus
                }, {
                    errorMessage : '获得菜单出错，'
                });
            };

            this.logout = function () {
                $cookies.remove('_userId');
                $location.path('login');
                authorizationService.clear();
            };

            if (!$scope.menus) {
                self.getMenus();
            }


            $scope.onClickMenu = function () {
                $mdSidenav('left').toggle();
            };



        }]
});
