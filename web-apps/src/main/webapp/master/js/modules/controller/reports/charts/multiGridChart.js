/**
 * Created by zhanglu1782 on 2017/1/3.
 */
angular.module('lineChart', []).component('lineChart', {
    template : '<div e-chart e-data="$ctrl.option" style="height:{{$ctrl.chartHeight}}px;width:{{$ctrl.chartWidth}}" id="{{$ctrl.chartId}}"></div>',
    bindings : {
        options : '<',
        idPrefix : '@',
        chartId : '@',
        title : '@',
        chartHeight : '<',
        chartWidth : '<',
        onClick : '<',
        onMouseOver : '<',
        beforeRender : '<'
    },
    controller : ['httpService', '$scope',function lineCharController(httpService, $scope) {
        var self = this;
        this.chartId = "chart";

        this.options = {
            data : []
        };
        this.chart = null;

        this.processChartData = function () {
            if (self.options.data.length === 0) {
                return;
            }
            self.generateLegends();
            self.generateChartSeries();
            self.renderChart();
        };
        this.generateLegends = function () {
            self.legendData = [];
            self.selectedLegend = {};
            self.options.data.forEach(function (element) {
                self.legendData.push({
                    name : element.label,
                    icon : 'circle'
                });
                self.selectedLegend[element.label] = true;
            });
        };
        this.generateChartSeries = function () {
            self.chartSeries = [];
            self.options.data.forEach(function (element) {
                self.chartSeries.push({
                    name : element.label,
                    type:'line',
                    smooth:true,
                    symbolSize:14,
                    data : element.data,
                    label: {
                        normal: {
                            show: false,
                            position: 'top'//值显示
                        }
                    }
                });
            });
        };
        this.renderChart = function () {
            self.chartOption = {
                title : {
                    text: self.title,
                    textStyle : {
                        color : 'white'
                    },
                    x:'right'
                },
                dataZoom: [{
                    type: 'slider'
                }, {
                    type: 'inside'
                }],
                backgroundColor:'#091323',
                tooltip: {
                    trigger: 'axis'
                },
                legend: {
                    x: 'left',
                    padding: [10, 20,0,20],
                    data: self.legendData,
                    selected: self.selectedLegend,
                    textStyle:{
                        color:'#dededf'
                    }
                },
                grid: {
                    left: '0',
                    right: '3%',
                    bottom: '3%',
                    top:'13%',
                    containLabel: true
                },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    axisTick:{show:false},
                    axisLabel:{
                        textStyle:{
                            color:'#dededf'
                        }
                    },
                    splitLine:{//网格线
                        show: true,
                        lineStyle:{
                            color:['#23303f'],
                            type:'solid'
                        }
                    },
                    //这个需要修改
                    data : self.options.axis
                },
                yAxis: {
                    min:0,
                    axisTick:{show:false},
                    axisLine:{
                        show:false
                    },
                    axisLabel:{
                        textStyle:{
                            color:'#dededf'
                        }
                    },
                    splitLine:{//网格线
                        show: true,
                        lineStyle:{
                            color:['#23303f'],
                            type:'solid'
                        }
                    }
                },
                series: self.chartSeries
            };
            if (!self.chart) {
                self.chart = echarts.init(document.getElementById(self.chartId));
                if (angular.isFunction(self.onClick)) {
                    self.chart.on('click', function (params) {
                        self.onClick(params);
                    });
                }
            }
            if (angular.isFunction(self.beforeRender)) {
                self.beforeRender(self.chartOption);
            }
            self.chart.setOption(this.chartOption);
        };
        this.$onChanges = function(changedObject) {
            if (changedObject.options) {
                self.processChartData();
            }
        }
    }]
});