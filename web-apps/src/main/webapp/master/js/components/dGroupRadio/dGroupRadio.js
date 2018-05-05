/**
 * Created by zhanglu1782 on 2016/11/7.
 */
angular.module('dGroupRadio', []).component('dGroupRadio', {
    templateUrl: 'master/js/components/dGroupRadio/dGroupRadio.html',
    bindings: {
        resultValuePropertyName: '<',
        url: '<',
        params: '<',
        textPropertyName: '<',
        valuePropertyName: '<',
        resultValuePropertyName: '<',
        cookieName : '<',
        afterChange : '<'
    },
    scope: {
        /**
         * 需要从父指令中获得的属性，包括
         * url 获得数据的url
         * params 获得数据的参数
         * textPropertyName 返回数据中的radio的描述名称属性
         * valuePropertyName 返回数据中的值名称属性
         * resultValuePropertyName 返回给父指令的值
         * componentName 组件名，用来唯一标识radioGroup
         * 用来设置和读取cookie中的值，如果此值为空，则表示不使用cookie
         * afterChange当值发生改变时触发的时间
         */
        config: '@',
        /**
         * 父指令中的值属性， 组件可以将自己的值传递给父组件
         */
        value: '@'
    },
    controller: ['httpService', '$scope', '$cookies', '$attrs', function dManageableComboBoxController(httpService, $scope, $cookies, $attrs) {
        var self = this;

        var config = $scope.$parent.config;
        if (!config) {
            config = {};
        }
        //用来暂存此组件的值
        self._value = null;
        //self.$onInit = function() {
            //从属性中获得cookie的名称，如果有，则将cookie值恢复到组件上，使得组件有默认值
            config.cookieName = $attrs.cookieName;
            if (config.cookieName) {
                self._value = $cookies.get(config.cookieName);
                if (config.resultValuePropertyName) {
                    $scope.$parent.value[config.resultValuePropertyName] = self._value;
                } else {
                    $scope.$parent.value = self._value;
                }
                if (angular.isFunction(config.afterChange)) {
                    config.afterChange(self._value);
                }
            }
        //};
        //根据指令html中的各种绑定的信息覆盖config中的对应内容。
        var url = config.url;
        var params = config.params;
        self.componentName = config.componentName;
        httpService.sendRequest(self, url, params, {
            callback: function (data) {
                self.items = [];
                var obj;
                data.forEach(function (item) {
                    obj = {
                        name: item[config.textPropertyName],
                        value: item[config.valuePropertyName]
                    };
                    self.items.push(obj);
                });
            }
        });
        self.setCurrentRadio = function (item) {
            //$scope.$parent.value[config.resultValuePropertyName] = item.value;
            if (config.resultValuePropertyName) {
                $scope.$parent.value[config.resultValuePropertyName] = item.value;
            } else {
                $scope.$parent.value = item.value;
            }
            self._value = item.value;
            if (angular.isFunction(config.afterChange)) {
                config.afterChange(self._value);
            }
            if (config.cookieName) {
                $cookies.put(config.cookieName, item.value);
            }
        };
    }]
});