angular.module('mysqlMonitor', []).component('mysqlMonitor', {
    templateUrl: 'app/views/monitor/mysqlMonitor.html',
    controller: ['httpService', function mysqlMonitorController(httpService) {
        var self = this;
        //this.dataUrl = "generalRouter";
        this.dataUrl = "http://esd.hxsd.local/probe/getmysqlprobe";

        //this.startTimeDisplay = moment().add(-1, "hours");
        //this.endTimeDisplay = moment();

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
        this.__chartConfigs = [{
            propertyName : 'transactions',
            axisConfigs : [{
                label : '提交事务数',
                propertyNameInCollection : 'Com_commit',
                defaultValue : 0
            }, {
                label : '回滚事务数',
                propertyNameInCollection : 'Com_rollback',
                defaultValue : 0
            }]
        }, {
            propertyName : 'updateDelete',
            axisConfigs : [{
                label : '查询次数',
                propertyNameInCollection : 'Com_select',
                defaultValue : 0
            }, {
                label : '插入次数',
                propertyNameInCollection : 'Com_insert',
                defaultValue : 0
            }, {
                label : '更新次数',
                propertyNameInCollection : 'Com_update',
                defaultValue : 0
            }, {
                label : '删除次数',
                propertyNameInCollection : 'Com_delete',
                defaultValue : 0
            }]
        }, {
            propertyName : 'threads',
            axisConfigs : [{
                label : '连接的客户端',
                propertyNameInCollection : 'Threads_connected',
                defaultValue : 0
            }, {
                label : '运行的客户端',
                propertyNameInCollection : 'Threads_running',
                defaultValue : 0
            }, {
                label : '异常终止的客户端',
                propertyNameInCollection : 'Aborted_clients',
                defaultValue : 0
            }, {
                label : '创建的线程数',
                propertyNameInCollection : 'Threads_created',
                defaultValue : 0
            }, {
                label : '缓存的线程数',
                propertyNameInCollection : 'Threads_cached',
                defaultValue : 0
            }]
        }, {
            propertyName: 'queries',
            axisConfigs: [{
                label: '接收查询数量',
                propertyNameInCollection: 'Questions',
                defaultValue: 0
            }, {
                label: 'openTable次数',
                propertyNameInCollection: 'Opened_tables',
                defaultValue: 0
            }, {
                label: '无主键Join',
                propertyNameInCollection: 'Select_full_join',
                defaultValue: 0
            }, {
                label: '全表扫描',
                propertyNameInCollection: 'Select_scan',
                defaultValue: 0
            }, {
                label: '慢查询',
                propertyNameInCollection: 'Slow_queries',
                defaultValue: 0
            }]
        }, {
            propertyName: 'queries',
            axisConfigs: [{
                label: '接收查询数量',
                propertyNameInCollection: 'Questions',
                defaultValue: 0
            }, {
                label: 'openTable次数',
                propertyNameInCollection: 'Opened_tables',
                defaultValue: 0
            }, {
                label: '无主键Join',
                propertyNameInCollection: 'Select_full_join',
                defaultValue: 0
            }, {
                label: '全表扫描',
                propertyNameInCollection: 'Select_scan',
                defaultValue: 0
            }, {
                label: '慢查询',
                propertyNameInCollection: 'Slow_queries',
                defaultValue: 0
            }]
        }, {
            propertyName: 'handlers',
            axisConfigs: [{
                label: 'read_first',
                propertyNameInCollection: 'Handler_read_first',
                defaultValue: 0
            }, {
                label: 'update',
                propertyNameInCollection: 'Handler_update',
                defaultValue: 0
            }, {
                label: 'write',
                propertyNameInCollection: 'Handler_write',
                defaultValue: 0
            }, {
                label: 'read_next',
                propertyNameInCollection: 'Handler_read_next',
                defaultValue: 0
            }, {
                label: 'read_prev',
                propertyNameInCollection: 'Handler_read_prev',
                defaultValue: 0
            }, {
                label: 'read_rnd',
                propertyNameInCollection: 'Handler_read_rnd',
                defaultValue: 0
            }, {
                label: 'read_rnd_next',
                propertyNameInCollection: 'Handler_read_rnd_next',
                defaultValue: 0
            }]
        }, {
            propertyName: 'ps',
            axisConfigs: [{
                label: 'TPS',
                propertyNameInCollection: 'TBS',
                defaultValue: 0
            }, {
                label: 'BPS',
                propertyNameInCollection: 'BPS',
                defaultValue: 0
            }]
        }];

        this.onChartClick = function (params) {
            var time = new Date(params.name);
            var startTime = new Date(time.setMinutes(time.getMinutes() - 1)).format('yyyy-MM-dd HH:mm:ss');
            var endTime = new Date(time.setMinutes(time.getMinutes() + 2)).format('yyyy-MM-dd HH:mm:ss');
            var url = 'main.html#!/mysqlDetail?startTime=' + startTime + "&endTime=" + endTime + "&probeName=ProcesslistClass&appCode=" + self.selectedApp.param;
            window.open(url, '_blank');
        };


        this.processResult = function (datas) {
            com.doubeye.Utils.chart.processLineChartData(datas.ShowStatusClass.data, self, 'startdate', self.__chartConfigs);
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
