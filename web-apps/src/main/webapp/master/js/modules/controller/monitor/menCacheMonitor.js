angular.module('memCacheMonitor', []).component('memCacheMonitor', {
    templateUrl: 'app/views/monitor/memCacheMonitor.html',
    controller: ['httpService', function memCacheMonitorController(httpService) {
        var self = this;
        //this.dataUrl = "generalRouter";
        this.dataUrl = "http://esd.hxsd.test/memcache/getmemcacheprobe";

        this.startTimeDisplay = moment().add(-1, "hours");
        this.endTimeDisplay = moment();

        this.memCacheServers = [{
            "name": "e正式MemCache",
            "param": "10.2.24.38:11211"
        }, {
            "name": "me正式",
            "param": "123_56_154_55:10010"
        }, {
            "name": "edu正式MemCache",
            "param": "10.2.1.73:11211"
        }, {
            "name": "job正式",
            "param": "10.2.1.42:11211"
        }, {
            "name": "e测试",
            "param": "10.2.20.153:11211"
        }, {
            "name": "me测试",
            "param": "123.57.41.95:11211"
        }];

        this.selectedMemCacheServer = this.memCacheServers[0];

        this.parameters = {
            objectName: 'com.hxsd.monitor.memCache.MemCacheMonitorService',
            action: 'getMonitorResult'
        };

        var headerHeight = document.getElementById("header").clientHeight;
        this.chartHeight = (window.innerHeight - headerHeight - 80) / 3;

        /**
         * 图表配置
         * @type {[Object, ...]} 具体配置配置参见com.doubeye.Utils.chart.processLineChartData注释
         * @see com.doubeye.Utils.chart.processLineChartData
         * @private
         */
        this.__chartConfigs = [{
            propertyName : 'counts',
            axisConfigs : [{
                label : 'item数量',
                propertyNameInCollection : 'counts',
                defaultValue : 0
            }]
        }, {
            propertyName : 'usedMem',
            axisConfigs : [{
                label : '已用内存（单位：MB）',
                propertyNameInCollection : 'bytes',
                valueFormatFunction : function (value) {
                    return parseFloat(value) / 1000000;
                },
                defaultValue : 0
            }]
        }, {
            propertyName : 'currentConnections',
            axisConfigs : [{
                label : '连接数',
                propertyNameInCollection : 'curr_connections',
                defaultValue : 0
            }, {
                label : 'MemCache线程数',
                propertyNameInCollection : 'threads',
                defaultValue : 0
            }]
        }, {
            propertyName : 'readWrite',
            axisConfigs : [{
                label : 'read',
                propertyNameInCollection : 'readTime',
                defaultValue : 0
            }, {
                label : 'write',
                propertyNameInCollection : 'writeTime',
                defaultValue : 0
            }]
        }, {
            propertyName : 'readWriteCount',
            axisConfigs : [{
                label : 'read次数',
                propertyNameInCollection : 'deltaCmdGet',
                defaultValue : 0
            }, {
                label : 'write次数',
                propertyNameInCollection : 'deltaCmdSet',
                defaultValue : 0
            }, {
                label : '命中数',
                propertyNameInCollection : 'deltaGetHits',
                defaultValue : 0
            }, {
                label : '未命中数',
                propertyNameInCollection : 'deltaGetMisses',
                defaultValue : 0
            }]
        }, {
            propertyName : 'readWriteBytes',
            axisConfigs : [{
                label : '请求字节数',
                propertyNameInCollection : 'deltaBytesRead',
                defaultValue : 0
            }, {
                label : '结果字节数',
                propertyNameInCollection : 'deltaBytesWrite',
                defaultValue : 0
            }]
        }];


        this.processResult = function (datas) {
            com.doubeye.Utils.chart.processLineChartData(datas, self, 'startdate', self.__chartConfigs);

        };

        this.getData = function () {
            //this.parameters.startTime = this.startTimeDisplay.format('YYYY-MM-DD HH:mm:ss');
            //this.parameters.endTime = this.endTimeDisplay.format('YYYY-MM-DD HH:mm:ss');
            this.parameters.startTime = this.startTime;
            this.parameters.endTime = this.endTime;
            this.parameters.memCache = this.selectedMemCacheServer.param;
            httpService.sendRequest(self, this.dataUrl, this.parameters, {
                callback: self.processResult
            }, {
                errorMessage: '获得图表数据出错'
            }, {}, 'GET');
        };
    }]
});
