/**
 * Created by doubeye
 * 对EChart词云的封装
 */
angular.module('wordCloud', []).component('wordCloud', {
    template : '<div e-chart e-data="$ctrl.option" style="height:{{$ctrl.chartHeight}}px;width:{{$ctrl.chartWidth}}px;" id="{{$ctrl.chartId}}"></div>',
    bindings : {
        chartId : '@',
        /**
         * 词的数组，需要包含两个属性：
         * name {String} 词
         * value {int} 权重，将影响文字的大小
         */
        words : '<',
        title : '<',
        subTitle : '@',
        chartWidth : '<',
        chartHeight : '<',
        onClick : '<'
    },
    controller : ['httpService', '$scope',function wordCloudController(httpService, $scope) {
        var self = this;
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
                series: [{
                    type: 'wordCloud',
                    gridSize: 8,
                    drawOutOfBound: true,
                    sizeRange: [12, 50],
                    rotationRange: [0, 0],
                    shape: 'circle',
                    textStyle: {
                        normal: {
                            color: function() {
                                return 'rgb(' + [
                                    Math.round(Math.random() * 160),
                                    Math.round(Math.random() * 160),
                                    Math.round(Math.random() * 160)
                                ].join(',') + ')';
                            }
                        },
                        emphasis: {
                            shadowBlur: 10,
                            shadowColor: '#333'
                        }
                    },
                    data: self.words
                }]
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
            if (changedObject.words && self.words) {
                self.renderChart();
            }
        }
    }]
});