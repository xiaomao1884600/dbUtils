angular.module('serverShellMonitor', []).component('serverShellMonitor', {
    templateUrl: 'app/views/monitor/serverShellMonitor.html',
    controller: ['httpService', function serverShellMonitorController(httpService) {
        var self = this;
        this.dataUrl = "http://esd.hxsd.local/probe/getshellprobe";

        this.apps = [{
            "name": "EDU",
            "param": "app_code_e_local"
        }, {
            "name": "ME",
            "param": "app_code_me_local"
        }];

        this.selectedApp = this.apps[0];


        this.parameters = {};


        var headerHeight = document.getElementById("header").clientHeight;
        this.chartHeight = (window.innerHeight - headerHeight - 80) / 3;

        /**
         * 图表配置
         * @type {[Object, ...]} 具体配置配置参见com.doubeye.Utils.chart.processLineChartData注释
         * @see com.doubeye.Utils.chart.processLineChartData
         * @private
         */
        this.__chartShellTopClassConfigs = [{
            propertyName: 'tasks',
            axisConfigs: [{
                label: '总数',
                propertyNameInCollection: 'tasks.total',
                defaultValue: 0
            }, {
                label: '正在运行',
                propertyNameInCollection: 'tasks.running',
                defaultValue: 0
            }, {
                label: '休眠',
                propertyNameInCollection: 'tasks.sleeping',
                defaultValue: 0
            }, {
                label: '停止的',
                propertyNameInCollection: 'tasks.stopped',
                defaultValue: 0
            }, {
                label: '僵尸',
                propertyNameInCollection: 'tasks.zombie',
                defaultValue: 0
            }]
        }, {
            propertyName: 'cpu',
            axisConfigs: [{
                label: 'top',
                propertyNameInCollection: 'top.load_average',
                defaultValue: 0
            }, {
                label: 'us',
                propertyNameInCollection: 'cpu.us',
                defaultValue: 0
            }, {
                label: 'sy',
                propertyNameInCollection: 'cpu.sy',
                defaultValue: 0
            }, {
                label: 'ni',
                propertyNameInCollection: 'cpu.sy',
                defaultValue: 0
            }, /*{
                label: 'id',
                propertyNameInCollection: 'cpu.id',
                defaultValue: 0
            }, */{
                label: 'wa',
                propertyNameInCollection: 'cpu.wa',
                defaultValue: 0
            }, {
                label: 'hi',
                propertyNameInCollection: 'cpu.hi',
                defaultValue: 0
            }, {
                label: 'si',
                propertyNameInCollection: 'cpu.si',
                defaultValue: 0
            }, {
                label: 'st',
                propertyNameInCollection: 'cpu.st',
                defaultValue: 0
            }]
        }, {
            propertyName: 'men',
            axisConfigs: [{
                label: '总数',
                propertyNameInCollection: 'mem.total',
                defaultValue: 0
            }, {
                label: '空闲',
                propertyNameInCollection: 'mem.free',
                defaultValue: 0
            }
            , {
                label: '占用',
                propertyNameInCollection: 'mem.used',
                defaultValue: 0
            }, {
                label: 'buff/cache',
                propertyNameInCollection: 'mem.buff/cache',
                defaultValue: 0
            }]
        }, {
            propertyName: 'swap',
            axisConfigs: [{
                label: '总数',
                propertyNameInCollection: 'mem.total',
                defaultValue: 0
            }, {
                label: '空闲',
                propertyNameInCollection: 'mem.free',
                defaultValue: 0
            }, {
                label: '占用',
                propertyNameInCollection: 'mem.used',
                defaultValue: 0
            }, {
                label: '可用',
                propertyNameInCollection: 'mem.avail',
                defaultValue: 0
            }]
        }];

        this.__chartShellIoClassConfigs = [{
            propertyName: 'iostatUtil',
            axisConfigs: [
                /*{
                label: '%util',
                propertyNameInCollection: 'iostatUtil.%util',
                defaultValue: 0
            }
            */
            ]
        }, {
            propertyName : 'diskUsages',
            axisConfigs : []
        }];

        this.onChartClick = function (params) {
            /*
            var time = new Date(params.name);
            var startTime = new Date(time.setMinutes(time.getMinutes() - 1)).format('yyyy-MM-dd HH:mm:ss');
            var endTime = new Date(time.setMinutes(time.getMinutes() + 2)).format('yyyy-MM-dd HH:mm:ss');
            var url = 'main.html#!/mysqlDetail?startTime=' + startTime + "&endTime=" + endTime + "&probeName=ProcesslistClass&appCode=" + self.selectedApp.param;
            window.open(url, '_blank');
            */
        };


        this.processResult = function (data) {
            if (angular.isArray(data.ShellIoClass.data) && (data.ShellIoClass.data.length > 0)) {
                var tempData = data.ShellIoClass.data[0];
                var fields = com.doubeye.Utils.objectRefactor.getFields(JSON.parse(tempData.iostatUtil));
                fields.forEach(function (element) {
                    self.__chartShellIoClassConfigs[0].axisConfigs.push({
                        label: element,
                        propertyNameInCollection: 'iostatUtil.' + element + '.%util',
                        defaultValue: 0
                    });
                });
                if (tempData.df) {
                    fields = com.doubeye.Utils.objectRefactor.getFields(JSON.parse(tempData.df));
                    fields.forEach(function (element) {
                        self.__chartShellIoClassConfigs[1].axisConfigs.push({
                            label: element,
                            propertyNameInCollection: 'df.' + element + '.Avail',
                            valueFormatFunction: function (value) {
                                return parseInt(value, 10);
                            },
                            defaultValue: 0
                        });
                    });
                }
            }
            com.doubeye.Utils.chart.processLineChartData(data.ShellTopClass.data, self, 'startdate', self.__chartShellTopClassConfigs);
            com.doubeye.Utils.chart.processLineChartData(data.ShellIoClass.data, self, 'startdate', self.__chartShellIoClassConfigs);
        };

        this.getData = function () {
            this.parameters.startTime = this.startTime;
            this.parameters.endTime = this.endTime;
            this.parameters.app_code = this.selectedApp.param;
            httpService.sendRequest(self, this.dataUrl, this.parameters, {
                callback: self.processResult
            }, {
                errorMessage: '获得图表数据出错'
            }, {}, 'GET');
        };
    }]
});
