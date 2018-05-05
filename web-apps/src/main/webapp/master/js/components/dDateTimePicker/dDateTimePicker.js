/**
 * Created by zhanglu1782 on 2016/11/7.
 * 日志事件范围选择
 */
angular.module('dDateTimePicker', []).component('dDateTimePicker', {
    templateUrl : 'master/js/components/dDateTimePicker/dDateTimePicker.html',
    bindings : {
        startTime: '=',
        endTime : '=',
        dateGroup : '='
    },
    controller : ['$scope', 'httpService', function dGrid($scope, httpService) {
        var self = this;
        this.$onInit = function (operation, row) {
            this.dateGroup = this.dateGroup ? this.dateGroup : 'lastOneHour';
            if (!this.startTime) {
                this.getTimeRange();
            } else {
                this.dateGroup = 'custom';
                self.startTimeDisplay = moment(self.startTime);
                self.endTimeDisplay = moment(self.endTime);
            }
        };

        this.getTimeRange = function () {
            if (self.dateGroup === 'lastSomeMinutes' && !self.lastMinutes) {
                self.lastMinutes = 10;
            }
            var range = com.doubeye.Utils.dateTime.getTimeRange(self.dateGroup, self.startTime, self.endTime, self.lastMinutes);
            self.startTime = range.startTime;
            self.endTime = range.endTime;
            self.startTimeDisplay = moment(self.startTime);
            self.endTimeDisplay = moment(self.endTime);
        };

        //this.dateGroup = this.dateGroup ? this.dateGroup : 'lastOneHour';
        
        this.onRadioChange = function () {
            this.getTimeRange();
        };

        this.onDatePickerChange = function () {
            if (self.dateGroup === 'custom') {
                self.startTime = self.startTimeDisplay.format('YYYY-MM-DD HH:mm:ss');
                self.endTime = self.endTimeDisplay.format('YYYY-MM-DD HH:mm:ss');
            }
        }
    }]
});