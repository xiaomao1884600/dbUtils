/**
 * Created by zhanglu1782 on 2016/11/7.
 * 可添加选项的ComboBox
 */
angular.module('dManageableComboBox', []).component('dManageableComboBox', {
    templateUrl : 'master/js/components/dManageableComboBox/dManageableComboBoxView.html',
    scope : {
        comboBoxUrl : '@',
        // ADDED
        value : '='
    },
    bindings : {
        url: '<',
        valuePropertyName : '<',
        labelPropertyName : '<',
        /**
         * 组件的值属性
         */
        componentValue : '='
    },
    require: '^ngModel', //做数据绑定用
    controller : ['$scope', 'httpService', '$element', '$attrs', function dManageableComboBoxController($scope, httpService, $element, $attrs) {
        var self = this;
        this.$onInit = function() {
            self.labelPropertyName = $attrs.labelPropertyName ? $attrs.labelPropertyName : "id";
            self.valuePropertyName = $attrs.valuePropertyName ? $attrs.valuePropertyName : "value";
        };

        if (self.url) {
            httpService.sendRequest(self, self.url, {

            }, {
                callback : function (data) {
                    self.processResult(data)
                }
            }, {
                errorMessage : '获取内容出错，'
            });
        }

        self.processResult = function(data) {
            self.items = [];
            data.forEach(function (element) {
                self.items.push({
                    id : element[self.valuePropertyName],
                    name : element[self.labelPropertyName]
                })
            });
        };


        self.componentValue = "it is now finally from inside";
    }]
});