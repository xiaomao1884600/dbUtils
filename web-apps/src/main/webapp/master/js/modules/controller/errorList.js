/**
 * Created by zhanglu1782 on 2016/7/14.
 */

angular.module('errorList', []).component('errorList', {
    templateUrl : 'app/views/errorList.html',
    controller : ['httpService',/* '$modal', */ function errorListController(httpService) {
        var self = this;
        // httpService.sendRequest(self, 'generalRouter', {objectName : 'com.hxsd.services.ErrorCodeService', action : 'getAllErrorCodes'}, self.processResult, {
        httpService.sendRequest(self, 'generalRouter', {objectName : 'com.hxsd.services.ErrorCodeService', action : 'getAllErrorCodes'}, {
            dataPropertyName : 'errorItems'
        }, {
            errorMessage : '获得错误信息列表时出错，'
        });
        self.processResult = function(data) {
            self.errorItems = data;
        };
        self.edit = function(item) {
            //alert(item.errorCode + ' ' + item.errorMessage);
            /*
            var newWindow = $modal.open({
                templateUrl : 'app/views/errorList.html'
            });
            */
        }
    }]
});
