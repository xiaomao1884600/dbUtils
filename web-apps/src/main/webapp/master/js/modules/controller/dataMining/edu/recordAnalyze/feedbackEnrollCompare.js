/**
 * Created by zhanglu1782 on 2016/12/21.
 *
 */
angular.module('feedbackEnrollCompare', []).component('feedbackEnrollCompare', {
    templateUrl: 'app/views/dataMining/edu/feedbackEnrollCompare.html',
    controller: ['httpService',
        function feedbackEnrollCompareController(httpService) {

            var self = this;
            this.conversationsDataUrl = "http://esd.hxsd.test/edu/memo_strip";
            this.wordsDataUrl = "http://esd.hxsd.test/edu/memo_word";


            this.$onInit = function () {
                this.__chartConfigs = {
                    dimensions : [{
                        label : '已报名',
                        propertyName : 'is_enroll'
                    }, {
                        label : '未报名',
                        propertyName : 'no_enroll'
                    }],
                    dataPairs : [{
                        label : 'ADA对话数',
                        propertyName : 'teacher_num'
                    } , {
                        label : '客户对话数',
                        propertyName : 'student_num'
                    }]
                };
            };
            this.processConversationsResult = function (data) {
                com.doubeye.Utils.chart.processScatterChartData(data, self, "conversations", self.__chartConfigs);
            };
            this.processWordsResult = function (data) {
                com.doubeye.Utils.chart.processScatterChartData(data, self, "words", self.__chartConfigs);
            };
            this.getConversationsData = function () {
                httpService.sendRequest(self, this.conversationsDataUrl, {

                }, {
                    callback: self.processConversationsResult
                }, {
                    errorMessage: '获得EDU反馈统计出错'
                }, {}, 'GET');
            };
            this.getWordsData = function () {
                httpService.sendRequest(self, this.wordsDataUrl, {

                }, {
                    callback: self.processWordsResult
                }, {
                    errorMessage: '获得EDU反馈统计出错'
                }, {}, 'GET');
            };

            this.getConversationsData();
            this.getWordsData();
        }]
});
