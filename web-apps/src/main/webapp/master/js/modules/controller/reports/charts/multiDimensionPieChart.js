/**
 * Created by doubeye
 * 对EChart饼图的封装
 */
angular.module('multiDimensionPieChart', []).component('multiDimensionPieChart', {
    template : '<div e-chart style="height:{{$ctrl.chartHeight}}px;width:{{$ctrl.chartWidth}}px;" id="{{$ctrl.chartId}}"></div>',
    bindings : {
        chartId : '@',
        options : '<',
        title : '<',
        subTitle : '@',
        chartWidth : '<',
        chartHeight : '<',
        onClick : '<'
    },
    controller : ['httpService', '$scope',function multiDimensionPieChartController(httpService, $scope) {
        var self = this;
        this.height = "200px";
        this.chart = null;
        if (!this.chartHeight) {
            this.chartHeight = 400;
        }
        if (!this.chartWidth) {
            this.chartWidth = 400;
        }
        this.renderChart = function () {
            if (self.options) {
                self.chartOption = {
                    title: {
                        text: self.title,
                        subtext: self.subTitle,
                        x: 'center'
                    },

                    tooltip: {
                        trigger: 'item',
                        formatter: "{a} <br/>{b}: {c} ({d}%)"
                    },

                    legend: {
                        orient: 'vertical',
                        x: 'left',
                        data: self.options.legend
                    },
                    series: self.options.series
                };
                if (document.getElementById(self.chartId)) {
                    if (!self.chart) {
                        self.chart = echarts.init(document.getElementById(self.chartId));
                        if (angular.isFunction(self.onClick)) {
                            self.chart.on('click', function (params) {
                                self.onClick(params);
                            });
                        }
                    }
                    self.chart.setOption(self.chartOption);
                }
            }
        };
        this.$onChanges = function(changedObject) {
            if (changedObject.options) {
                self.renderChart();
            }
        }
    }]
});