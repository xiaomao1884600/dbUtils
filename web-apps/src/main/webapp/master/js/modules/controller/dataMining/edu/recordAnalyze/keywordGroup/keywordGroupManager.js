/**
 * @see 本功能接口文档http://record_logs.mydoc.io/
 */
angular.module('keywordGroupManager', []).component('keywordGroupManager', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/keywordGroup/keywordGroupManager.html',
    controller: ['httpService', '$mdDialog',
        function keywordGroupManagerController(httpService, $mdDialog) {

            var self = this;
            self.index = 0;

            this.$onInit = function () {
                this.saving = false;

                this.__columnDefines = [{
                    dataId : 'name',
                    label : '关键词组名称'
                }];
                this.__operations = [{
                    text : '管理关键词组',
                    callback : function(record) {
                        self.showKeywordGroup(record);
                    }
                }, {
                    text : '删除',
                    callback : function(record) {
                        httpService.sendRequest(self, 'generalRouter', {
                            objectName: 'com.doubeye.datamining.recordanalyze.service.KeywordGroupService',
                            action: 'removeKeywordGroup',
                            id : record.id
                        }, {
                            callback : function() {
                                self.getData();
                            }
                        }, {
                            errorMessage : '删除关键词组出错'
                        });
                    }
                }];
            };

            this.showKeywordGroup = function (record) {
                if (record) {
                    record.displayKeywords = [];
                    var keyword;
                    for (var i = 0; i < record.keywords.length; i ++) {
                        keyword = record.keywords[i];
                        record.displayKeywords.push({
                            id : keyword,
                            text : keyword
                        });
                    }
                }
                self.currentKeywordGroup = angular.copy(record);
                $mdDialog.show({
                    contentElement: '#keywordGroupContent',
                    clickOutsideToClose: true
                });
            };

            this.addWord = function (target) {
                self.index ++;
                self.currentKeywordGroup.displayKeywords.push({id : "新增" + self.index});
            };

            this.removeWord = function (item) {
                self.currentKeywordGroup.displayKeywords.splice(item, 1);
            };

            this.addKeywordGroup = function () {
                self.currentKeywordGroup = {
                    keywords : []
                };
                self.showKeywordGroup(self.currentKeywordGroup);
            };

            this.saveKeywordGroup = function () {
                var keywordGroup = angular.copy(self.currentKeywordGroup);
                if (angular.isArray(keywordGroup.displayKeywords)) {
                    keywordGroup.keywords = [];
                    keywordGroup.displayKeywords.forEach(function (value) {
                        keywordGroup.keywords.push(value.text);
                    });
                    delete keywordGroup.displayKeywords;
                }
                self.saving = true;
                httpService.sendRequest(self, 'generalRouter', {
                    objectName: 'com.doubeye.datamining.recordanalyze.service.KeywordGroupService',
                    action: 'saveKeywordGroup',
                    keywordGroup : keywordGroup
                }, {
                    callback : function (data) {
                        self.getData();
                        self.saving = false;
                        $mdDialog.hide({
                            contentElement: '#keywordGroupContent'
                        });
                    }
                }, {
                    errorMessage : '保存关键词组出错，'
                });
            };

            this.getData = function () {
                httpService.sendRequest(self, 'generalRouter', {
                    objectName: 'com.doubeye.datamining.recordanalyze.service.KeywordGroupService',
                    action: 'getAllKeywordGroup'
                }, {
                    dataPropertyName : 'keywordGroups'
                }, {
                    errorMessage : '获取关键词组出错，'
                });
            };
            this.getData();
        }]
});
