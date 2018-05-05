/**
 * Created by zhanglu1782 on 2016/11/7.
 * 日期范围选择
 */
angular.module('dSimpleDateTimePicker', []).component('dSimpleDateTimePicker', {
    templateUrl : 'master/js/components/dSimpleDateTimePicker/dSimpleDateTimePicker.html',
    bindings : {
        startTime: '=',
        endTime : '=',
        dateGroup : '='
    },
    controller : ['$scope', 'httpService', function dGrid() {
        var self = this;
        this.$onInit = function () {
            this.maxDate = null;
            this.startHourPart = 0;
            this.startMinutePart = 0;
            this.startSecondPart = 0;
            this.endHourPart = 0;
            this.endMinutePart = 0;
            this.endSecondPart = 0;
            this.dateGroups = [{
                identifier : 'today',
                name : '今天'
            }, {
                identifier : 'yesterday',
                name : '昨天'
            }, {
                identifier : 'last24Hours',
                name : '过去24小时'
            }, {
                identifier : 'lastDay7',
                name : '近7天'
            }, {
                identifier : 'custom',
                name : '自定义'
            }];
            if (this.startTime) {
                this.dateGroup = 'custom';
                this.startDate = com.doubeye.Utils.String.getStringBefore(this.startTime, ' ');
                var timePart = com.doubeye.Utils.String.getStringAfter(this.startTime, ' ');
                var parts = timePart.split(':');
                if (parts[0]) {
                    this.startHourPart = parseInt(parts[0]);
                }
                if (parts[1]) {
                    this.startMinutePart = parseInt(parts[1]);
                }
                if (parts[2]) {
                    this.startSecondPart = parseInt(parts[2]);
                }
                this.endDate = com.doubeye.Utils.String.getStringBefore(this.endTime, ' ');
                timePart = com.doubeye.Utils.String.getStringAfter(this.endTime, ' ');
                parts = timePart.split(':');
                if (parts[0]) {
                    this.endHourPart = parseInt(parts[0]);
                }
                if (parts[1]) {
                    this.endMinutePart = parseInt(parts[1]);
                }
                if (parts[2]) {
                    this.endSecondPart = parseInt(parts[2]);
                }
                self.maxDate = new Date(self.endTime);
            } else {
                this.getTimeRange();
            }
        };

        this.getTimeRange = function (onDateGroupChange) {
            if (!self.dateGroup) {
                self.dateGroup = 'today';
            }
            if (self.dateGroup !== 'custom') {
                self.onDateGroupChange = onDateGroupChange;
                var timeRange = com.doubeye.Utils.dateTime.getTimeRange(self.dateGroup, self.startTime, self.endTime, null, true);
                self.startDate = timeRange.startTime.format('yyyy-MM-dd');
                self.startHourPart = timeRange.startTime.getHours();
                self.startMinutePart = timeRange.startTime.getMinutes();
                self.startSecondPart = timeRange.startTime.getSeconds();
                self.endDate = timeRange.endTime.format('yyyy-MM-dd');
                self.endHourPart = timeRange.endTime.getHours();
                self.endMinutePart = timeRange.endTime.getMinutes();
                self.endSecondPart = timeRange.endTime.getSeconds();
                self.unitDateTime();
            }
            self.maxDate = new Date(self.endDate);
        };

        this.onDateChange = function (sender) {
            if (sender === 'start') {
                self.startDate = self.startDate.format('yyyy-MM-dd');
            } else {
                self.endDate = self.endDate.format('yyyy-MM-dd');
            }
            self.unitDateTime();
        };

        this.onTimeChange = function () {
            self.unitDateTime();
        };
        this.unitDateTime = function () {
            self.startTime = self.startDate + ' ' +
                (self.startHourPart <= 9 ? '0' + self.startHourPart : self.startHourPart) + ':' +
                (self.startMinutePart <= 9 ? '0' + self.startMinutePart : self.startMinutePart) + ':' +
                (self.startSecondPart <= 9 ? '0' + self.startSecondPart : self.startSecondPart)
            ;
            self.endTime = self.endDate + ' ' +
                (self.endHourPart <= 9 ? '0' + self.endHourPart : self.endHourPart) + ':' +
                (self.endMinutePart <= 9 ? '0' + self.endMinutePart : self.endMinutePart) + ':' +
                (self.endSecondPart <= 9 ? '0' + self.endSecondPart : self.endSecondPart);
            if (new Date(self.endTime) < new Date(self.startTime)) {
                self.startDate = self.endDate;
                self.startHourPart = self.endHourPart;
                self.startMinutePart = self.endMinutePart;
                self.startSecondPart = self.endSecondPart;
                self.startTime = self.endTime;
                self.maxDate = new Date(self.endDate);
            }
            if (!self.onDateGroupChange) {
                self.dataGroup = 'custom';
                self.onDateGroupChange = false;
            }
        }
    }]
});