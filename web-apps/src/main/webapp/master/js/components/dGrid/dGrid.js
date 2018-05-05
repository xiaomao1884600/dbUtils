/**
 * @author doubeye
 */

angular.module('dGrid', []).component('dGrid', {
    template :

    '<style>' +
    'table {\n' +
    '    box-sizing: border-box;\n' +
    '    -moz-box-sizing: border-box;\n' +
    '    display: flex;\n' +
    '    flex-direction: column;\n' +
    '    align-items: stretch;\n' +
    '}\n' +
    '\n' +
    'table * {\n' +
    '    box-sizing: inherit;\n' +
    '    -moz-box-sizing: inherit;\n' +
    '}\n' +
    '\n' +
    'thead {\n' +
    '    display: flex;\n' +
    '    flex-direction: column;\n' +
    '    align-items: stretch;\n' +
    '}\n' +
    '\n' +
    'tbody {\n' +
    '    overflow-y: scroll;\n' +
    '    display: inline-block;\n' +
    '}\n' +
    '\n' +
    'thead > tr, tbody > tr, tfoot > tr {\n' +
    '    display: flex;\n' +
    '    flex-direction: row;\n' +
    '    flex-wrap: nowrap;\n' +
    '}\n' +
    '\n' +
    'thead, tfoot {\n' +
    '    flex-shrink: 0;\n' +
    '}\n' +
    '\n' +
    'th, tbody td {\n' +
    '    width: 20%; /* this can vary */\n' +
    '    overflow-x: hidden;\n' +
    '    text-overflow: ellipsis;\n' +
    '    display: inline-block;\n' +
    '}\n' +
    '\n' +
    'tfoot {\n' +
    '    display: inline-block;\n' +
    '}\n' +
    '\n' +
    'tfoot td {\n' +
    '    width: 100%;\n' +
    '    display: inline-block;\n' +
    '}' +
    '</style>' +

    '<table st-table="$ctrl.displayData" st-safe-src="$ctrl.gridData" class="table table-striped" style="height:{{$ctrl.tableHeight}}">\n' +
    '    <thead>\n' +
    '    <tr>\n' +
    '        <th ng-repeat="column in $ctrl.columnDefines" st-sort="{{column.sortDataId}}" ng-hide="column.hidden === true">{{column.label}}</th>\n'+
    '        <th ng-if="$ctrl.operations">操作</th>\n'+
    '    </tr>\n' +
    '    </thead>\n' +
    '    <tbody>' +
    '    <tr ng-repeat="row in $ctrl.displayData" st-select-row="row" st-select-mode="{{$ctrl.selectModel}}">' +
            '<td ng-repeat="column in $ctrl.columnDefines" style="color:{{row[column.color]}};background:{{row[column.backgroundDataId]}}" ng-hide="{{column.hidden === true}}">' +
                '<p ng-if="!column.html">{{$ctrl.render(column, row)}}</p>' +
                '<p ng-if="column.html"><to-html column-define="column" row-data="row" column-html="column.html"></to-html>' +
            '</td>' +
    '       <td ng-if="$ctrl.operations">' +
    '           <button ng-repeat="operation in $ctrl.operations" ng-click="$ctrl.processOperation(operation, row)" ng-hide="(operation.id && row._hideOperation) ? row._hideOperation[operation.id] : false">{{$ctrl.renderButtonLabel(operation, row)}}</button>' +
    '       </td>' +
    '    </tr>\n' +
    '    </tbody>\n' +
    '</table>',
    bindings : {
        /**
         * 列定义，包含如下属性
         * dataId <String> 显示数据在record中的属性名 必填
         * label <String> 列的标题，必填
         * html <String | template> 列值的模板，如果存在，则不使用默认的列值，而是将列值注入到模板中，选填
         * value <function> 值转换函数
         * color <Color> 文字的颜色
         * sortDataId <String> 排序字段，如果没有指定，则使用dataId,
         * hidden <boolean> 是否隐藏此数据
         * backgroundDataId <String> 背景颜色的数据id
         */
        columnDefines: '<',
        gridData : '<',
        /**
         * 操作定义，包含属性如下
         * text <String> 按钮文字
         * id <String> 按钮的id
         * callback <function> 点击按钮的回调函数，会把row对象和按钮配置对象作为参数返回
         * hide <boolean> 是否要隐藏按钮，需要在表格数据的每一行来设置是否隐藏某个按钮，例如要隐藏id为log的按钮，需要将要隐藏的行中添加以下对象：row._hideOperation{ id : true}
         */
        operations : '<',
        /**
         * 是否支持行选，如果需要支持，则传入'multiple'或'single'，默认为single
         */
        selectModel : '<',
        /**
         * 表格的高度
         */
        tableHeight : '<',
        /**
         * 无需排序，默认为false
         * todo
         */
        noSort : '<'
    },
    controller : ['$scope', '$compile', '$attrs','$sce', function dGrid($scope, $compile, $attrs,$sce) {
        var self = this;
        this.columnDefines.forEach(function (columnDefine) {
            if (!columnDefine.sortDataId) {
                columnDefine.sortDataId = columnDefine.dataId;
            }
        });

        this.$onInit = function () {
            if (!this.selectModel) {
                this.selectModel = 'single'
            }
        };
        this.processOperation = function (operation, row) {
            operation.callback(row);
        };
        this.render = function (column, row) {
            var value = com.doubeye.Utils.objectRefactor.getValue(row, column.dataId, '.');
            if (angular.isFunction(column.value)){
                return column.value(value, row);
            } else {
                return value;
            }
        };
        this.renderButtonLabel = function(buttonConfig, row) {
            if (angular.isFunction(buttonConfig.render)) {
                return buttonConfig.render(row, buttonConfig)
            } else {
                return buttonConfig.text;
            }
        }
    }]
}).filter('toHtml', function ($sce) {
    return function (text, a, b, c, d) {
        console.log(text);
        return $sce.trustAsHtml(text);
    };
}).directive('toHtml', function($sce){
    return {
        restrict:'E',
        replace:true,
        transclude : true,
        scope:{
            columnDefine : '=columnDefine',
            rowData : '=rowData',
            html : "=columnHtml"
        },
        template : '<p ng-bind-html="columnDefine.html"></p>',
        link : function ($scope, element) {
            setTimeout(function () {
                if (element[0].children.length > 0) {
                    var tagName = element[0].children[0].tagName;
                    if (tagName === 'LABEL' || tagName === 'SPAN') {
                        var val = com.doubeye.Utils.objectRefactor.getValue($scope.rowData, $scope.columnDefine.dataId);
                        element[0].children[0].innerHTML = (val || val === 0) ? val : '';
                    } else if (tagName === 'BUTTON') {
                        element[0].children[0].innerHTML = $scope.columnDefine.html;
                    }else {
                        element[0].children[0].href = $scope.rowData[$scope.columnDefine.dataId];
                        element[0].children[0].innerHTML = $scope.rowData[$scope.columnDefine.dataId];
                        element[0].children[0].value = $scope.rowData[$scope.columnDefine.dataId];
                    }
                } else {
                    if ($scope.columnDefine.html.indexOf('button') > 0) {
                        element[0].innerHTML = $scope.columnDefine.html;
                    }

                }
            }, 100);
        }
    }
});