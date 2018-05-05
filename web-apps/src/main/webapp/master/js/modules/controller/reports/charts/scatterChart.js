/**
 * Created by zhanglu1782 on 2017/1/3.
 */
angular.module('scatterChart', []).component('sactterChart', {
    template : '<div e-chart e-data="$ctrl.option" style="width:800px;height: 800px;" id="{{$ctrl.chartId}}"></div>',
    bindings : {
        chartId : '@',
        chartData : '<',
        legends : '<',
        title : '@',
        subTitle : '@',
        height : '<'
    },
    controller : ['httpService', '$scope',function pieCharController(httpService, $scope) {
        var self = this;

        //this.height = "200px";
        this.chart = null;
        this.renderChart = function () {
            self.chartOption = {
                title : {
                    text: self.title,
                    subtext: self.subTitle
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                tooltip : {
                    trigger: 'item',
                    showDelay : 0,
                    axisPointer:{
                        show: true,
                        type : 'cross',
                        lineStyle: {
                            type : 'dashed',
                            width : 1
                        }
                    }
                },
                legend: {
                    data : self.legends,
                    left: 'right'
                },
                xAxis : [
                    {
                        type : 'value',
                        scale:true,
                        axisLabel : {
                            formatter: '{value}'
                        },
                        splitLine: {
                            lineStyle: {
                                type: 'dashed'
                            }
                        }
                    }
                ],
                yAxis : [
                    {
                        type : 'value',
                        scale:true,
                        axisLabel : {
                            formatter: '{value}'
                        },
                        splitLine: {
                            lineStyle: {
                                type: 'dashed'
                            }
                        }
                    }
                ],
                series : self.chartData
            };

            if (document.getElementById(self.chartId)) {
                if (!self.chart) {
                    self.chart = echarts.init(document.getElementById(self.chartId));
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