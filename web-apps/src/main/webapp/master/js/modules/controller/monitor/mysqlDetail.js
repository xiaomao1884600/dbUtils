angular.module('mysqlDetail', []).component('mysqlDetail', {
    templateUrl: 'app/views/monitor/mysqlDetail.html',
    bindings : {
        startTime : '<',
        endTime : '<',
        appCode : '<',
        probeName : '<',
        detailContents : '<'
    },
    controller: ['httpService', '$location', function mysqlDetailController(httpService, $location) {
        var self = this;

        this.$onInit = function (){
            if ($location.search().startTime) {
                self.startTime = $location.search().startTime;
            }
            if ($location.search().endTime) {
                self.endTime = $location.search().endTime;
            }
            if ($location.search().appCode) {
                self.appCode = $location.search().appCode;
            }
            if ($location.search().probeName) {
                self.probeName = $location.search().probeName;
            }
            if (!self.detailContents) {
                this.getData();
            }

            this.columnDefines = [{
                dataId : 'HOST',
                label : '客户端主机名称'
            }, {
                dataId : 'USER',
                label : '数据库用户'
            }, {
                dataId : 'DB',
                label : '连接的schema'
            }, {
                dataId : 'ID',
                label : '线程id'
            }, {
                dataId : 'COMMAND',
                label : '命令类型'
            }, {
                dataId : 'STATE',
                label : '状态'
            }, {
                dataId : 'TIME',
                label : '持续时间'
            }, {
                dataId : 'INFO',
                label : '语句'
            }];
        };


        this.dataUrl = "http://esd.hxsd.local/mysql/getserverprobenameinfo";

        this.getData = function () {
            var postParams = {
                app_code : self.appCode,
                startTime : self.startTime,
                endTime : self.endTime,
                probename : self.probeName
            };
            httpService.sendRequest(self, this.dataUrl, postParams, {
                callback: function (data) {
                    self.detailContents = data[self.probeName]['data'];
                    self.selectedIndex = 0;
                }
            }, {
                errorMessage: '获得数据库详情出错'
            }, {}, 'GET');
        };
    }]
});
