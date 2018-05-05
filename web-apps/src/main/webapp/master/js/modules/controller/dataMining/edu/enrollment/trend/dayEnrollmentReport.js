/**
 * Created by doubeye
 * 项目中用的组件模板
 * 注意！！！不要将此文件加入到main.html的js加载中
 */
angular.module('dayEnrollment', []).component('dayEnrollment', {
    templateUrl: 'app/views/dataMining/edu/enrollment/trend/dayEnrollmentReport.html',
    controller: ['httpService', 'authorizationService',
        function dayEnrollmentController(httpService, authorizationService) {
            var self = this;
            this.$onInit = function () {
                authorizationService.getChargeInfo(function (allCampus) {
                    self.campuses = allCampus;
                });
                this.results = [];
                this.tabs = [{
                    type : 'all',
                    name : '全部'
                }];
                this.__teamDefines = [{
                    dataId : 'campus_title',
                    label : '校区'
                }, {
                    dataId : 'username',
                    label : '团队'
                }, {
                    dataId : 'enrolled_rate',
                    label : '总报名量'
                }, {
                    dataId : 'enrolled_total',
                    label : '总退班量'
                }, {
                    dataId : 'assign_total',
                    label : '总境报名量'
                }];
                this.__teamDetailDefines = [{
                    dataId : 'username',
                    label : '团队'
                }, {
                    dataId : 'username',
                    label : 'ada'
                }, {
                    dataId : 'enrolled_rate',
                    label : '总报名量'
                }, {
                    dataId : 'enrolled_total',
                    label : '总退班量'
                }, {
                    dataId : 'assign_total',
                    label : '总境报名量'
                }];

                this.__operations = [{
                    id : 'detail',
                    text : '详情',
                    callback : self.drillDown
                }];


            }//END OF $onInit
        }]//EDN OF CONTROLLER
});
