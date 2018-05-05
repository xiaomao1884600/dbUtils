/**
 * Created by zhanglu1782 on 2016/12/21.
 */

angular.module('scheduleEditor', []).component('scheduleEditor', {
    templateUrl : 'app/views/scheduler/scheduleEditor.html',
    bindings : {
        schedule : '='
    },
    controller : ['httpService', function scheduleInitConfigEditorController(httpService) {
        var self = this;
        this.$onInit = function (){
            this.dataSources = [{
                id : 1,
                name : '测试'
            }];
        };
    }]
});