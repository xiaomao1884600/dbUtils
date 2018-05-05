/**
 * @see 本功能接口文档http://record_logs.mydoc.io/
 */
angular.module('hotVocabularyManager', []).component('hotVocabularyManager', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/hotVocabulary/hotVocabularyManager.html',
    controller: ['httpService', '$mdDialog',
        function hotVocabularyManagerController(httpService, $mdDialog) {

            var self = this;
            self.index = 0;

            this.$onInit = function () {

                this.__columnDefines = [{
                    dataId : 'id',
                    label : '词汇表id'
                }, {
                    dataId : 'name',
                    label : '词汇表名称'
                }];
                this.__operations = [{
                    text : '管理词汇',
                    callback : function(record) {
                        self.showVocabulary(record);
                    }
                }, {
                    text : '删除',
                    callback : function(record) {
                        httpService.sendRequest(self, 'generalRouter', {
                            objectName: 'com.hxsd.services.productLine.dataMining.recordAnalyze.VocabularyTableService',
                            action: 'deleteVocabularyTableById',
                            vocabularyTableId : record.id
                        }, {
                            callback : function() {
                                self.getData();
                            }
                        }, {
                            errorMessage : '删除词汇表出错'
                        });
                    }
                }];
            };

            this.showVocabulary = function (record) {
                if (record.id) {
                    httpService.sendRequest(self, 'generalRouter', {
                        objectName: 'com.hxsd.services.productLine.dataMining.recordAnalyze.VocabularyTableService',
                        action: 'getVocabularyTableById',
                        vocabularyTableId : record.id
                    }, {
                        callback : function (data) {
                            if (angular.isArray(data) && data.length > 0) {
                                self.currentVocabularyTable = data[0];
                                self.currentVocabularyTable.name = record.name;
                                self.currentVocabularyTable.id = record.id;
                                $mdDialog.show({
                                    contentElement: '#vocabularyContent',
                                    clickOutsideToClose: true
                                });
                            }
                        }
                    }, {
                        errorMessage : '获取热词表内容出错，'
                    });
                } else {
                    $mdDialog.show({
                        contentElement: '#vocabularyContent',
                        clickOutsideToClose: true
                    });
                }
            };

            this.addWord = function (target) {
                self.index ++;
                if (target === 'words') {
                    self.currentVocabularyTable[target].push("新增" + self.index);
                } else {
                    self.currentVocabularyTable[target].push({
                        id : "新增" + self.index,
                        weight : 1
                    });
                }
            };

            this.removeWord = function (target, item) {
                if (target === 'words') {
                    self.currentVocabularyTable[target].splice(item, 1);
                } else {
                    self.currentVocabularyTable[target].splice(item, 1);
                }
            };

            this.addVocabularyTable = function () {
                self.currentVocabularyTable = {
                    words : [],
                    word_weights : []
                };
                self.showVocabulary(self.currentVocabularyTable);
            };

            this.saveVocabularyTable = function () {
                httpService.sendRequest(self, 'generalRouter', {
                    objectName: 'com.hxsd.services.productLine.dataMining.recordAnalyze.VocabularyTableService',
                    action: 'saveVocabularyTable',
                    vocabularyTable : self.currentVocabularyTable
                }, {
                    callback : function (data) {
                        self.getData();
                        $mdDialog.hide({
                            contentElement: '#keywordGroupContent'
                        });
                    }
                }, {
                    errorMessage : '更新热词表出错，'
                });
            };

            this.getData = function () {
                httpService.sendRequest(self, 'generalRouter', {
                    objectName: 'com.hxsd.services.productLine.dataMining.recordAnalyze.VocabularyTableService',
                    action: 'getAllVocabularyTable'
                }, {
                    dataPropertyName : 'vocabularyTables'
                }, {
                    errorMessage : '获取热词表出错，'
                });
            };
            this.getData();
        }]
});
