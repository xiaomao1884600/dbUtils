/**
 * 电话统计分析
 * @see 本功能接口文档
 */
angular.module('statByCampus', []).component('statByCampus', {
    templateUrl: 'app/views/dataMining/edu/recordAnalyze/statByCampus.html',
    controller: ['httpService',
        function statByCampusController(httpService) {

            var self = this;

            this.$onInit = function () {
                this.url = 'http://record.hxsd.local/api/record/get_campuscount';
                this.campusUrl = 'http://record.hxsd.local/api/record/get_record_campus_list';
                this.gettingResult = false;
                this.dimensions = [{
                    id : 1,
                    dataKeyName : 'name',
                    name : '各校区指定月份汇总',
                    parameterNames : [{
                        propertyName : 'selectedMonth.id',
                        parameterName : 'yearMonth'}]
                }, {
                    id : 2,
                    dataKeyName : 'month',
                    name : '单个校区各月汇总',
                    parameterNames : [{
                        propertyName : 'selectedCampus.campusid',
                        parameterName : 'campusid'}, {
                        propertyName : 'selectedYear',
                        parameterName : 'year'
                    }]
                }];

                this.selectedDimension = this.dimensions[0];
                /**
                 * 结果的维度，由于改变维度下拉后，可能没有获得数据，需要保存此属性作为正确下钻事件的条件，该值在成果获得结果后更新为下拉条件
                 */
                self.resultDimention = this.selectedDimension;

                this.forceDiscardCache = 0;
                this.chartHeight = (window.innerHeight - 135) / 3;
                this.chartWidth = (window.innerWidth - 300) / 2 - 10;

                this.monthes = [];

                this.generateYearMonth = function () {
                    var date = new Date();
                    this.monthes.push({
                        id : date.format('yyyy-MM'),
                        name : date.format('yyyy年MM月')
                    });
                    for (var i = 11; i >= 1; i --) {
                        date.setMonth(date.getMonth() - 1);
                        this.monthes.push({
                            id : date.format('yyyy-MM'),
                            name : date.format('yyyy年MM月')
                        });
                    }
                };

                this.campuses = [];
                this.years = [];

                //各个图使用的指标坐标轴归一因子
                this.factors = {
                    chart1RatioFactor : 50000,
                    chart2RatioFactor : 1500,
                    chart3Factor : 10000,
                    chart4Factor : 15000
                };
                var thisYear = new Date().getFullYear();
                for (var i = 2017; i <= thisYear; i ++) {
                    this.years.push(i);
                }


                this.generateYearMonth();

                this.selectedMonth = this.monthes[0];
                this.rangeConfig = [{
                    min : 1,
                    max : 60,
                    step : 10
                }, {
                    min : 61,
                    max : 1200,
                    step : 60
                }];
                this.processRangeConfig = function () {
                    var result = [];
                    if (self.rangeConfig.length > 0) {
                        self.rangeConfig.forEach(function (element) {
                            var steps = (element.max - element.min + 1) / element.step;
                            for (var i = 1; i <= steps; i ++) {
                                result.push((element.min + (i - 1) * element.step) + '-' + (element.min + i * element.step - 1));
                            }
                        });
                        result.push((self.rangeConfig[self.rangeConfig.length - 1].max + 1) + '- ~');
                    }
                    return result;
                };
                this.__chartConfigs = [{//通话数
                    propertyName : 'callCounts',
                    tipFormatter : function (params) {
                        var info = params[0].name + '<br/>';
                        info += params[0].marker + params[0].seriesName + params[0].value + '<br/>';
                        info += params[1].marker + params[1].seriesName + params[1].value + '<br/>';
                        info += params[2].marker + params[2].seriesName + (Math.round(params[2].value / self.factors.chart1RatioFactor * 100) / 100) + '<br/>';
                        return info;
                    },
                    axisConfigs : [{
                        label: '通话总数',
                        chartType : 'bar',
                        propertyNameInCollection: 'records.all.t_calls',
                        defaultValue: 0
                    }, {
                        label: '有效通话总数',
                        chartType : 'bar',
                        propertyNameInCollection: 'records.valid.t_calls',
                        defaultValue: 0
                    }, {
                        label: '有效无效占比',
                        propertyNameInCollection: 'records.valid.t_calls',
                        chartType : 'line',
                        valueFormatFunction : function (value, record) {
                            return value / com.doubeye.Utils.objectRefactor.getValue(record, 'records.all.t_calls') * self.factors.chart1RatioFactor;
                        },
                        defaultValue: 0
                    }]
                }, {//通话时长
                    propertyName : 'callLength',
                    tipFormatter : function (params) {
                        var info = params[0].name + '<br/>';
                        info += params[0].marker + params[0].seriesName + params[0].value + '<br/>';
                        info += params[1].marker + params[1].seriesName + params[1].value + '<br/>';
                        info += params[2].marker + params[2].seriesName + (Math.round(params[2].value / self.factors.chart2RatioFactor * 100) / 100) + '<br/>';
                        return info;
                    },
                    axisConfigs : [{
                        label: '通话总时长(小时)',
                        chartType : 'bar',
                        propertyNameInCollection: 'records.all.t_time',
                        defaultValue: 0
                    }, {
                        label: '有效通话总时长（小时）',
                        chartType : 'bar',
                        propertyNameInCollection: 'records.valid.t_time',
                        defaultValue: 0
                    }, {
                        label: '无效有效占比',
                        propertyNameInCollection: 'records.valid.t_time',
                        chartType : 'line',
                        valueFormatFunction : function (value, record) {
                            return value / com.doubeye.Utils.objectRefactor.getValue(record, 'records.all.t_time') * self.factors.chart2RatioFactor;
                        },
                        defaultValue: 0
                    }]
                }, {//拨入播出数
                    propertyName : 'callDirectionCounts',
                    tipFormatter : function (params) {
                        var info = params[0].name + '<br/>';
                        info += params[0].marker + params[0].seriesName + params[0].value + '<br/>';
                        info += params[1].marker + params[1].seriesName + params[1].value * 1 + '<br/>';
                        //info += params[2].marker + params[2].seriesName + (Math.round(params[2].value / 100000 * 100) / 100) + '<br/>';
                        info += params[2].marker + params[2].seriesName + (Math.round(params[2].value / self.factors.chart3RationFactor * 100) / 100) + '<br/>';
                        return info;
                    },
                    axisConfigs : [{
                        label: '拨入总数',
                        chartType : 'bar',
                        propertyNameInCollection: 'records.all.in_calls',
                        defaultValue: 0
                    }, {
                        label: '播出总数',
                        chartType : 'bar',
                        propertyNameInCollection: 'records.all.out_calls',
                        valueFormatFunction : function (value) {
                            return value / 1;
                        },
                        defaultValue: 0
                    }, {
                        label: '拨入播出占比',
                        propertyNameInCollection: 'records.all.in_calls',
                        chartType : 'line',
                        valueFormatFunction : function (value, record) {
                            return value / com.doubeye.Utils.objectRefactor.getValue(record, 'records.all.out_calls') * self.factors.chart3RationFactor;
                        },
                        defaultValue: 0
                    }]
                }, {//拨入播出时长
                    propertyName : 'callDirectionLength',
                    tipFormatter : function (params) {
                        var info = params[0].name + '<br/>';
                        info += params[0].marker + params[0].seriesName + params[0].value + '<br/>';
                        info += params[1].marker + params[1].seriesName + params[1].value + '<br/>';
                        info += params[2].marker + params[2].seriesName + (Math.round(params[2].value / self.factors.chart4Factor * 100) / 100) + '<br/>';
                        return info;
                    },
                    axisConfigs : [{
                        label: '拨入时长(小时)',
                        chartType : 'bar',
                        propertyNameInCollection: 'records.all.in_time',
                        defaultValue: 0
                    }, {
                        label: '拨出时长（小时）',
                        chartType : 'bar',
                        propertyNameInCollection: 'records.all.out_time',
                        defaultValue: 0
                    }, {
                        label: '拨入播出时长占比',
                        propertyNameInCollection: 'records.all.in_time',
                        chartType : 'line',
                        valueFormatFunction : function (value, record) {
                            return value / com.doubeye.Utils.objectRefactor.getValue(record, 'records.all.out_time') * self.factors.chart4Factor;
                        },
                        defaultValue: 0
                    }]
                }, {//平均通话时长（秒）
                    propertyName : 'avgLength',
                    axisConfigs : [{
                        label: '所有',
                        chartType : 'line',
                        propertyNameInCollection: 'records.avg.all_avg_time',
                        defaultValue: 0
                    }, {
                        label: '拨入',
                        chartType : 'line',
                        propertyNameInCollection: 'records.all.in_time',
                        valueFormatFunction : function (value, record) {
                            return parseFloat(value) * 3600 / parseInt(com.doubeye.Utils.objectRefactor.getValue(record, 'records.all.in_calls'), 10);
                        },
                        defaultValue: 0
                    }, {
                        label: '拨出',
                        propertyNameInCollection: 'records.all.out_time',
                        chartType : 'line',
                        valueFormatFunction : function (value, record) {
                            return parseFloat(value) * 3600 / parseInt(com.doubeye.Utils.objectRefactor.getValue(record, 'records.all.out_calls'), 10);
                        },
                        defaultValue: 0
                    }]
                }];
                this.getCampus();
            };

            this.onSearchClick = function () {
                self.forceDiscardCache = 1;
                self.getData();
            };

            this.getCampus = function () {
                httpService.sendRequest(self, self.campusUrl, {}, {
                    dataPropertyName : 'campuses',
                    resultPropertyName : 'data.campusList',
                    callback : function (data) {
                        //self.campuses = data.data.campusList;
                        self.getData();
                    }
                }, {
                    errorMessage: '获得所有校区出错'
                });
            };

            self.computeFactors = function (datas) {
                //计算有效所有通话占比的因子
                var maxAllCall = 0, maxCallCount = 0, maxOutCalls = 0, maxCallOutLength = 0;
                datas.forEach(function (campus) {
                    maxAllCall = Math.max(maxAllCall, com.doubeye.Utils.objectRefactor.getValue(campus, 'records.all.t_calls'));
                    maxCallCount = Math.max(maxCallCount, com.doubeye.Utils.objectRefactor.getValue(campus, 'records.all.t_time'));
                    maxOutCalls = Math.max(maxOutCalls, com.doubeye.Utils.objectRefactor.getValue(campus, 'records.all.out_calls'));
                    maxCallOutLength = Math.max(maxCallOutLength, com.doubeye.Utils.objectRefactor.getValue(campus, 'records.all.out_time'));
                });
                self.factors.chart1RatioFactor = maxAllCall;
                self.factors.chart2RatioFactor = maxCallCount * 0.8;
                //比值为0.0X,放大10倍
                self.factors.chart3RationFactor = maxOutCalls * 10;
                //比值为0.0X,放大10倍
                self.factors.chart4Factor = maxCallOutLength * 10;
            };

            this.processResult = function (datas) {
                self.gettingResult = false;
                //遍历结果集，获得每个指标的缩放因子
                self.computeFactors(datas);
                com.doubeye.Utils.chart.processLineChartData(datas, self, self.selectedDimension.dataKeyName, self.__chartConfigs);
                self.ranges = {
                    axis : [],
                    data : [],
                    tipFormatter : function (params) {
                        var name = params[0].name;
                        var info = name + '<br/>';
                        for (var i = 0; i < datas.length; i ++) {
                            var value = datas[i]['records']['recordRange'][name];
                            if (value) {
                                info += datas[i][self.selectedDimension.dataKeyName] + ':' + value + '<br/>';
                            }
                        }
                        return info;
                    }
                };
                //构建范围数组

                self.ranges.axis = self.processRangeConfig();

                datas.forEach(function (element) {
                    self.ranges.data.push({
                        //label : self.selectedDimension.id === 1 ? element.name : element.month,
                        label : element[self.selectedDimension.dataKeyName],
                        chartType : 'line',
                        data : []
                    });
                });

                for (var i = 0; i < datas.length; i ++) {
                    self.ranges.axis.forEach(function (axis) {
                        self.ranges.data[i].data.push(datas[i].records.recordRange[axis]);
                    });
                }

                self.getTitle();
                self.resultDimention = self.selectedDimension;
            };

            this.getData = function () {
                self.gettingResult = true;
                var parameters = {
                    recordRange : self.rangeConfig,
                    validTime : 20,
                    getLatestData : self.forceDiscardCache
                };
                this.selectedDimension.parameterNames.forEach(function (value) {
                    parameters[value.parameterName] = com.doubeye.Utils.objectRefactor.getValue(self, value.propertyName);
                });
                httpService.sendRequest(self, self.url, parameters, {
                    callback: self.processResult
                }, {
                    errorMessage: '获得电话统计数据出错'
                });
            };

            this.getTitle = function () {
                if (self.selectedDimension.id === 1) {
                    self.title = self.selectedMonth.id + '所有校区通话数据统计';
                } else if (self.selectedDimension.id === 2) {
                    self.title = self.selectedYear + '年' + self.selectedCampus.name + '通话数据统计';
                }
            };

            this.onChartClick = function (params) {
                if (self.resultDimention.id === 1) {
                    var campusName = params.name;
                    self.selectedCampus = com.doubeye.Utils.array.getObjectFromArray({name : campusName}, self.campuses);
                    self.selectedDimension = self.dimensions[1];
                    self.selectedYear = parseInt(com.doubeye.Utils.String.getStringBefore(self.selectedMonth.id, "-"));

                    self.getData();
                }
            };

            /*
            this.onMonthChanged = function () {
                self.forceDiscardCache = 0;
                self.getData();
            }
            */
        }]
});
