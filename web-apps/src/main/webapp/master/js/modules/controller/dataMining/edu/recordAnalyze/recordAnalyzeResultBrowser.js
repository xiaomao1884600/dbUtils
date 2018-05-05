/**
 *
 */
angular.module('recordAnalyzeResultBrowser', []).component('recordAnalyzeResultBrowser', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/recordAnalyzeResultBrowser.html',
    bindings : {
        /**
         * 用户编号
         */
        userId: '<',
        /**
         * 用户姓名
         */
        userName : '<',
        /**
         * 用户组
         */
        userGroups : '<',
        /**
         * 分析结果
         */
        analyzeResult : '<',
        selectedTags : '=',
        /**
         * 是否显示确认识别质量的操作，默认为true
         */
        showConfirmOperation : '<'
    },
    controller: ['httpService', '$mdDialog', 'dialogService', 'authorizationService', '$interval',
        function recordAnalyzeResultBrowserController(httpService, $mdDialog, dialogService, authorizationService, $interval) {
            var self = this;
            this.$onInit = function () {
                this.developer = authorizationService.isDeveloper();
                this.playedTime = 100;
                this.showDownload = com.doubeye.Utils.compatibility.audioWithoutDownload;
                this.qulityUrl = 'http://record.hxsd.local/api/record/set_confirm';
                this.analyzeTagUrl = 'http://record.hxsd.local/api/record/set_analyze_type';
                /**
                 * 关键词在阿里识别结果中的当前检索位置
                 * @type {number}
                 */
                this.aliSearchIndex = -1;
                this.percent = 100;
                if (this.showConfirmOperation) {
                    this.showConfirmOperation = true;
                }
                /**
                 * 对话类型，默认为全部
                 * @type {number}
                 */
                this.conversationTypes = [{
                    propertyName : 'all',
                    name : '所有'
                }, {
                    propertyName : 'student',
                    name : '学生'
                }, {
                    propertyName : 'user',
                    name : 'ADA'
                }];

                this.wordCloudModes = [ {id : 0, name : '词频'}, {id : 1, name : '关键词'}, {id : 2, name : '异议词'}];
                this.selectedCloudMode = self.wordCloudModes[1];

                this.selectedConversation = self.conversationTypes[0].propertyName;
                //this.recordTags = ['无意向', '了解课程', '报名', '到校联系', '退学', '网校', '售后', '无效电话', '推迟联系', '强意向'];
                this.recordTags = ['零基础', '距离远', '没时间，在工作、上学', '钱不够，贷款', '担心就业', '家里不同意', '考虑竞聘'];
                this.headerHeight = 0;
                if (this.developer) {
                    this.headerHeight += 123;
                }
            };

            this.audio = document.getElementById("player");
            this.play = function(beginTime) {
                this.audio.currentTime = beginTime / 1000;
                this.audio.play();
            };
            var aliResultElement = document.getElementById('aliResult');

            var getAudioCurrentTime = function() {
                var tencentResultElement = document.getElementById('tencentResult');
                var playing = !self.audio.paused;
                self.playedTime = self.audio.currentTime * 1000;
                if (playing && !isNaN(self.audio.duration) && self.audio.duration !== 0 && self.audio.currentTime > 5) {
                    if (aliResultElement) {
                        var aliPosition = aliResultElement.scrollHeight * self.audio.currentTime / self.audio.duration;
                        var aliCurrentConversationEl = document.querySelector('#aliResult .current-conversation');
                        if (aliCurrentConversationEl && aliCurrentConversationEl.offsetTop> aliResultElement.scrollTop) {
                            aliResultElement.scrollTop = Math.min(aliPosition, (aliCurrentConversationEl.offsetTop - 5));
                        }
                    }
                    if (tencentResultElement) {
                        var tencentPosition = tencentResultElement.scrollHeight * self.audio.currentTime / self.audio.duration;
                        var tencentCurrentConversationEl = document.querySelector('#tencentResult .current-conversation');
                        if (tencentCurrentConversationEl && tencentCurrentConversationEl.offsetTop > tencentResultElement.scrollTop) {
                            tencentResultElement.scrollTop = Math.min(tencentPosition, (tencentCurrentConversationEl.offsetTop - 5));
                        }
                    }
                }
            };
            $interval(getAudioCurrentTime, 1000);

            //人为判断转文字质量
            this.changeAsrRate = function () {
                httpService.sendRequest(self, self.qulityUrl, {
                    userid : self.userId,
                    username : self.userName,
                    record_id : self.analyzeResult.record_id,
                    asr_rate : self.analyzeResult.asr_rate === true ? 1 : 0,
                    _noProcessingDialog : true
                }, {

                }, {
                    errorMessage : '记录文字识别是否正确出错，'
                });
            };
            //人为判断转文字质量
            this.changeChannelRate = function () {
                httpService.sendRequest(self, self.qulityUrl, {
                    userid : self.userId,
                    username : self.userName,
                    record_id : self.analyzeResult.record_id,
                    channel_rate : self.analyzeResult.channel_rate === true ? 1 : 0,
                    _noProcessingDialog : true
                }, {

                }, {
                    errorMessage : '记录角色识别是否正确出错，'
                });
            };

            //人为转换角色
            this.changeUserRole = function () {
                self.analyzeResult.user_channel_id = (self.analyzeResult.user_channel_id + 1) % 2;
                httpService.sendRequest(self, self.qulityUrl, {
                    userid : self.userId,
                    username : self.userName,
                    record_id : self.analyzeResult.record_id,
                    user_channel_id : self.analyzeResult.user_channel_id,
                    _noProcessingDialog : true
                }, {

                }, {
                    errorMessage : '转换角色出错，'
                });
            };

            this.$onChanges = function(changedObject) {
                if (changedObject.analyzeResult) {
                    if (changedObject.analyzeResult.tencent_record === 1) {
                        self.percent = 50;
                    } else {
                        self.percent = 100;
                    }
                }
            };
            this.isTagSelected = function (tag) {
                if (angular.isArray(self.selectedTags)) {
                    return self.selectedTags.indexOf(tag) > -1;
                } else {
                    return false;
                }
            };
            this.toggleTagSelect = function (tag) {
                var idx = self.selectedTags.indexOf(tag);
                if (idx > -1) {
                    self.selectedTags.splice(idx, 1);
                }
                else {
                    self.selectedTags.push(tag);
                }

                httpService.sendRequest(self, self.analyzeTagUrl, {
                    userid : self.userId,
                    username : self.userName,
                    record_id : self.analyzeResult.record_id,
                    analyze_type : self.selectedTags,
                    _noProcessingDialog : true
                }, {

                }, {
                    errorMessage : '标记标签出错，'
                });
            };

            this.refreshWordCloud = function () {
                if (self.analyzeResult) {
                    self.aliSearchIndex = -1;
                    var wordType = self.selectedConversation;
                    var words, valuePropertyName;
                    if (self.selectedCloudMode.name === '关键词' && self.analyzeResult.wordStat) {
                        words = self.analyzeResult.wordStat[wordType].tf_idf;
                        valuePropertyName = 'tf_idf';
                    } else if (self.selectedCloudMode.name === '词频' && self.analyzeResult.wordStat) {
                        words = self.analyzeResult.wordStat[wordType].tf;
                        valuePropertyName = 'tf';
                    } else if (self.selectedCloudMode.name === '异议词') {
                        valuePropertyName = 'objection';
                        words = [];
                        if (angular.isArray(self.analyzeResult.objection_tags[wordType])) {
                            self.analyzeResult.objection_tags[wordType].forEach(function (value) {
                                if (angular.isArray(value.keywords)) {
                                    value.keywords.forEach(function (keyword) {
                                        words.push({
                                            word : keyword.word,
                                            objection : keyword.count
                                        });
                                    });
                                }
                            });
                        }
                    }

                    var results = [];
                    if (angular.isArray(words)) {
                        words.forEach(function (word) {
                            results.push({
                                name : word.word,
                                value : word[valuePropertyName]
                            });
                        });
                        self.analyzeResult.words = results;
                    }
                }
            };

            this.onChangeConversationType = function (conversationType) {
                self.selectedConversation = conversationType;
                self.refreshWordCloud();
            };

            this.onWordClick = function (params) {
                var word = params.name;
                self.keyword = word;
                var aliConversationEls = document.querySelectorAll('#aliResult td');
                for (var i = self.aliSearchIndex + 1; i < aliConversationEls.length; i ++) {
                    if (aliConversationEls[i].innerText.toLocaleLowerCase().indexOf(word) >= 0) {
                        self.aliSearchIndex = i;
                        aliResultElement.scrollTop = aliConversationEls[i].offsetTop - 5;
                        for (var j = i + 1; j < aliConversationEls.length; j ++) {
                            if (aliConversationEls[j].innerText.toLocaleLowerCase().indexOf(word) >= 0) {
                                break;
                            }
                        }
                        if (j >= aliConversationEls.length) {
                            self.aliSearchIndex = -1;
                        }
                        return;
                    }
                }
                self.aliSearchIndex = -1;
            };

            this.$onChanges = function(changedObject) {
                if (changedObject.analyzeResult) {
                    self.refreshWordCloud();
                }
            }
        }]
}).filter('highlight', function($sce) {
    return function(text, phrase) {
        if (phrase) text = text.replace(new RegExp('('+phrase+')', 'gi'),
            '<span class="highlighted">$1</span>');
        return $sce.trustAsHtml(text)
    }
});
