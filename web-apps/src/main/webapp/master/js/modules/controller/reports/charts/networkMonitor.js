/**
 * Created by zhanglu1782 on 2016/7/14.
 */

angular.module('networkMonitor', []).component('networkMonitor', {
    templateUrl : 'app/views/reports/charts/networkMonitor.html',
    controller : ['httpService', function networkMonitonController(httpService) {
        var self = this;
        this.dataUrl = "generalRouter";

        this.startTimeDisplay = moment().add(-1, "days");
        this.endTimeDisplay = moment();

        this.monitoredUrls = ["www.baidu.com", "www.163.com", "www.qq.com", "www.aliyun.com"];

        var headerHeight = document.getElementById("header").clientHeight;
        this.chartHeight = (window.innerHeight - headerHeight - 70);

        this.parameters = {
            objectName : 'com.hxsd.monitor.network.NetworkMonitorService',
            action : 'getMonitorData',
            url : 'www.aliyun.com'
            //startTime : '2017-07-31 15:20:32',
            //endTime : '2017-08-01 15:20:32'
        };

        this.processResult = function(datas) {
            self.data = datas;
        };

        this.getData = function () {
            /*
            this.parameters.startTime = this.startTimeDisplay.format('YYYY-MM-DD HH:mm:ss');
            this.parameters.endTime = this.endTimeDisplay.format('YYYY-MM-DD HH:mm:ss');
            */
            this.parameters.startTime = this.startTime;
            this.parameters.endTime = this.endTime;
            httpService.sendRequest(self, this.dataUrl, this.parameters, {
                callback : self.processResult
            }, {
                errorMessage : '获得图表数据出错'
            });
        };

    }]
});
