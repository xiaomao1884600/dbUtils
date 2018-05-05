/**
 * Created by zhanglu1782 on 2016/8/11.
 */
angular.module('etlCasesList', []).component('etlCasesList', {
    templateUrl: 'app/views/etlCasesList.html',
    controller: ['httpService', '$timeout', 'ngDialog', '$scope', function etlCasesListController(httpService, $timeout, ngDialog, $scope) {
        var self = this;
        $scope.value = {};
        $scope.config = {
            url : 'generalRouter',
            params : {
                objectName: 'com.doubeye.core.dataSource.services.DataSourceGroupService',
                action : 'getAllPublicSources'
            },
            textPropertyName : 'identifier',
            valuePropertyName : 'identifier',
            resultValuePropertyName : 'dataSource',
            componentName : 'dataSource'
        };

        self.processEtlCases = function(etlCases) {
            self.etlCases = etlCases;
        };
        self.processRunResult = function (response, item) {
            if (response.REDIRECT === true) {
                self.checkProgress(response.url, response.uuid, item)
            }
        };

        self.getEtlCases = function() {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.EduEtlService',
                action : 'getAllEtlCases',
                dataSource : $scope.value.dataSource
            }, {
                dataPropertyName : 'etlCases'
            }, {
                errorMessage : '获得方案列表出错,'
            });
        };
        self.run = function (item) {
            item.running = true;
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.EduEtlService',
                action : 'runCase',
                dataSource : $scope.value.dataSource,
                id : item.id
            }, {
                callback : self.processRunResult
            }, {
                errorMessage : '运行方案[' + item.name + ']出错,'
            }, item);
        };

        self.runCases = function () {
            var etlIds = "";
            if (self.etlCases && self.etlCases.length > 0) {
                self.etlCases.forEach(function (item) {
                    if (item.selected) {
                        etlIds += item.id + ',';
                    }
                });
                etlIds = etlIds.substr(0, etlIds.length - 1);
            }
            if (etlIds.length > 0) {
                self.etlCases.forEach(function (item) {
                    item.running = true;
                });
                self.groupProgress = "开始...";
                httpService.sendRequest(self, 'generalRouter', {
                    objectName : 'com.hxsd.services.EduEtlService',
                    action : 'runCases',
                    etlIds : etlIds,
                    dataSource : $scope.value.dataSource
                }, {
                    callback : function(data) {
                        if (data.REDIRECT === true) {
                            self.checkGroupProgress(data.url, data.uuid);
                        }
                    }
                }, {
                    errorMessage : '执行多个etl方案出错，'
                });
            } else {
                ngDialog.open({
                    template: '<p>请先选择要运行的etl方案</p>',
                    plain: true
                });
            }
        };

        self.onRemoveClick = function (item, index) {
            if (item.running) {
                alert("无法删除正在运行的方案");
                return;
            }
            ngDialog.openConfirm({
                template: 'modalDialogId',
                className: 'ngdialog-theme-default',
                scope: $scope,
                data: item.name
            }).then(function (value) {
                self.remove(item, index);
            }, function (value) {

            });
        };

        self.remove = function (item, index) {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.EduEtlService',
                action : 'removeCase',
                id : item.id,
                dataSource : $scope.value.dataSource
            }, {
                callback : function () {
                    self.etlCases.splice(index, 1);
                    ngDialog.open({
                        template: '<p>删除成功</p>',
                        plain: true
                    });
                }
            });
        };


        self.checkProgress = function (url, uuid, item) {
            $timeout(function () {
                httpService.sendRequest(self, url, {
                    uuid : uuid
                }, {
                    callback : function (data) {
                        var progress = data[0].progress;
                        if (progress == 0) {
                            item.progress = '准备执行';
                        } else if (progress == 100) {
                            item.progress = '执行完毕';
                        } else {
                            item.progress = progress.toFixed(2) + '%';
                        }

                        if (progress < 100) {
                            self.checkProgress(url, uuid, item);
                        } else {
                            item.running = false;
                            self.removeProgress(uuid);
                        }
                    }
                });
            }, 500);
        };

        self.checkGroupProgress = function (url, uuid) {
            $timeout(function () {
                httpService.sendRequest(self, url, {
                    uuid : uuid
                }, {
                    callback : function (data) {
                        var progress = data[0].progress;
                        self.groupProgress = data[0].description;
                        if (progress < 100) {
                            self.checkGroupProgress(url, uuid);
                        } else {
                            self.etlCases.forEach(function (item) {
                                item.running = false;
                            });
                            self.removeProgress(uuid);
                        }
                    }
                });
            }, 500);
        };

        self.addCase = function () {
            if (self.isEtlCaseValid()) {
                httpService.sendRequest(self, 'generalRouter', {
                    objectName : 'com.hxsd.services.EduEtlService',
                    action : 'addCase',
                    caseName : self.addingEtlCaseName,
                    originTableName : self.addingOriginTableName,
                    targetTableName : self.addingTargetTableName,
                    primaryKey : self.addingPrimaryKey,
                    condition : self.addingCondition,
                    dataSource : $scope.value.dataSource
                }, {
                    callback : function(data) {
                        self.getEtlCases();
                    }
                }, {
                    errorMessage : '添加etl方案出错'
                });
            }
        };
        self.isEtlCaseValid = function () {
            var errorMessage = "";
            if (!self.addingEtlCaseName) {
                errorMessage += "etl方案名称不能为空\n";
            }
            if (!self.addingOriginTableName) {
                errorMessage += "etl源表名（远程表名）不能为空\n";
            }
            if (!self.addingTargetTableName) {
                errorMessage += "目标表名（实体表名）不能为空\n";
            }
            if (!self.addingTargetTableName) {
                errorMessage += "主键不能为空\n";
            }
            if (errorMessage != '') {
                ngDialog.open({
                    template: '<p>' + errorMessage + '</p>',
                    plain: true
                });
                return false;
            } else {
                return true;
            }
        };

        self.setDataSource = function(dataSource) {
            self.dataSource = dataSource;
            self.etlCases = [];
        };
        self.selectAllCases = function () {
            if (self.etlCases && self.etlCases.length > 0) {
                self.etlCases.forEach(function (item) {
                    item.selected = true;
                });
            }
        };

        self.unselectAllCases = function () {
            if (self.etlCases && self.etlCases.length > 0) {
                self.etlCases.forEach(function (item) {
                    item.selected = false;
                });
            }
        };

        self.targetTableNameChanged = function () {
            self.addingOriginTableName = 'etl_' + self.addingTargetTableName + '_from_old';
        };

        self.removeProgress = function(uuid) {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.ProgressChecker',
                action : 'removeProgress',
                uuid : uuid
            }, {

            }, {
                errorMassage : 'com.hxsd.services.ProgressChecker.removeProgress(' + uuid + ')出错'
            });
        };
        // self.getDataSources();
    }]
});