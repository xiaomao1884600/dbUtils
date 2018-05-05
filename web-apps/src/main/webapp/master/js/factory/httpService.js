/**
 * Created by zhanglu1782 on 2016/11/8.
 */
angular.module('dbUtilsApp').factory('httpService', ['$http', 'ngDialog', '$cookies', '$location', function($http, ngDialog, $cookies, $location){
    var processError = function (data, params) {
        if (!params) {
            params = {};
        }
        if (com.doubeye.Ext.isFunction(params.callback)) {
            params.callback(data);
        } else {
            ngDialog.open({
                template: '<p>' + (params.errorMessage ? params.errorMessage + ' ' : '') + data.errorMessage + '</p>',
                plain: true
            });
        }
    };
    return {
        /**
         *
         * @param scope 作用域
         * @param url url
         * @param data 发送的数据
         * @param successParams 正常返回时的参数，包含以下内容
         *  dataPropertyName 在scope中添加的数据属性名，如果设置此参数，则将返回的内容赋值给指定作用域的指定属性
         *  callback 返回正确时调用的回调函数
         * @param errorParams 错误参数，包含以下内容：
         *  callback 发生错误的回调函数，如果没有提供此函数，则默认弹出错误提示
         *  errorMessage 没有提供错误函数时，弹出提示框中的通用信息，如果没有提供此函数，则仅显示错误信息
         * @param customParameters 自定义参数，将传递给successCallback 和 errorParam.callback
         *
         */
        sendRequest: function (scope, url, data, successParams, errorParams, customParameters) {
            if (!successParams) {
                successParams = {};
            }
            if (!data) {
                data = {};
            }
            data._userId = $cookies.get("_userId");
            data._expiredDate = $cookies.get("_expiredDate");
            data._token = $cookies.get("_token");
            var successCallback = successParams.callback;
            var promise = $http({
                url: url,
                data : data,
                method: 'POST',
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            }).success(function (response) {
                /** @namespace response.SUCCESS **/
                if (response.SUCCESS) {
                    var result = response.DATA ? response.DATA : response;
                    // 加入点号的支持,可以对scope深层的属性赋值
                    if (result.NOT_LOGIN) {
                        $location.path('/login');
                        return;
                    }
                    if (scope && successParams.dataPropertyName) {
                        if (successParams.dataPropertyName.indexOf(".") > 0) {
                            var pathNames = successParams.dataPropertyName.split('.');
                            var valueNode = scope;
                            for (var i = 0; i < pathNames.length - 1; i ++) {
                                valueNode = valueNode[pathNames[i]];
                                if (!valueNode) {
                                    valueNode = {};
                                }
                            }
                            valueNode[pathNames[pathNames.length - 1]] = result;
                        } else {
                            scope[successParams.dataPropertyName] = result;
                        }
                    }
                    if (!result.NOT_LOGIN && com.doubeye.Ext.isFunction(successCallback)) {
                        successCallback(result, customParameters);
                    }
                } else {
                    processError(response, errorParams, customParameters)
                }
            }).error(function (response) {
                processError(response, errorParams, customParameters)
            });
            return promise;
        }
    };
}]);