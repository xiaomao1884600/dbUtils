/**
 * Created by doubeye
 * ADA招生任务目标设置
 */
angular.module('campusGoalManage', []).component('campusGoalManage', {
    templateUrl: 'app/views/dataMining/edu/enrollment/goal/campusGoalManage.html',
    controller: ['httpService',
        function campusGoalManageController(httpService) {
            var self = this;
            this.$onInit = function () {
                this.dataUrl = "http://record.hxsd.local/api/edu/get_campus_term_enrolled_goal";
                this.saveUrl = "http://record.hxsd.local/api/edu/set_campus_term_enrolled_goal";
                /**
                 * 期数
                 * @type {Array}
                 */
                self.terms = [];
                /**
                 * 目标
                 * @type {Array}
                 */
                self.goals = [];

                this.save = function (record) {
                    var goals = [{
                        termid : self.selectedTerm.termid,
                        campus_id : record.campus_id,
                        enrolled_goal : record.enrolled_goal
                    }];
                    httpService.sendRequest(self, self.saveUrl, {
                        goals : goals,
                        _noProcessingDialog : true
                    }, {

                    });

                };
                this.getData = function () {
                    httpService.sendRequest(self, self.dataUrl, {
                        termid : self.selectedTerm.termid
                    }, {
                        dataPropertyName : 'goals',
                        resultPropertyName : 'data'
                    });
                };
            }//END OF $onInit
        }]//EDN OF CONTROLLER
});
