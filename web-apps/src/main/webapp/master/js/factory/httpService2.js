/**
 * @author doubeye
 * @version 2.0.1
 *  history
 *   2.0.1 :
 *    + 成功回调中加入点三个参数，总记录数，用以支持分页
 */
app.factory('httpService', ['$http', 'ngDialog', '$cookies', '$location', 'dialogService', function($http, ngDialog, $cookies, $location, dialogService){
    var processError = function (data, params) {
        if (!params) {
            params = {};
        }
        if (angular.isFunction(params.callback)) {
            params.callback(data);
        } else {
            var errorMessage = data.errorMessage ? data.errorMessage : '';
            if (data.status === -1) {
                errorMessage = data.config.url + "域名没有解析或后台接口报错"
            }
            ngDialog.open({
                template: '<p>' + (params.errorMessage ? params.errorMessage + ' ' : '') + errorMessage + '</p>',
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
         *  _noProcessingDialog <boolean> 不要显示处理对话框，默认为false
         *  resultPropertyName 在返回结果中，数据列表返回的属性，默认为DATA
         *  resultRecordCountPropertyName 在返回结果中，总记录数属性名，默认为TOTAL_RECORDS
         *  returnRawResponse 将整个返回结果作为结果返回给调用者，而不进行解析
         *  callback 返回正确时调用的回调函数包含参数
         *      data <JSONArray> 返回的数据
         *      customParameters <Object> 提交请求时的customParameters
         *      totalRecords <int> 结果的总页数，注意，此参数不是每个结果都会提供
         * @param errorParams 错误参数，包含以下内容：
         *  callback 发生错误的回调函数，如果没有提供此函数，则默认弹出错误提示
         *  errorMessage 没有提供错误函数时，弹出提示框中的通用信息，如果没有提供此函数，则仅显示错误信息
         * @param customParameters 自定义参数，将传递给successCallback 和 errorParam.callback
         * @param method HTTP method, 如果没有指定，则使用POST
         * @param dataPutInParams 提交的参数中，出现在此列表中，将拼装到url中
         */
        sendRequest: function (scope, url, data, successParams, errorParams, customParameters, method, dataPutInParams) {
            if (!successParams) {
                successParams = {};
            }
            if (successParams.resultPropertyName !== '' && !successParams.resultPropertyName) {
                successParams.resultPropertyName = 'DATA';
            }
            if (!successParams.resultRecordCountPropertyName) {
                successParams.resultRecordCountPropertyName = 'TOTAL_RECORDS';
            }
            if (!data) {
                data = {};
            }
            var httpMethod = method ? method : 'POST';
            data._userId = $cookies.get("_userId");
            data._expiredDate = $cookies.get("_expiredDate");
            data._token = $cookies.get("_token");
            data._eduUserId = $cookies.get("_eduUserId");
            data._eduUserName = $cookies.get("_eduUserName");
            var successCallback = successParams.callback;
            if (!data._noProcessingDialog) {
                var dialogId = dialogService.processing();
            }

        return $http({
            url: url,
            data: !dataPutInParams && (httpMethod ===  'POST') ? data : {},
            params: (dataPutInParams || httpMethod === 'GET') ? data : {},
            dataType:'json',
            method : httpMethod,
            headers: {'Content-Type': 'application/x-www-form-urlencoded'}
        }).then(function onSuccess(response) {
            if (!data._noProcessingDialog) {
                dialogService.closeProcessing(dialogId);
            }
            /** @namespace response.SUCCESS **/
            var success = response.data.SUCCESS ? response.data.SUCCESS : response.data.success;
            if (success) {
                //var result = response.data[successParams.resultPropertyName] ? response.data[successParams.resultPropertyName] : response.data.data && response.data.data[successParams.resultPropertyName] ? response.data.data[successParams.resultPropertyName] : response.data;
                var result = successParams.returnRawResponse ? response.data : com.doubeye.Utils.objectRefactor.getValue(response.data, successParams.resultPropertyName);
                var notLogin = response.data.NOT_LOGIN;
                // 加入点号的支持,可以对scope深层的属性赋值
                //if (result && result.NOT_LOGIN) {
                if (notLogin) {
                    $location.path('login');
                    return;
                }
                if (scope && successParams.dataPropertyName) {
                    if (successParams.dataPropertyName.indexOf(".") > 0) {
                        var pathNames = successParams.dataPropertyName.split('.');
                        var valueNode = scope;
                        for (var i = 0; i < pathNames.length - 1; i++) {
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
                //if (!result.NOT_LOGIN && angular.isFunction(successCallback)) {
                if (!notLogin && angular.isFunction(successCallback)) {
                    //var totalRecords = response.data[successParams.resultRecordCountPropertyName] ? response.data[successParams.resultRecordCountPropertyName] : (response.data.data ? response.data.data[successParams.resultRecordCountPropertyName] : 0);
                    var totalRecords = com.doubeye.Utils.objectRefactor.getValue(response.data, successParams.resultRecordCountPropertyName);
                    successCallback(result, customParameters, totalRecords);
                }
            } else {
                if (!data._noProcessingDialog) {
                    dialogService.closeProcessing(dialogId);
                }
                processError(response.data, errorParams, customParameters)
            }
        }).catch(function onError(response) {
            if (!data._noProcessingDialog) {
                dialogService.closeProcessing(dialogId);
            }
            console.log(response);
            processError(response, errorParams, customParameters)
        });
        }
    };
}]);