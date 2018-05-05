/**
 * Created by doubeye
 * 对EChart饼图的封装
 */
angular.module('pieChart', []).component('pieChart', {
    template : '<div e-chart e-data="$ctrl.option" style="height:{{$ctrl.chartHeight}}px;width:{{$ctrl.chartWidth}}px;" id="{{$ctrl.chartId}}"></div>',
    bindings : {
        chartId : '@',
        chartData : '<',
        legends : '<',
        title : '<',
        subTitle : '@',
        chartWidth : '<',
        chartHeight : '<',
        onClick : '<'
    },
    controller : ['httpService', '$scope',function pieChartController(httpService, $scope) {
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
            self.chartOption = {
                title : {
                    text: self.title,
                    subtext: self.subTitle,
                    x:'center'
                },
                tooltip : {
                    trigger: 'item',
                    formatter: "{a} <br/>{b} : {c} ({d}%)"
                },
                legend: {
                    orient: 'vertical',
                    left: 'left',
                    data: self.legends ? self.legends :[]
                },
                series : [
                    {
                        type: 'pie',
                        radius : '55%',
                        center: ['50%', '50%'],
                        data: self.chartData ? self.chartData : [],
                        itemStyle: {
                            emphasis: {
                                shadowBlur: 10,
                                shadowOffsetX: 0,
                                shadowColor: 'rgba(0, 0, 0, 0.5)'
                            }
                        }
                    }
                ]
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
        };
        this.$onChanges = function(changedObject) {
            if (changedObject.chartData || changedObject.legends) {
                self.renderChart();
            }
        }
    }]
});