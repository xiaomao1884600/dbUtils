/**
 * 对echart中曲线图的封装
 * @version 1.0.0
 */
angular.module('lineChart', []).component('lineChart', {
    template : '<div e-chart e-data="$ctrl.option" style="height:{{$ctrl.chartHeight}}px;width:{{$ctrl.chartWidth}}px;" id="{{$ctrl.effectChartId}}"></div>',
    bindings : {
        options : '<',
        chartId : '<',
        idPrefix : '<',
        title : '<',
        titlePosition : '@',
        /**
         * 是否显示网格线，false
         */
        showSplitLine : '@',
        subtext : '<',
        remarkConfig : '<',
        chartHeight : '<',
        chartWidth : '<',
        onClick : '<',
        tipFormatter : '<',
        forceReRender : '<',
        onMouseOver : '<',
        beforeRender : '<',
        /**
         * y坐标格式化器
         */
        yAxisFormatter : '<',
        /**
         * x轴label的旋转角度
         */
        axisLabelRotate : '@'
    },
    controller : ['httpService', '$scope',function lineCharController(httpService, $scope) {
        var self = this;
        this.$onInit = function () {
            this.effectChartId = this.idPrefix ? this.idPrefix + this.chartId : this.chartId;
        };
        this.options = {
            data : []
        };
        this.chart = null;
        this.processChartData = function (options) {
            if (!self.options) {
                return;
            }
            self.renderChart();
        };
        this.generateLegends = function () {
            self.legendData = [];
            self.selectedLegend = {};
            if (self.options && angular.isArray(self.options.data)) {
                self.options.data.forEach(function (element) {
                    self.legendData.push({
                        name : element.name,
                        icon : 'circle'
                    });
                    self.selectedLegend[element.label] = true;
                });
            }
        };
        this.generateChartSeries = function () {
            self.chartSeries = [];
            if (self.options && angular.isArray(self.options.data)) {
                self.options.data.forEach(function (element) {
                    //element.name = element.label;
                    element.type = element.chartType ? element.chartType : 'line';

                    if (element.itemStyle) {
                        delete element.label;
                    } else {
                        element.label = {
                            normal: {
                                show: false,
                                position: 'top'//值显示
                            }
                        }
                    }

                    /*
                    self.chartSeries.push({
                        name : element.label,
                        type: element.chartType ? element.chartType : 'line',
                        markLine : element.markLine,
                        itemStyle : element.itemStyle,
                        stack : element.stack,
                        smooth:true,
                        symbolSize:14,
                        data : element.data,
                        label: element.itemStyle ? null : {
                            normal: {
                                show: false,
                                    position: 'top'//值显示
                            }
                        }
                    });
                    */
                    self.chartSeries.push(element);
                });
            }
        };
        this.renderChart = function () {
            self.generateLegends();
            self.generateChartSeries();
            self.chartOption = {
                title : {
                    text: self.title,
                    x: self.titlePosition ? self.titlePosition : 'right'
                },
                subText : self.subtext,
                "toolbox": {
                    "show": true,
                    "feature": {
                        "restore": {},
                        "saveAsImage": {}
                    }
                },
                dataZoom: [{
                    type: 'slider',
                    height : 18,
                    bottom : 3,
                    start : 0,
                    end : 100
                }, {
                    type: 'inside'
                }],
                backgroundColor:'#ffffff',
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    x: 'left',
                    padding: [25, 20,0,20],
                    data: self.legendData,
                    selected: self.selectedLegend,
                    textStyle:{
                        color:'#000000'
                    }
                },
                grid: {
                    left: '3%',
                    right: '3%',
                    bottom: self.remarkConfig ? (self.remarkConfig.bottom +  self.remarkConfig.shape.height) : 30,
                    top: '21%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    axisTick:{show:true},
                    axisLabel:{
                        textStyle:{
                            color:'#000000'
                        },
                        "rotate": self.axisLabelRotate
                    },
                    splitLine:{//网格线
                        show: self.showSplitLine,
                        lineStyle:{
                            color:['#000000'],
                            type:'solid'
                        }
                    },
                    data : self.options.axis
                },
                yAxis: {
                    //min:0,
                    axisTick:{show:true},
                    axisLine:{
                        show:true
                    },
                    axisLabel:{
                        textStyle:{
                            color:'#000000'
                        },
                        formatter : self.yAxisFormatter
                    },
                    splitLine:{//网格线
                        show: self.showSplitLine,
                        lineStyle:{
                            color:['#000000'],
                            type:'solid'
                        }
                    }
                },
                series: self.chartSeries
            };
            if (angular.isFunction(self.options.tipFormatter)) {
                self.chartOption.tooltip.formatter = self.options.tipFormatter;
            }
            if (!self.chart) {
                var element = document.getElementById(self.effectChartId);
                if (!element) {
                    console.warn('find no container ' + self.effectChartId);
                    return;
                }
                self.chart = echarts.init(element);
                if (angular.isFunction(self.onClick)) {
                    self.chart.on('click', function (params) {
                        self.onClick(params);
                    });
                }
                if (angular.isFunction(self.onMouseOver)) {
                    self.chart.on('mouseover', function (params) {
                        self.onMouseOver(params);
                    });
                }
            }
            if (self.forceReRender) {
                self.chart.clear();
            }
            if (angular.isFunction(self.beforeRender)) {
                self.beforeRender(self.chartOption);
            }
            if (self.remarkConfig) {
                this.chartOption.graphic = [
                    {
                        type: 'group',
                        left: '3%',
                        bottom : self.remarkConfig.bottom,
                        children: [
                            {
                                type: 'rect',
                                left: '0',
                                top: '0',
                                shape: self.remarkConfig.shape,
                                style: {
                                    fill: '#fff',
                                    stroke: '#555',
                                    lineWidth: 1
                                }
                            },
                            {
                                type: 'text',
                                left: '10',
                                top: '10',
                                style: {
                                    fill: '#333',
                                    text: self.remarkConfig.text,
                                    font: '14px Microsoft YaHei'
                                }
                            }
                        ]
                    }
                ];
            }

            self.chart.setOption(this.chartOption);
            self.chart.resize(self.chartWidth, self.chartHeight);
        };
        this.$onChanges = function(changedObject) {
            if (changedObject.options || changedObject.remarkConfig) {
                self.processChartData();
            }
        }
    }]
});