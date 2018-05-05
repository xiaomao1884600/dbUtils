/**
 * Created by zhanglu1782.
 * 指定电话的分析结果
 */
angular.module('callInfo', []).component('callInfo', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/callInfo.html',
    controller: ['httpService', 'dialogService',
        function callInfoController(httpService, dialogService) {
            var self = this;
            this.$onInit = function () {
                this.__callInfoColumnDefines = [{
                    label : 'ADA',
                    dataId : 'userName'
                }, {
                    label : '呼叫类型',
                    dataId : 'callType'
                }, {
                    label : '是否接通',
                    dataId : 'connected'
                }, {
                    label : '联系次数',
                    dataId : 'cnt'
                } ];
            };
            this.getData = function () {
                if (!self.mobile) {
                    dialogService.toast('请先输入要查询的电话号码');
                    return;
                }
                httpService.sendRequest(self, 'generalRouter', {
                    objectName : 'com.hxsd.services.productLine.e.call.CallInfoService',
                    action : 'getCallInfo',
                    mobile : self.mobile
                }, {
                    dataPropertyName : 'data'
                }, {
                    errorMessage: '获得电话信息出错'
                });
            };
        }]
});
