/**
 * Created by zhanglu1782 on 2016/10/25.
 */
angular.module('toBespokeInfo', []).component('toBespokeInfo', {
    templateUrl : 'app/views/toBespokeInfo.html',
    controller : ['$http', 'ngDialog', '$cookies', 'httpService', '$scope', 'authorizationService', function toBespokeInfoController($http, ngDialog, $cookies, httpService, $scope, authorizationService) {
        var self = this;
        self.init = function () {
            self.students = [];
            self.adas = [];
            self.currentCampusId = null;
            self.summaries = [];
            self.currentCampusId = -1;
            self.currentADACampusId = -1;
            self.developer = authorizationService.isDeveloper();
        };

        self.getAllCampuses = function() {
            self.init();
            if ($scope.value.dataSource) {
                httpService.sendRequest(self, 'generalRouter', {
                    objectName: 'com.hxsd.services.productLine.e.ToBespokeInfoService',
                    action: 'getAllCampuses',
                    dataSource: $scope.value.dataSource
                }, {
                    dataPropertyName : 'campuses'
                }, {
                    errorMessage : '获得数据源出错,'
                });
            }
        };

        $scope.value = {};
        $scope.config = {
            url : 'generalRouter',
            params : {
                objectName: 'com.doubeye.core.dataSource.services.DataSourceGroupService',
                action: 'getAllEDataSources'
            },
            textPropertyName : 'identifier',
            valuePropertyName : 'identifier',
            resultValuePropertyName : 'dataSource',
            componentName : 'dataSource',
            afterChange : self.getAllCampuses
        };

        self.init();

        self.getToBespokeInfo = function () {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.e.ToBespokeInfoService',
                action: 'getAllToBespokeInfo',
                // dataSource: self.dataSource
                dataSource: $scope.value.dataSource
            }, {
                dataPropertyName : 'students',
                callback : self.generateSummaries
            }, {
                errorMessage : '获取预约通道发生错误,'
            });
        };

        self.processAdas = function() {
            self.adas.forEach(function(ada){
                var message = '';
                if (ada.canGetNewStudent == 0) {
                    if ((parseInt(ada.receivemaxnum, 10) > 0) && (parseInt(ada.receivemaxnum, 10) <= parseInt(ada.countToday, 10))) {
                        message += '超过当日接量上限 ';
                    }
                    if ((parseInt(ada.advisorys, 10) > 0) && (parseInt(ada.advisorys, 10) <= parseInt(ada.countab, 10))) {
                        message += '超过AB量上限 ';
                    }
                    if (ada.noReply >= 3) {
                        message += '未反馈量大于等于3 ';
                    }
                    if (ada.workstatus != "在线") {
                        message += ada.workstatus + " ";
                    }
                    ada.canPickStudent = false;
                } else {
                    var receiveMaxNum = parseInt(ada.receivemaxnum, 10);
                    var countToday = parseInt(ada.countToday, 10);
                    if (receiveMaxNum > 0) {
                        message = "还可接" + (receiveMaxNum - countToday) + "个新量";
                    } else {
                        message = "可无限接量";
                    }
                    ada.canPickStudent = true;
                }
                ada.canGetNewStudent = message;
            });
        };

        self.getECUserStatus = function(campusId) {
            self.currentADACampusId = campusId;

            self.adas = [];
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.e.ToBespokeInfoService',
                action: 'getECUsersInfoByCampusId',
                // dataSource: self.dataSource,
                dataSource: $scope.value.dataSource,
                campusId : campusId
            }, {
                dataPropertyName : 'adas',
                callback : self.processAdas
            }, {
                errorMessage : '获得数据源出错,'
            });
        };

        self.generateSummaries = function() {
            var campusId = '';
            var count = 0;
            self.summaries = [];
            self.students.forEach(function(student) {
                if (campusId == student.campusid) {
                    count ++;
                } else {
                    self.summaries.push({
                        campusId : student.campusid,
                        campus : student.campus
                    });
                    if (self.summaries.length > 1) {
                        self.summaries[self.summaries.length - 2].count = count;
                    }
                    campusId = student.campusid;
                    count = 1;

                }
            });
            if (self.summaries.length > 0) {
                self.summaries[self.summaries.length - 1].count = count;
            }
            self.summaries.push({
                campusId : -1,
                campus : '全部',
                count : self.students.length
            });
        };
        self.setCurrentCampus = function (campusId) {
            self.currentCampusId = campusId;
            self.currentADACampusId = null;
            self.adas = [];
        };
        self.getAllocatedStudents = function (userId) {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.e.ToBespokeInfoService',
                action: 'getAllocatedStudents',
                dataSource: $scope.value.dataSource,
                ecuserId : userId
            }, {
                callback : self.processAllocatedStudents
            }, {
                errorMessage : '获得数据源出错,'
            });
        };
        self.processAllocatedStudents = function (data) {
            $scope.allocatedStudents = data;
            ngDialog.open({
                template: '<table><tr><th>序号</th><th>id</th><th>手机号</th><th>姓名</th><th>类型</th><th>分配时间</th></tr><tr ng-repeat="student in allocatedStudents"><td>{{$index + 1}}</td><td>{{student.studentid}}</td><td>{{student.mobile}}</td><td>{{student.studentname}}</td><td>{{student.assort}}</td><td>{{student.assignDate}}</td></tr></table>',
                plain : true,
                className: 'ngdialog-theme-plain custom-width',
                scope : $scope
            });
        };


        self.getNoReplyedStudent = function (userId) {
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.e.ToBespokeInfoService',
                action: 'getAllNoReplayedStudent',
                dataSource: $scope.value.dataSource,
                ecuserId : userId
            }, {
                callback : self.processNoReplayedStudents
            }, {
                errorMessage : '获得数据源出错,'
            });
        };
        self.processNoReplayedStudents = function (data) {
            $scope.noReplayedStudents = data;
            ngDialog.open({
                template: '<table><tr><th>序号</th><th>id</th><th>手机号</th><th>姓名</th><th>类型</th><th>分配时间</th></tr><tr ng-repeat="student in noReplayedStudents"><td>{{$index + 1}}</td><td>{{student.studentid}}</td><td>{{student.mobile}}</td><td>{{student.studentname}}</td><td>{{student.assort}}</td><td>{{student.assignDate}}</td></tr></table>',
                plain : true,
                className: 'ngdialog-theme-plain custom-width',
                scope : $scope
            });
        };
        /**
         * 判断是否为公开的url或非开发，如果是，则隐藏数据源，并将数据源设置为生产环境
         */
        if (window.location.href.indexOf('public.html') > 0 || !self.developer) {
            self.hideDataSource = true;
            $scope.value.dataSource = 'E-PRODUCT';
            $cookies.put('eDataSource', 'E-PRODUCT');
            self.getAllCampuses();
        }
    }]
});