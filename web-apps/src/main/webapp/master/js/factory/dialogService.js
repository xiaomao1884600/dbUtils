/**
 * @author doubeye
 * @version 1.0.0
 对话框服务
 */
app.factory('dialogService', ['$mdDialog', '$mdToast', function($mdDialog, $mdToast){
    var _dialogId = 0;
    var _isProcessing = false;
    return {
        /**
         *
         * @param title <String> 消息的标题
         * @param message <String> 消息内容
         * @param dialogConfig <Object> 对话框配置，支持以下参数
         *  width 宽度
         *  height 高度
         *  parentSelector 父组件的selector，如果为指定，则使用document
         */
        alert: function (title, message, dialogConfig) {
            if (!dialogConfig) {
                dialogConfig = {};
            }
            $mdDialog.show(
                $mdDialog.alert()
                    .parent(dialogConfig.parentSelector ? angular.element(document.querySelector(dialogConfig.parentSelector)) : angular.element(document.body))
                    .clickOutsideToClose(true)
                    .title(title)
                    .textContent(message)
                    .ariaLabel(title)
                    .ok('确定')
            );
        },
        toast : function (text) {
            var config = $mdToast.simple().textContent(text);
            config._options.hideDelay = 1000;
            config._options.position = 'top right';
            $mdToast.show(config);

        },
        confirm : function (title, message, dialogConfig, confirmCallback, abortCallback) {
            if (!dialogConfig) {
                dialogConfig = {};
            }
            var confirm = $mdDialog.confirm().parent(dialogConfig.parentSelector ? angular.element(document.querySelector(dialogConfig.parentSelector)) : angular.element(document.body))
                .title(title)
                .textContent(message)
                .ok('确定')
                .cancel('取消');

            $mdDialog.show(confirm).then(function(result) {
                if (angular.isFunction(confirmCallback)) {
                    confirmCallback(result);
                }
            }, function(result) {
                if (angular.isFunction(abortCallback)) {
                    abortCallback(result);
                }
            });
        },
        showContent : function (title, message, content, dialogConfig, confirmCallback, abortCallback) {
            if (!dialogConfig) {
                dialogConfig = {};
            }
            $mdDialog.show({
                controller: DialogController,
                templateUrl: 'master/factory/template/alert.html',
                parent: angular.element(document.body),
                clickOutsideToClose:true
            });
            $scope.title = title;
            $scope.message = message;
            $scope.content = content;
            function DialogController($scope, $mdDialog) {
                $scope.hide = function() {
                    $mdDialog.hide();
                };

                $scope.cancel = function() {
                    $mdDialog.cancel();
                };

                $scope.answer = function(answer) {
                    $mdDialog.hide(answer);
                };
            }
        },
        processing : function (parent) {
            if (_isProcessing) {
                return '';
            }
            _isProcessing = true;
            _dialogId ++;
            var id = "processing_" + _dialogId;
            var template =
                '    <div class="md-dialog-container" id="' + id + '">\n' +
                '        <md-dialog aria-label="processing" style="min-width: 60px;height: 60px;overflow: hidden;text-align: -webkit-center;padding-top: 5px;">\n' +
                '              <md-progress-circular md-mode="indeterminate"></md-progress-circular>\n' +
                '        </md-dialog>\n' +
                '    </div>\n';
            var processingConfig = {
                template: template,
                multiple: true,
                parent: parent ? angular.element(document.querySelector(dialogConfig.parentSelector)) : angular.element(document.body),
                clickOutsideToClose : false,
                escapeToClose : false,
                autoWrap : true,
                toastClass : 'doubeyeProcessingDialog',
                onRemoving : function () {
                    _isProcessing = false;
                }
            };
            var dialog = $mdDialog.show(processingConfig);
            return id;
        },

        closeProcessing : function (id) {
            if (id && _isProcessing) {
                $mdDialog.hide({
                    contentElement: '#' + id
                });
            }
        }

    };
}]);