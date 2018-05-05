/**
 * Created by doubeye on 2016/11/7.
 * ComboBox
 */
angular.module('dComboBox', []).component('dComboBox', {
    templateUrl : 'master/js/components/dComboBox/dComboBox.html',
    bindings : {
        url: '<',
        valuePropertyName : '<',
        labelPropertyName : '<',
        /**
         * 组件的值属性
         */
        value : '=',
        options : '<',
        onChange : '&'
    },
    transclude : true,
    require: '^ngModel', //做数据绑定用
    controller : ['$scope', 'httpService', '$element', '$attrs', function dManageableComboBoxController($scope, httpService, $element, $attrs) {
        var self = this;

        this.$onInit = function () {

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
            if (com.doubeye.Ext.isJSONArray(data)) {
                self.items = [];
                data.forEach(function (element) {
                    var item = {
                        value : element[self.valuePropertyName],
                        label: element[self.labelPropertyName]
                    };
                    self.items.push(item);
                });
            }
        };

        self.$onInit = function() {
            self.labelPropertyName = self.labelPropertyName ? self.labelPropertyName : "label" ;
            self.valuePropertyName = self.valuePropertyName ? self.valuePropertyName : "value";
        };

        this.$onChanges = function(changesObj) {
            this.processResult(changesObj.options.currentValue);
        }
    }]
});