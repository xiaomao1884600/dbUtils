/**
 * Created by zhanglu1782 on 2017/4/19.
 */
angular.module('projectEnvironments', []).component('projectEnvironments', {
    templateUrl: 'app/views/projectEnvironments.html',
    controller: ['httpService', 'ngDialog', '$scope', function projectEnvironments(httpService, ngDialog, $scope) {
        var self = this;
        this.showEditor = false;
        this.allProjects = [];
        this.allEnvironments = [];
        this.allProjectEnvironments = [];
        this.getAllProjectEnvironments = function () {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.productLine.management.projectManagement.ProjectEnvironmentService',
                action : 'getAllProjectEnvironments'
            }, {
                returnRawResponse : true,
                callback : function(data) {
                    self.allProjectEnvironments = data.projectEnvironments;
                    self.allProjectEnvironments.forEach(function (t) {
                        t.stable = parseInt(t.stable, 10);
                    });
                    self.version = data.version;
                }
            }, {
                errorMessage : '获得所有项目环境错误'
            });
        };
        this.getAllEnvironments = function () {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.productLine.management.projectManagement.EnvironmentService',
                action : 'getAllEnvironments'
            }, {
                dataPropertyName: 'allEnvironments',
                callback : function() {
                    self.getAllProjectEnvironments();
                }
            }, {
                errorMessage : '获得所有环境错误'
            });
        };
        httpService.sendRequest(self, 'generalRouter', {
            objectName : 'com.hxsd.services.productLine.management.projectManagement.ProjectService',
            action : 'getAllProjects'
        }, {
            dataPropertyName : 'allProjects',
            callback : function() {
                self.getAllEnvironments();
            }
        }, {
            errorMessage : '获得所有项目错误'
        });


        this.edit = function(currentProjectEnvironment) {
            //如果在新增，则点击其他方案编辑前提示是否保存，如果不保存，则从列表中删除
            if (this.currentProjectEnvironment && !this.currentProjectEnvironment.id && this.showEditor) {
                ngDialog.openConfirm({
                    className: 'ngdialog-theme-default',
                    scope: $scope,
                    template : 'modalDialogId',
                    data : '是否要保存尚未保存的新建描述？'
                }).then(function (value) {
                    return;
                }, function (value) {
                    self.allProjectEnvironments.pop();
                    self.currentProjectEnvironment = currentProjectEnvironment;
                    self.showEditor = true;
                });
            } else {
                this.currentProjectEnvironment = currentProjectEnvironment;
                self.showEditor = true;
            }
        };
        /**
         * 保存
         */
        this.save = function() {
            var postContent = angular.copy(this.currentProjectEnvironment);
            postContent.stable = postContent.stable ? 1: 0;
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.productLine.management.projectManagement.ProjectEnvironmentService',
                action : 'saveProjectEnvironment',
                data : postContent
            }, {
                callback : function (data) {
                    self.currentProjectEnvironment.id = data.id;
                    self.version = data.version;
                    self.showEditor = false;
                }
            }, {
                errorMessage : '保存环境配置出错'
            });
        };

        this.add = function () {
            this.showEditor = true;
            var currentProjectEnvironment = {
                stable : 1
            };
            this.allProjectEnvironments.push(currentProjectEnvironment);
            this.currentProjectEnvironment = currentProjectEnvironment;
        };

        this.remove = function (currentProjectEnvironment, index) {
            if (self.currentProjectEnvironment && self.currentProjectEnvironment.id) {
                httpService.sendRequest(self, 'generalRouter', {
                    objectName : 'com.hxsd.services.productLine.management.projectManagement.ProjectEnvironmentService',
                    action : 'removeProjectEnvironment',
                    id : currentProjectEnvironment.id
                }, {
                    callback : function (data) {
                        self.allProjectEnvironments.splice(index, 1);
                        self.version = data.version;
                        ngDialog.open({
                            template: '<p>删除成功</p>',
                            plain: true
                        });
                    }
                });
            } else {
                self.allProjectEnvironments.splice(self.allProjectEnvironments.length - 1, 1);
            }
        };

        this.onRemoveClick = function (currentProjectEnvironment, index) {
            ngDialog.openConfirm({
                template: 'modalDialogId',
                className: 'ngdialog-theme-default',
                scope: $scope,
                data: '是否要删除' + currentProjectEnvironment.name
            }).then(function () {
                self.remove(currentProjectEnvironment, index);
                self.currentProjectEnvironment = null;
                self.showEditor = false;
            }, function (value) {

            });
        };

        this.downloadHosts = function () {
            window.open("generalRouter?objectName=com.hxsd.services.productLine.management.projectManagement.ProjectEnvironmentService&action=downloadHosts");
        };


        $scope.$watch('$ctrl.currentProjectEnvironment.projectId', function (newValue) {
            if (self.currentProjectEnvironment && newValue) {
                self.currentProjectEnvironment.projectName = com.doubeye.Utils.array.getObjectFromArray({
                    id : newValue
                }, self.allProjects).name;
            }
        });
        $scope.$watch('$ctrl.currentProjectEnvironment.environmentId', function (newValue) {
            if (self.currentProjectEnvironment && newValue) {
                self.currentProjectEnvironment.environmentName = com.doubeye.Utils.array.getObjectFromArray({
                    id : newValue
                }, self.allEnvironments).name;
            }
        });
    }]
});