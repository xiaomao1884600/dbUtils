/**
 * Created by zhanglu1782 on 2016/11/26.
 */
angular.module('doubleEleven', []).component('doubleEleven', {
    templateUrl : 'app/views/doubleEleven.html',
    controller : ['httpService',/* '$modal', */ function doubleElevenController(httpService) {
        var self = this;
        httpService.sendRequest(self, 'generalRouter', {objectName : 'com.hxsd.services.DoudbleElevelErrorService', action : 'getAllErrorInfo'}, {
            dataPropertyName : 'students'
        }, {
            errorMessage : '获得错误信息列表时出错，'
        });
    }]
});