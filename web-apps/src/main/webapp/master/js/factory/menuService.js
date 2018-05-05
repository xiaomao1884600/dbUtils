'use strict';

angular.module('Routing', ['ui.router'])
    .provider('router', function ($stateProvider) {

        var menuProvider;

        this.$get = function ($http) {
            return {
                setUpRoutes: function () {
                    var processStates = function(menuItems){
                        // 菜单的加载
                        menuItems.push({
                            munuId: "-1",
                            name: "登录",
                            id: "login",
                            state: "login"
                        });

                        menuItems.push({
                            munuId: "-2",
                            name: "主页面",
                            id: "main-frame",
                            state: "mainFrame"
                        });
                        menuItems.push({
                            munuId: "-3",
                            name: "欢迎",
                            id: "welcome",
                            state: "welcome"
                        });

                        menuItems.push({
                            munuId: "-4",
                            name: "mysql监控详情",
                            id: "mysql-detail",
                            state: "mysqlDetail"
                        });

                        var generateState = function (items) {
                            items.forEach(function (element) {
                                if (element.pages) {
                                    generateState(element.pages)
                                }
                                if (element.type !== 'toggle') {
                                    $stateProvider.state({
                                        name: element.state,
                                        url: '/' + element.state,
                                        component: element.state
                                    });
                                }
                            });

                        };

                        generateState(menuItems);
                        $stateProvider.state({
                            name : 'forbidden',
                            url: '/' + 'forbidden',
                            templateUrl : 'app/views/infoPages/403.html'
                        });

                        /*
                        $urlRouterProvider.otherwise(function () {
                            return '/';
                        });
                        ssSideNavSectionsProvider.initWithSections(menuItems);
                        */
                        /*
                        $urlRouterProvider.otherwise(function () {
                            return '/';
                        });

                        menuProvider.initWithSections(menuItems);
                        */
                    };
                    $http({
                        url: 'generalRouter',
                        data: {
                            objectName: 'com.doubeye.core.authorization.service.MenuService',
                            action: 'getAllMenus'
                        },
                        dataType:'json',
                        method : 'POST',
                        headers: {'Content-Type': 'application/x-www-form-urlencoded'}
                    }).then(function onSuccess(response) {
                        processStates(response.data.DATA);
                    });
                }
            }
        };

        this.setMenuProvider = function (provider) {
            menuProvider = provider;
        }
    })

    .run(function (router) {
        //router.setUpRoutes();
    });