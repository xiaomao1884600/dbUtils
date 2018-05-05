/**
 * Created by zhanglu1782 on 2016/11/7.
 * 日期范围选择
 */
angular.module('dDateRangePicker', []).component('dDateRangePicker', {
    templateUrl : 'master/js/components/dDateRangePicker/dDateRangePicker.html',
    bindings : {
        startTime: '=',
        endTime : '=',
        dateGroup : '=',
        noToday : '<',
        /**
         * 默认的最大日期， 格式为yyyy-MM-dd
         */
        maxEndDate : '<'
    },
    controller : ['$scope', 'httpService', function dGrid() {
        var self = this;
        this.$onInit = function () {
            this.maxDate = null;
            this.setMaxDate = function () {
                self.maxDate = new Date(self.endTime);
            };
            this.dateGroups = [{
                identifier : 'yesterday',
                name : '昨天'
            }, {
                identifier : 'lastDay7',
                name : '近7天'
            }, {
                identifier : 'lastDay30',
                name : '近30天'
            }, {
                identifier : 'custom',
                name : '自定义'
            }];
            if (this.startTime) {
                this.dateGroup = 'custom';
                self.setMaxDate();
            } else {
                this.getTimeRange();
            }
        };

        this.getTimeRange = function (onDateGroupChange) {
            if (!self.dateGroup) {
                self.dateGroup = 'today';
            }
            self.onDateGroupChange = onDateGroupChange;
            var timeRange = com.doubeye.Utils.dateTime.getTimeRange(self.dateGroup, self.startTime, self.endTime, null, 'yyyy-MM-dd');
            self.startTime = timeRange.startTime;
            self.endTime = timeRange.endTime;
            if (this.noToday && ((self.dateGroup === 'lastDay7') || (self.dateGroup === 'lastDay30'))) {
                self.startTime = com.doubeye.Utils.dateTime.addDay(new Date(self.startTime), -1).format('yyyy-MM-dd');
                self.endTime = com.doubeye.Utils.dateTime.addDay(new Date(self.endTime), -1).format('yyyy-MM-dd');
            }
            self.setMaxDate();
        };

        this.onDateChange = function (sender) {
            if (sender === 'start') {
                self.startTime = self.startTime.format('yyyy-MM-dd');
            }
            if (sender === 'end') {
                self.endTime = self.endTime.format('yyyy-MM-dd');
            }
            if (self.endTime < self.startTime) {
                self.startTime = self.endTime;
                self.setMaxDate();
            }
            if (!self.onDateGroupChange) {
                self.dateGroup = 'custom';
                self.onDateGroupChange = false;
            }
        };
    }]
});