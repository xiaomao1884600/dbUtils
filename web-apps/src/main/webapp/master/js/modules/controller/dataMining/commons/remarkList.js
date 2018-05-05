/**
 * Created by doubeye
 * 项目中用的组件模板
 * 注意！！！不要将此文件加入到main.html的js加载中
 */
angular.module('remarkList', []).component('remarkList', {
    templateUrl: 'app/views/dataMining/commons/remarkList.html',
    bindings : {
        /**
         * 用户
         */
        user : '<',
        /**
         * 备注列表
         */
        list : '<'
    },
    controller: ['httpService',
        function remarkListController(httpService) {
            var self = this;
            this.$onInit = function () {

            }//END OF $onInit
        }]//EDN OF CONTROLLER
});
