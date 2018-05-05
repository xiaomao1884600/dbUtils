/**
 * Created by doubeye
 * 项目中用的组件模板
 * 注意！！！不要将此文件加入到main.html的js加载中
 */
angular.module('templateName', []).component('templateName', {
    templateUrl: '',
    controller: ['httpService',
        function templateNameController(httpService) {
            var self = this;
            this.$onInit = function () {

            }//END OF $onInit
        }]//EDN OF CONTROLLER
});
