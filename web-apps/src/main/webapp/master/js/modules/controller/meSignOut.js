/**
 * Created by zhanglu1782 on 2017/2/13.
 */
angular.module('meSignOut', []).component('meSignOut', {
    templateUrl : 'app/views/meSignOut.html',
    controller : ['httpService',function meSignOutController(httpService) {
        var self = this;
        self.getSignOutCount = function () {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.productLine.me.SignOutService', action : 'getSignOutCount'
            }, {
                dataPropertyName : 'signOut'
            }, {
                errorMessage : '获取签退人数出错，'
            });
        };

        self.getSignInCount = function () {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.productLine.me.SignOutService', action : 'getSignInCount'
            }, {
                dataPropertyName : 'signIn'
            }, {
                errorMessage : '获取签到人数出错，'
            });
        };
    }]
});