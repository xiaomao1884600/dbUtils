/**
 * Created by zhanglu1782 on 2016/9/21.
 */
/**
 * 学生信息集合
 */
angular.module('studentInfo', []).component('studentInfo', {
    templateUrl: 'app/views/studentInfo.html',
    controller: ['ngDialog', '$scope', 'httpService', function etlCasesListController(ngDialog, $scope, httpService) {
        var self = this;
        this.mobileNumber = '13548554409';
        this.currentStudent = null;
        this.dataSourceGroup = null;

        $scope.value = {};
        $scope.config = {
            url : 'generalRouter',
            params : {
                objectName: 'com.doubeye.core.dataSource.services.DataSourceGroupService',
                action: 'getAllEDUDataSourceGroups'
            },
            textPropertyName : 'identifier',
            valuePropertyName : 'identifier',
            resultValuePropertyName : 'dataSourceGroup',
            componentName : 'dataSourceGroup'
        };

        self.doSearch = function() {
            this.currentStudent = null;
            httpService.sendRequest(self, 'generalRouter', {
                objectName: 'com.hxsd.services.productLine.edu.StudentInfoService',
                action: 'getStudentByMobile',
                dataSourceGroup : $scope.value.dataSourceGroup,
                mobileNumber : self.mobileNumber
            }, {
                dataPropertyName: 'students'
            }, {
                errorMessage: '查询发生错误,'
            });
        };

        self.showStudentTimeLine = function(student) {
            this.currentStudent = student;
            for (var i = 0; i < student.timeLine.length - 1; i ++) {
                var timeLine = student.timeLine[i];
                var infoType = timeLine.infoType;
                var index = timeLine.index;
                var contents = student.log[infoType];
                var content = contents[index];
                timeLine.dateline = content.dateline;
                timeLine.info = content.info ? content.info : JSON.stringify(content);
            }
        };
    }]
});