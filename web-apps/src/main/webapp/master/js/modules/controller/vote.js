/**
 * Created by zhanglu1782 on 2017/1/3.
 */
angular.module('vote', []).component('vote', {
    templateUrl : 'app/views/vote.html',
    controller : ['httpService', '$scope',function errorListController(httpService, $scope) {
        var self = this;

        self.getOptions = function () {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.VoteService', action : 'getAllOptions'
            }, {
                dataPropertyName : 'options'
            }, {
                errorMessage : '获取内容出错，'
            });
        };

        self.addVoteOption = function () {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.VoteService', action : 'addVoteOption',
                optionName : self.optionName
            }, {
                //dataPropertyName : 'options'
                callback : self.getOptions
            }, {
                errorMessage : '添加内容出错，'
            });
        };

        self.vote = function(id) {
            httpService.sendRequest(self, 'generalRouter', {
                objectName : 'com.hxsd.services.VoteService', action : 'vote',
                id : id
            }, {
                //dataPropertyName : 'options'
                callback : self.getOptions
            }, {
                errorMessage : '投票错误，'
            });
        };

        self.getOptions();

        $scope.model = {
            title: 'vote'
        }
    }]
});