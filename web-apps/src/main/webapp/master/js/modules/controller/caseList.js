/**
 * Created by zhanglu1782 on 2017/4/19.
 */
angular.module('caseList', []).component('caseList', {
    templateUrl: 'app/views/importExportCase/caseList.html',
    controller: ['httpService', '$timeout', 'ngDialog', '$scope', function caseListController(httpService, $timeout, ngDialog, $scope) {
        var self = this;

        this.dataSources = [];
        this.cases = [];
        this.showEditor = false;
        this.editExistCase = false;
        // 是否改变已选择的源数据表
        this.keepSelectedTable = false;

        this.getAllCases = function() {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.doubeye.commons.database.MySQL.administrator.service.ImportExportCaseService',
                action : 'getAllCases'
            }, {
                dataPropertyName: 'cases',
                callback : this.getAllCases
            }, {
                errorMessage : '获得方案错误'
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
                        self.description = data[0].description;
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

        this.processRunResult = function (response, item) {
            if (response.REDIRECT === true) {
                self.checkProgress(response.url, response.uuid, item)
            }
        };

        this.runCase = function (thisCase) {
            thisCase.running = true;
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.doubeye.commons.database.MySQL.administrator.service.ImportExportCaseService',
                action : 'runCase',
                caseId : thisCase.id
            }, {
                callback : self.processRunResult
            }, {
                errorMessage : '运行方案[' + thisCase.name + ']出错,'
            }, thisCase);
        };


        this.setCurrentCase = function (currentCase) {
            this.currentCase = currentCase;
            this.currentCase.selectAllTableInOriginDatabase = !currentCase.originTableNames || currentCase.originTableNames.length == 0;
            this.showEditor = true;
            this.editExistCase = (currentCase.id != null);
            this.keepSelectedTable = true;
        };

        this.editCase = function(currentCase) {
            //如果在新增，则点击其他方案编辑前提示是否保存，如果不保存，则从列表中删除
            if (this.currentCase && !this.currentCase.id && this.showEditor) {
                ngDialog.openConfirm({
                    className: 'ngdialog-theme-default',
                    scope: $scope,
                    template : 'modalDialogId'
                }).then(function (value) {
                    return;
                }, function (value) {
                    self.cases.pop();
                    self.setCurrentCase(currentCase);
                });
            } else {
                self.setCurrentCase(currentCase);
            }
        };



        httpService.sendRequest(self, 'generalRouter', {
            objectName : 'com.doubeye.core.dataSource.services.DataSourceGroupService',
            action : 'getAllPublicSources'
        }, {
            dataPropertyName: 'dataSources',
            callback : this.getAllCases
        }, {
            errorMessage : '获得数据源错误'
        });


        this.addCase = function () {
            this.showEditor = true;
            this.editExistCase = false;
            var currentCase = {
                selectAllTableInOriginDatabase : true
            };
            this.cases.push(currentCase);
            this.setCurrentCase(currentCase);
        };

        this.currentCase = {};



        /**
         * 保存etlCase
         */
        this.save = function() {
            var postContent = angular.copy(this.currentCase);
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.doubeye.commons.database.MySQL.administrator.service.ImportExportCaseService',
                action : 'saveCase',
                case : postContent
            }, {
                callback : function (data) {
                    self.currentCase.id = data.id;
                    self.currentCase = {};
                    self.showEditor = false;
                    self.keepSelectedTable = false;
                }
            }, {
                errorMessage : '获得数据源包含的数据库出错'
            });
        };

        this.changeDatasource = function (newValue, sender) {
            var dataBasePropertyName = '';
            var datasourceId = '';
            if (sender == 'originDatasource') {
                dataBasePropertyName = 'originDatabases';
                datasourceId = this.currentCase.originDatasourceId;
                if (!this.editExistCase) {
                    this.currentCase.originDatabase = null;
                }
            } else if (sender == 'targetDatasource') {
                dataBasePropertyName = 'targetDatabases';
                datasourceId = this.currentCase.targetDatasourceId;
                if (!this.editExistCase) {
                    this.currentCase.targetDatabase = null;
                }
            }
            if (datasourceId) {
                httpService.sendRequest(self, 'generalRouter', {
                    objectName : 'com.doubeye.core.dataSource.services.meta.MySQL.MetadataService',
                    datasourceId : datasourceId,
                    action : 'getAllSchemasByDataSourceId'
                }, {
                    dataPropertyName: dataBasePropertyName
                }, {
                    errorMessage : '获得数据源包含的数据库出错'
                });
            }
        };

        this.getAllTablesInSchema = function () {
            if (self.currentCase.originDatasourceId && self.currentCase.originDatabase) {
                httpService.sendRequest(self, 'generalRouter', {
                    objectName : 'com.doubeye.core.dataSource.services.meta.MySQL.MetadataService',
                    datasourceId : self.currentCase.originDatasourceId,
                    database : self.currentCase.originDatabase,
                    action : 'getAllTablesInSchema'
                }, {
                    dataPropertyName: "currentCase.allTablesInOriginDatabase",
                    callback : function () {
                        //清除掉每次改数据库时，不存在的数据表
                        if (self.currentCase && self.currentCase.allTablesInOriginDatabase) {
                            var selectedTables = self.currentCase.originTableNames;
                            if  (angular.isArray(selectedTables)) {
                                for (var i = selectedTables.length - 1; i >= 0; i --) {
                                    if (!com.doubeye.Utils.isValueInObjectArray(selectedTables[i], self.currentCase.allTablesInOriginDatabase, 'TABLE_NAME')) {
                                        self.currentCase.originTableNames.splice(i, 1);
                                        //self.currentCase.originTableNames.slice(i, 1);
                                    }
                                }
                            }
                        }
                    }
                }, {
                    errorMessage : '获得数据源包含的数据库出错'
                });
            }
        };

        this.getDatasourceLabelById = function (datasourceId) {
            var label = '';
            this.dataSources.forEach(function (element) {
                if (datasourceId == element.id) {
                    label = element.name;
                    return;
                }
            });
            return label;
        };

        this.removeProgress = function(uuid) {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.ProgressChecker',
                action : 'removeProgress',
                uuid : uuid
            }, {

            }, {
                errorMassage : 'com.hxsd.services.ProgressChecker.removeProgress(' + uuid + ')出错'
            });
        };

        $scope.$watch('$ctrl.currentCase.originDatasourceId', function (newValue) {
            self.changeDatasource(newValue, 'originDatasource');
            self.currentCase.originDatasourceLabel = self.getDatasourceLabelById(newValue);
        });
        $scope.$watch('$ctrl.currentCase.targetDatasourceId', function (newValue) {
            self.changeDatasource(newValue, 'targetDatasource');
            self.currentCase.targetDatasourceLabel = self.getDatasourceLabelById(newValue);
        });

        $scope.$watch('$ctrl.currentCase.originDatabase', function (newValue) {
            if (!self.keepSelectedTable) {
                self.currentCase.allTablesInOriginDatabase = [];
                self.currentCase.selectAllTableInOriginDatabase = true;
            } else {
                self.keepSelectedTable = false;
            }
        });
        /**
         * 选择是否全选数据库时触发的事件，如果选择“选择所有表”则去掉选定的数据表内容
         */
        $scope.$watch('$ctrl.currentCase.selectAllTableInOriginDatabase', function (newValue) {
            console.log('watch selectAllTableInOriginDatabase');
            if (!newValue && self.currentCase.originDatasourceId && self.currentCase.originDatabase) {
                self.getAllTablesInSchema();
            } else {
                self.currentCase.selectedTablesInOriginDatabase = [];
                self.currentCase.originTableNames = [];
            }
        });

    }]
});