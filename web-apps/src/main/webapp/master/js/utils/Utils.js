/**
 * 工具方法集合
 */
Date.prototype.format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
};

com.doubeye.Utils = {
    WEB_APP_CONTEXT_PATH: '/',
    /**
     * 所有与应用程序相关的变量都放到这里里面。
     * TODO 总觉得这么做不符合Angular的思想，暂时没有找到好的解决方案
     */
    application: {
        generalRouter: 'generalRouter',
        /**
         * 所有被授权的menu
         */
        menus: [],
        /**
         * 消除树形结构的所有授权的菜单
         */
        flatMenus: [],
        /**
         * menu操作的scope
         */
        menuScope: {},
        /**
         * 承载Menu的组件
         */
        menuComponent: {},
        cookies: null,
        location: null,
        /**
         * 无需鉴权的states
         */
        publicStates: ['login', 'welcome', 'forbidden'],
        registerMenu: function (menuComponent, scope, cookies, location) {
            com.doubeye.Utils.application.menuComponent = menuComponent;
            com.doubeye.Utils.application.menuScope = scope;
            com.doubeye.Utils.application.cookies = cookies;
            com.doubeye.Utils.application.location = location;
        },
        renderMenu: function (menuItems, jump, authorizationService) {
            com.doubeye.Utils.array.fromTree(menuItems, 'pages', com.doubeye.Utils.application.flatMenus);
            com.doubeye.Utils.application.menus = menuItems;
            com.doubeye.Utils.application.menuComponent.sections = menuItems;
            com.doubeye.Utils.application.menuScope.menu = com.doubeye.Utils.application.menuComponent;
            var cookiePropertyName = com.doubeye.Utils.application.cookies.get("_userId") + "_lastState";
            if (jump) {
                if (com.doubeye.Utils.application.cookies.get(cookiePropertyName)) {
                    com.doubeye.Utils.application.location.path(com.doubeye.Utils.application.cookies.get(cookiePropertyName));
                } else {
                    com.doubeye.Utils.application.location.path("/welcome");
                }
            }
            com.doubeye.Utils.application.menuScope.user = authorizationService.getUser();
        }
    },
    audioPlayer: {
        audioSrcError : function(event){
            console.log('error');
            var audio = event.target;
            if (audio.src.indexOf('http://local/') >= 0) {
                var fileName = audio.src.replace('http://local/', '');
                var arg = JSON.stringify({FileName : fileName});
                var request = new XMLHttpRequest();
                request.onreadystatechange = function () {
                    if (request.readyState === 4) {
                        if(request.status === 200) {
                            audio.src = JSON.parse(request.responseText).data.SignatureUrl;
                            audio.play();
                        }
                    }
                };
                request.open("POST","http://record.hxsd.local/api/record/yun_download",true);
                request.setRequestHeader("Content-Type","application/x-www-form-urlencoded;");
                request.send(arg);
            }
        },
        onPlay: function (event) {
            var audio = event.target;
            var audios = document.getElementsByTagName("audio");
            for (var i = 0; i < audios.length; i++) {
                if (audio !== audios[i]) {
                    audios[i].pause();
                }
            }

        }
    },
    array: {
        /**
         * 根据数组调整数组下标
         */
        fixArrayIndex: function (array, index) {
            // 如果array不是数组，或者index小于0，或者index不是数字，返回0
            if (!com.doubeye.Ext.isArray(array) || index < 0 || isNaN(index)) {
                return 0;
            } else if (index > array.length - 1) {//如果index超出数组下标，返回数组最后一个元素的下标
                return array.length - 1;
            } else {
                return index;
            }
        },
        /**
         * 从对象数组中获得指定的元素
         * @param conditionObj {Object} 要找到对象
         * @param array {Array} 数组对象
         * @param startIndex {int} 数组中开始查找的位置
         * @param valuePropertyName {String} 使用数组元素中的那个属性进行比较，如果该参数为空，则直接比较数组的元素
         * @return {Object} 第一个符合条件的对象，如果从开始位置起无符合条件的对象，则返回空
         */
        getObjectFromArray: function (conditionObj, array, startIndex, valuePropertyName) {
            if (!com.doubeye.Ext.isArray(array)) {
                return null;
            }
            startIndex = com.doubeye.Utils.array.fixArrayIndex(array, startIndex);
            var fields = com.doubeye.Utils.objectRefactor.getFields(conditionObj), field, obj, finding = true;
            if (fields.length === 0) {
                return null;
            }
            for (var i = startIndex; i < array.length; i++) {
                if (valuePropertyName) {
                    obj = array[i][valuePropertyName];
                } else {
                    obj = array[i];
                }
                finding = true;
                for (var j = 0; j < fields.length; j++) {
                    field = fields[j];
                    if (obj[field] != conditionObj[field]) {
                        finding = false;
                        break;
                    }
                }
                if (finding) {
                    return array[i];
                }
            }
            return null;
        },
        fromTree: function (tree, childrenProperty, result) {
            tree.forEach(function (node) {
                result.push(node);
                if (angular.isArray(node[childrenProperty])) {
                    com.doubeye.Utils.array.fromTree(node[childrenProperty], childrenProperty, result);
                }
            });
        },
        /**
         * 将数组元素中的某个属性用逗号分隔开，组成祝福词
         * @param array 数组
         * @param propertyName 属性名，如果不指定此属性，则将整个元素作为值进行拼接，适用于非对象数组
         * @returns {string} 结果
         */
        toString: function (array, propertyName) {
            var result = '';
            if (angular.isArray(array)) {
                array.forEach(function (entry) {
                    if (propertyName) {
                        if (entry[propertyName]) {
                            result += (',' + entry[propertyName]);
                        }
                    } else {
                        result += (',' + entry);
                    }
                });
            }
            return result.substr(1, result.length);
        }
    },
    /**
     * 集合操作类
     */
    collection: {
        /**
         * 将jsonObject 转化为树形组件所使用的数组格式（abnTree）
         * @param value <jsonObject> json对象
         * @param result <array> abnTree的结果对象
         * @returns {Array} 结果
         */
        toAbnTreeJSON: function (value, result) {
            if (com.doubeye.Ext.isJSONObject(value)) {
                for (var key in value) {
                    var node = {label: key};
                    if (com.doubeye.Ext.isJSON(value[key])) {
                        node.children = [];
                        com.doubeye.Utils.collection.toAbnTreeJSON(value[key], node.children);
                        result.push(node);
                    } else {
                        node.label += ' -- ' + value[key];
                        result.push(node);
                    }
                }
            } else if (com.doubeye.Ext.isJSONArray(value)) {
                value.forEach(function (item) {
                    var node = {label: '[]'};
                    if (com.doubeye.Ext.isJSON(item)) {
                        node.children = [];
                        com.doubeye.Utils.collection.toAbnTreeJSON(item, node.children);
                    }
                    result.push(node);
                });
            } else {
                var node = {label: value};
                result.push(node);
            }
        },
        /**
         * 将jsonTree对象与比较结果进行绑定
         * @param jsonTree json的abn tree对象
         * @param compareResult 比较结果
         * @param isFirst 是否为first，如果值为true，则认为是第一个对象，否则认为是第二个对象，默认为true
         * @param path 路径
         */
        abnTreeJSONCompareResultWrapper: function (jsonTree, compareResult, isFirst, path) {
            if (!path) {
                path = "";
            }
            compareResult.forEach(function (item) {
                com.doubeye.Utils.collection.__abnTreeJSONCompareResultDifferWrapper(jsonTree, item, isFirst, path);
            });
        },
        /**
         * 目前没有却别仅存在于第一个对象和第二个对象的颜色
         * @param jsonTree
         * @param compareResult
         * @param isFirst
         * @param path
         * @private
         */
        __abnTreeJSONCompareResultDifferWrapper: function (jsonTree, compareResult, isFirst, path) {
            var fields = com.doubeye.Utils.objectRefactor.getFields(compareResult);
            fields.forEach(function (item) {
                if (compareResult[item]._notEquals) {
                    //不等，用红色标出
                    var node = com.doubeye.Utils.collection.__findNotInTree(jsonTree, path + "/" + item);
                    node.color = 'red';
                } else if (compareResult[item]._onlyExistsInTheFirst || compareResult[item]._onlyExistsInTheSecond) {
                    //存在于第一个
                    var node = com.doubeye.Utils.collection.__findNotInTree(jsonTree, path + "/" + item);
                    node.color = '#e89a46';
                } else {
                    com.doubeye.Utils.collection.__abnTreeJSONCompareResultDifferWrapper(jsonTree, compareResult[item], isFirst, path + "/" + item);
                }
            });
        },
        __findNotInTree: function (abnTreeNode, path) {
            var pathArray = path.split("/");
            var result = abnTreeNode;
            var label;
            pathArray.forEach(function (item) {
                if (item) {
                    result.forEach(function (node) {
                        label = (node.label.indexOf(' --') > 0) ? (node.label.substr(0, node.label.indexOf(' --'))) : node.label;
                        if (label === item) {
                            if (node.children && node.children.length > 0) {
                                result = node.children;
                            } else {
                                result = node;
                            }
                            //return;
                        }
                    });
                }
            });
            return result;
        },
        /**
         * 通过path设置对象的值
         * @warning not fully tested
         * @param object 被设置值的对象
         * @param path json path，包含
         * @param property 属性名称
         * @param value
         */
        setValue: function (object, path, property, value) {
            var pathArray = path.split("/");
            var node = object;
            pathArray.forEach(function (item) {
                if (item) {
                    if (!node.item) {
                        node.item = {};
                    }
                    node = node.item;
                }
            });
            node[property] = value;
        }
    },
    objectRefactor: {
        /**
         * 获得对象的所有属性
         * @param obj 要获得属性的对象
         */
        getFields: function (obj) {
            var result = [];
            for (var p in obj) {
                try {
                    if (typeof (obj[p]) != "function") {
                        result.push(p);
                    }
                } catch (e) {

                }
            }
            return result;
        },
        /**
         * 获得对象的所有函数
         * @param obj 要获得函数的对象
         */
        getFunctions: function (obj) {
            var result = [];
            for (var p in obj) {
                try {
                    if (typeof (obj[p]) == "function") {
                        result.push(p);
                    }
                } catch (e) {

                }
            }
            return result;
        },
        /**
         * 将对象的所有指定属性进行加和
         * @param object{Object} 被加和的对象
         * @param interestedFields{Array<String>} 要加和的属性名，如果为空，则所有字段都需要加和
         * @return 加和的结果
         */
        sumFields: function (object, interestedFields) {
            if (!object) {
                object = {};
            }
            var fields = angular.isArray(interestedFields) ? interestedFields : com.doubeye.Utils.objectRefactor.getFields(object);
            var result = 0;
            fields.forEach(function (field) {
                result += isNaN(object[field]) ? 0 : object[field];
            });
            return result;
        },
        /**
         * 用defaultObject（默认值对象）补充source（源对象）
         * @param source 要被设置默认值的对象
         * @param defaultObject 默认值对象
         */
        merge: function (source, defaultObject) {
            var fields = com.doubeye.Utils.objectRefactor.getFields(defaultObject);
            fields.forEach(function (field) {
                if (!source[field]) {
                    source[field] = defaultObject[field];
                }
            });
        },
        /**
         * 将key-value对象转换为JSONArray，并用idPorpertyName作为key的属性名
         * @param object 要转换的JSONObject
         * @param idPropertyName id的属性值，如果不指定此值，则不将id加入到对象中，适用于id已经以某个属性存在
         */
        objectToArray: function (object, idPropertyName) {
            var fields = com.doubeye.Utils.objectRefactor.getFields(object);
            var result = [];
            fields.forEach(function (fieldName) {
                var obj = object[fieldName];
                if (idPropertyName) {
                    obj[idPropertyName] = fieldName;
                }
                result.push(obj);
            });
            return result;
        },
        clone: function (obj) {
            console.log("deprecated!! use angular.copy()");
            var attr = {};
            for (var key in obj) {
                if (obj[key] instanceof Array) {
                    if (obj[key]) {
                        attr[key] = [];
                    }
                    for (var i = 0; i < obj[key].length; i++) {
                        attr[key].push(obj[key][i]);
                    }
                } else {
                    attr[key] = obj[key];
                }
            }
            return attr;
        },
        /*
         * 判断两个对象是否相等，判断方法为如果两个对象是数字或字符串，用==，否则比较JSON.stringify()的字符串是否相等
         */
        equals: function (obj1, obj2) {
            if (obj1 === "true") {
                obj1 = true;
            } else if (obj1 == "false") {
                obj1 = false;
            }
            if (obj2 == "true") {
                obj2 = true;
            } else if (obj2 == "false") {
                obj2 = false;
            }
            if (com.doubeye.Ext.isString(obj1)) {
                return obj1 == obj2;
            }
            if (!isNaN(obj1)) {
                return obj1 == obj2;
            }
            // return (com.doubeye.Ext.encode(obj1) == com.doubeye.Ext.encode(obj2));
            return (JSON.stringify(obj1) == JSON.stringify(obj2));
        },
        /**
         * 根据指定的属性名来比较对象是否相等，该方法仅比对field列出的属性，其他属性将被忽略
         * @param obj1
         * @param obj2
         * @param fields
         */
        equalsByField: function (obj1, obj2, fields) {
            if (com.doubeye.Ext.isJSONObject(obj1) && com.doubeye.Ext.isJSONObject(obj2)) {
                if (!fields) {
                    fields = [];
                }
                var filteredObject1 = {}, filteredObject2 = {};
                fields.forEach(function (element) {
                    filteredObject1[element] = obj1[element];
                    filteredObject2[element] = obj2[element];
                });
                return (JSON.stringify(filteredObject1) === JSON.stringify(filteredObject2));
            } else {
                return com.doubeye.Utils.objectRefactor.equals(obj1, obj2);
            }
        },
        /**
         * 获得对象中指定的属性
         * @param object 要获得值的对象
         * @param path .分隔的属性名
         * @param pathSeparator path参数中用来分隔路径的分隔符，默认为'.'，如果默认的'.'在field中出现是，可以用此参数覆盖
         */
        getValue: function (object, path, pathSeparator) {
            if (!pathSeparator) {
                pathSeparator = '.';
            }
            if (!angular.isObject(object)) {
                return object;
            }
            var fields = path.split(pathSeparator);
            var value = object, field;
            for (var i = 0; i < fields.length; i++) {
                field = fields[i];
                try {
                    value = JSON.parse(value[field]);
                } catch (e) {
                    value = value[field];
                }
                if (!value) {
                    return value;
                }
            }
            return value;
        },
        /**
         * 根据配置获得数据
         * @param record 数据集合
         * @param config 获得数据的配置，@see com.doubeye.Utils.chart.processLineChartData 中的axisConfigs参数
         * @param valuePropertyName {String} config中保存数据节点属性名的属性，如果没有指定，则使用默认的propertyNameInCollection
         * @returns {*}
         */
        getValueByConfig: function (record, config, valuePropertyName) {
            var dataPropertyName = valuePropertyName ? valuePropertyName : 'propertyNameInCollection';
            var value = com.doubeye.Utils.objectRefactor.getValue(record, config[dataPropertyName]);
            if (angular.isFunction(config.valueFormatFunction)) {
                value = config.valueFormatFunction(value, record);
            }
            if (!value) {
                value = config.defaultValue ? config.defaultValue : 0;
            }
            return value;
        }
    },
    chart: {
        /**
         * @version 1.0.1
         * @param dataCollection 数据集合，即要被拆分的数据集
         * @param scope 拆分结果保存的属性，通常为$ctrl,或$scope下的一个属性，或控制器本身
         * @param axisValuePropertyName 用来生成统一坐标轴的属性
         * @param chartConfigs 图表配置，内容为以下对象的数组
         *    propertyName : 图表数据将保存在scope下的指定变量名下
         *    filter {function} 过滤函数，返回为true的记录不列在图中
         *    markline {Object} 标注线配置，参见EChart文档
         *    itemStyle {Object} 每个元素的样式，参见EChart文档
         *    axisConfigs [{ 一个图形的配置，此对象组成数组
		 * 		label : 该属性的显示名称
		 * 	    chartType : Enumeration<line|bar> 类型，默认为line
		 * 	    stack : {String} 当图类型为bar时，表示柱子堆叠的标志，值相同的柱子将会被堆叠在一起
		 * 		propertyNameInCollection : 指定坐标轴数据在集合中的属性名，如果为一个对象的字属性，用.标记，@see serverShellMonitor.js
		 * 		valueFormatFunction : 数值格式化函数，如果为空，则直接将集合中的属性值作为坐标轴的值,如果格式化过程中出现错误，返回空
		 * 	    defaultValue 默认值，在值不存在，或者格式化函数失败是使用此默认值，如果未提供，则此值为0
		 * 	}]
         *    @param maxDataCount 最大绘制点数，默认为不限制
         *    history :
         *        1.0.1 :
         *            + 对axisConfigs.propertyNameInCollection 添加对象支持
         *        1.0.2
         *            + 加入maxDataCount，用来限制最大的绘制数据数，避免页面卡死
         */
        processLineChartData: function (dataCollection, scope, axisValuePropertyName, chartConfigs, maxDataCount) {
            //如果chartConfig不是一个array，则记录错误
            if (!angular.isArray(chartConfigs)) {
                console.log("possible wrong chartConfigs, chartConfigs is not Array");
                return;
            }
            //如果chartConfig不是一个array，则记录错误
            if (!angular.isArray(dataCollection)) {
                console.log("possible wrong dataCollection, dataCollection is not Array");
                return;
            }
            //初始化scope中的变量
            chartConfigs.forEach(function (element) {
                scope[element.propertyName] = {
                    axis: [],
                    data: []
                };

                if (angular.isFunction(element.tipFormatter)) {
                    scope[element.propertyName].tipFormatter = element.tipFormatter;
                }
                element.axisConfigs.forEach(function (axisConfig) {
                    axisConfig.data = [];
                    /*
                    scope[element.propertyName].data.push({
                        label: axisConfig.label,
                        chartType: axisConfig.chartType,
                        stack: axisConfig.stack,
                        markLine : axisConfig.markLine,
                        itemStyle : axisConfig.itemStyle,
                        barMaxWidth :
                        data: []
                    });
                    */
                    var entryConfig = angular.copy(axisConfig);
                    entryConfig.name = axisConfig.label;
                    scope[element.propertyName].data.push(entryConfig);
                })
            });
            //循环结果集中的每一条记录
            var count = 0;
            dataCollection.forEach(function (record) {
                var axisValue = record[axisValuePropertyName];
                //循环每一个图形
                chartConfigs.forEach(function (chartConfig) {
                    //判断是否绘制的点过多，或者是否数据被指定的filter过滤掉
                    if ((!angular.isNumber(chartConfig.maxDataCount) || count < chartConfig.maxDataCount) && !(angular.isFunction(chartConfig.filter) && chartConfig.filter(record))) {
                        //设置每个chart的横坐标
                        scope[chartConfig.propertyName].axis.push(axisValue);
                        var axisConfigs = chartConfig.axisConfigs;
                        //循环每一个坐标轴
                        for (var i = 0; i < axisConfigs.length; i++) {
                            var axisConfig = axisConfigs[i];
                            var value = com.doubeye.Utils.objectRefactor.getValueByConfig(record, axisConfig);
                            scope[chartConfig.propertyName].data[i].data.push(value);
                        }
                    }
                });
                count++;


            });
        },

        processScatterChartData: function (dataCollection, scope, scopeDataProperty, chartConfig) {
            scope[scopeDataProperty] = {
                legends: [], // 图例
                chartData: [] //表格的数据
            };

            var allData = {};

            chartConfig.dimensions.forEach(function (element) {
                scope[scopeDataProperty].legends.push(element.label);
                scope[scopeDataProperty].chartData.push({
                    name: element.label,
                    type: 'scatter',
                    data: [],
                    markPoint: {
                        data: [
                            {type: 'max', name: '最大值'},
                            {type: 'min', name: '最小值'}
                        ]
                    },
                    markLine: {
                        data: [
                            {type: 'average', name: '平均值'}
                        ]
                    }
                });
                allData[element.label] = [];
            });

            chartConfig.dimensions.forEach(function (element) {
                var valuePropertyName = element.propertyName;
                dataCollection[valuePropertyName].forEach(function (dataEntry) {
                    var values = [];
                    chartConfig.dataPairs.forEach(function (pairEntry) {
                        values.push(dataEntry[pairEntry.propertyName]);
                    });
                    allData[element.label].push(values);
                });
            });

            var dataArrayInScope = scope[scopeDataProperty].chartData;
            var fields = com.doubeye.Utils.objectRefactor.getFields(allData);
            for (var i = 0; i < dataArrayInScope.length; i++) {
                for (var j = 0; fields.length; j++) {
                    if (dataArrayInScope[i].name === fields[j]) {
                        dataArrayInScope[i].data = allData[fields[j]];
                        break;
                    }
                }
            }

            console.log(scope[scopeDataProperty]);
        },
        /**
         * 格式化拼图数据
         * @param data 结果集
         * @param scope 计算结果保存的scope
         * @param valueProperty 结果集保存的属性名
         * @param dataPropertyName 数据集中的数据值属性名
         * @param idPropertyName 数据集中的图例值（id）属性名
         * @skip0 {boolean} 去掉为0的数据
         */
        processPieData: function (data, scope, valueProperty, dataPropertyName, idPropertyName, skip0) {
            scope[valueProperty] = {};
            scope[valueProperty].data = [];
            scope[valueProperty].legend = [];
            data.forEach(function (element) {
                //是否要跳过为0的数据，以简化饼图
                if ((skip0 && element[dataPropertyName] > 0) || !skip0) {
                    scope[valueProperty].data.push({
                        name: element[idPropertyName],
                        value: element[dataPropertyName]
                    });
                }
                scope[valueProperty].legend.push(element[idPropertyName]);
            });
        },
        /**
         * 注意，给定数据集合里每个维度的每个值都必须一一对应，并且顺序一致，否则可能会
         * @param dataCollection
         * @param scope
         * @param axisValuePropertyName
         * @param dataValuePropertyName 数值对象的属性，如果没有指定，则表示数据与轴的属性为平级
         * @param chartConfigs
         */
        processLineChartDataWithDynaticValueProperty: function (dataCollection, scope, axisValuePropertyName, dataValuePropertyName, chartConfigs) {
            if (!axisValuePropertyName) {
                console.warn('没有指定横坐标轴属性名');
                return;
            }
            if (!angular.isArray(chartConfigs)) {
                console.warn('给定的表格配置不是一个数组');
                return;
            }
            if (angular.isArray(dataCollection)) {
                //生成所有横坐标轴
                //生成所有横坐标轴
                var axis = {}, axisIndex = 0;
                dataCollection.forEach(function (data) {
                    axis[data[axisValuePropertyName]] = {index: axisIndex};
                    axisIndex++;
                });
                chartConfigs.forEach(function (config) {
                    scope[config.propertyName] = {
                        axis: com.doubeye.Utils.objectRefactor.getFields(axis),
                        data: []
                    };
                });
                //遍历所有结果集，构建每个纵轴所对应的属性，消除排序和数据不全的问题
                var data = [], dataSetConfig = {}, dataSetIndex = 0;
                chartConfigs.forEach(function (config) {
                    config.axisConfigs.forEach(function (axisConfig) {
                        dataSetIndex = 0;
                        dataSetConfig = {};
                        dataCollection.forEach(function (dataInCollection) {
                            var valueEntry = dataValuePropertyName ? dataInCollection[dataValuePropertyName] : dataInCollection;
                            valueEntry.forEach(function (dataInDimension) {
                                var label = com.doubeye.Utils.objectRefactor.getValue(dataInDimension, axisConfig.labelPropertyName);
                                if (!dataSetConfig[label] && dataSetConfig[label] !== 0) {
                                    dataSetConfig[label] = dataSetIndex;
                                    dataSetIndex++;
                                    scope[config.propertyName].data.push({
                                        chartType: axisConfig.chartType ? axisConfig.chartType : 'line',
                                        stack: axisConfig.stack,
                                        data: [],
                                        label: label
                                    });
                                }
                            });
                        });
                    });
                });
                //构建结果数据
                dataCollection.forEach(function (dataInCollection) {
                    var axisLabel = dataInCollection[axisValuePropertyName];
                    chartConfigs.forEach(function (chartConfig) {
                        //纵坐标轴的索引
                        var axisLabelIndex = axis[axisLabel].index;
                        var dataEntry = scope[chartConfig.propertyName].data[axisLabelIndex];
                        chartConfig.axisConfigs.forEach(function (axisConfig) {
                            var valuePropertyName = axisConfig.propertyNameInCollection;
                            var valueEntry = dataValuePropertyName ? dataInCollection[dataValuePropertyName] : dataInCollection;
                            valueEntry.forEach(function (dataInDimension) {
                                //var value = com.doubeye.Utils.objectRefactor.getValue(dataInDimension, valuePropertyName);
                                var value = com.doubeye.Utils.objectRefactor.getValueByConfig(dataInDimension, axisConfig);
                                var label = dataInDimension[axisConfig.labelPropertyName];
                                var index = dataSetConfig[label];
                                scope[chartConfig.propertyName].data[index]['data'][axisLabelIndex] = value;
                            });
                        });

                    });
                });
            } else {
                console.warn('给定的数据集不是一个数组');
            }
        },
        /**
         * 格式化多维饼图数据
         * @param data 结果集
         * @param scope 计算结果保存的scope
         * @param valueProperty 结果集保存的属性名
         * @param chartConfigs {Object} 图表配置，包含以下属性
         *  label {String} legend下的属性名 最终决定legend的属性名过程如下：
         *      如果存在labelPropertyName，则使用数据中的labelPropertyName，否则使用label
         *  labelPropertyName {String} 数据中保存legend的属性名
         *  dataPropertyName {String} 包含数据的属性名，支持.分隔的多级属性
         *  skip0 {boolean} 是否跳过为0的数据，默认为不跳过
         *  label {Object} 图表上的label属性，参见EChart说明 目前是写死的
         *  labelLine {Object} 图表上的labelLine属性，参见EChart说明 目前是写死的
         *  children {object} 内容如下：
         *      chartName {String} 字图表名称
         *      configs {Array<chartConfig>}
         * @param circleConfig {Object} 饼图中同心圆的参数，包含如下属性
         *  centerPercent {int} 中心饼图的半径占比，默认为30%
         *  voidPercent {int} 每个维度间间隔比例，默认10%
         *  shrinkScale 维度见缩小比例，默认为2
         */
        processMultiDimensionPieData : function (data, scope, valueProperty, chartConfigs, circleConfig) {
            //设置同心圆的参数
            com.doubeye.Utils.objectRefactor.merge(circleConfig, {
                centerPercent: 30,
                voidPercent: 10,
                shrinkScale: 2
            });
            var result = {
                legend: {data: []},
                series: [],
                label : {
                    show : false
                }
            };

            /**
             * 获得一个空的维度
             * @param name
             * @param seriesIndex
             * @return {{name: *, type: string}}
             */
            var getSingleEmptySeries = function (name, seriesIndex) {
                var radius = {
                    name: name,
                    type: 'pie',
                    data: [],
                    label: {
                        normal: {
                            show: false
                        },
                        emphasis: {
                            show: true
                        }
                    },
                    lableLine: {
                        normal: {
                            show: false
                        },
                        emphasis: {
                            show: true
                        }
                    }
                    //roseType : true
                };
                if (seriesIndex === 0) {
                    radius.radius = [0, circleConfig.centerPercent + '%'];
                } else {
                    var start = parseFloat(result.series[seriesIndex - 1].radius[1]) + circleConfig.voidPercent;
                    var end = start + circleConfig.centerPercent / (Math.pow(circleConfig.shrinkScale, seriesIndex));
                    radius.radius = [start + '%', end + '%'];
                }
                return radius;
            };

            /**
             * 计算多维饼图的数据
             * @param dataEntry 数据集
             * @param chartConfig 当前计算维度的配置
             * @param parentDataName 父饼图的lengend前缀
             * @param seriesIndex 维度索引
             * @param chartName 图表名称
             */
            var compute = function (dataEntry, chartConfig, parentDataName, seriesIndex, chartName) {
                dataEntry.forEach(function (entry) {
                    var legendName = chartConfig.labelPropertyName ? entry[chartConfig.labelPropertyName] : chartConfig.label;
                    if (parentDataName) {
                        legendName = parentDataName + '-' + legendName;
                    }
                    var value = com.doubeye.Utils.objectRefactor.getValueByConfig(entry, chartConfig, 'dataPropertyName');
                    if (value !== 0 || (value === 0 && !chartConfig.skip0)) {
                        result.legend.data.push(legendName);
                        if (!result.series[seriesIndex]) {
                            result.series[seriesIndex] = getSingleEmptySeries(chartName, seriesIndex);
                        }
                        result.series[seriesIndex].data.push({
                            value: value,
                            name: legendName
                        });
                        if (chartConfig.children && angular.isArray(chartConfig.children.chartConfigs)) {
                            var currentDataEntry = [];
                            currentDataEntry.push(entry);
                            chartConfig.children.chartConfigs.forEach(function (childConfig) {
                                compute(currentDataEntry, childConfig, legendName, seriesIndex + 1, chartConfig.children.chartName);
                            });
                        }
                    }
                });
            };

            chartConfigs.forEach(function (chartConfig) {
                compute(data, chartConfig, '', 0, chartConfig.chartName);
            });
            scope[valueProperty] = result;
        }
    },
    dateTime: {
        /**
         * 获得时间范围，目前支持以下枚举
         * @param range
         * @param defaultStartTime
         * @param defaultEndTime
         * @param minutes
         * @param format {String|true} 当format为true时，直接返回Date对象值,否则返回指定的格式字符串形式
         */
        getTimeRange: function (range, defaultStartTime, defaultEndTime, minutes, format) {
            if (!format) {
                format = 'yyyy-MM-dd HH:mm:ss';
            }
            var startTime, endTime, tmp;
            if (range === 'today') {
                startTime = new Date(new Date(new Date().setHours(0)).setMinutes(0)).setSeconds(0);
                endTime = new Date(new Date(new Date().setHours(23)).setMinutes(59)).setSeconds(59);
            } else if (range === 'yesterday') {
                tmp = new Date();
                tmp.setHours(tmp.getHours() - 24);
                startTime = new Date(tmp);
                startTime = new Date(new Date(startTime.setHours(0)).setMinutes(0)).setSeconds(0);
                tmp = new Date();
                tmp.setHours(tmp.getHours() - 24);
                endTime = new Date(tmp);
                endTime = new Date(new Date(endTime.setHours(23)).setMinutes(59)).setSeconds(59);
            } else if (range === 'last24Hours') {
                endTime = new Date();
                tmp = new Date();
                startTime = new Date(tmp.setHours(tmp.getHours() - 24));
            } else if (range === 'lastOneHour') {
                endTime = new Date();
                tmp = new Date();
                startTime = new Date(tmp.setHours(tmp.getHours() - 1));
            } else if (range === 'lastHalfHour') {
                endTime = new Date();
                tmp = new Date();
                startTime = new Date(tmp.setMinutes(tmp.getMinutes() - 30));
            } else if (range === 'lastSomeMinutes') {
                endTime = new Date();
                tmp = new Date();
                startTime = new Date(tmp.setMinutes(tmp.getMinutes() - minutes));
            } else if (range === 'lastDay7' || range === 'lastDay30') {
                var days = parseInt(com.doubeye.Utils.String.getStringAfter(range, 'lastDay'), 10);
                endTime = new Date();
                tmp = new Date();
                startTime = new Date(tmp.setDate(tmp.getDate() - (days - 1)));
            } else {
                startTime = defaultStartTime;
                endTime = defaultEndTime;
            }
            if (format === true) {
                return {
                    startTime: new Date(startTime),
                    endTime: new Date(endTime)
                };
            } else {
                return {
                    startTime: new Date(startTime).format(format),
                    endTime: new Date(endTime).format(format)
                };
            }
        },
        dayDiff : function(day1,  day2){    //sDate1和sDate2是2002-12-18格式
            var  aDate,  oDate1,  oDate2,  iDays;
            //aDate  =  day1.split("-");
            //oDate1  =  new  Date(aDate[1]  +  '-'  +  aDate[2]  +  '-'  +  aDate[0]);  //转换为12-18-2002格式
            oDate1 = new Date(day1);
            //aDate  =  day2.split("-");
            //oDate2  =  new  Date(aDate[1]  +  '-'  +  aDate[2]  +  '-'  +  aDate[0]);
            oDate2 = new Date(day2);
            iDays  =  parseInt((oDate1  -  oDate2)  /  1000  /  60  /  60  /24);   //把相差的毫秒数转换为天数
            return  iDays;
        },
        addDay : function (date, days) {
            return new Date(new Date(date).setDate(date.getDate() + days));
        }
    },
    /**
     * 判断一个对象数组中的field是否包含value,
     * 如果array不是数组，返回value == array;
     * 如果array简单数组，则如果存在    value == array[i]返回true, 否则返回false
     * 如果array为对象数组，则如果存在 value == array[i].field返回true,否则返回false
     * @param {Object} value 要查找的值
     * @param {Object} array 对象数组
     * @param {Object} field 对象的field
     */
    isValueInObjectArray: function (value, array, field) {
        if (!array) {
            return false;
        }
        //if (array.constructor != Array) {
        if (!angular.isArray(array)) {
            return value === array;
        }
        if (array[0] && array[0] instanceof Object) {
            for (var i = 0; array[i]; i++) {
                if (value && value.field) {
                    if (array[i][field] && array[i][field] == value[field]) {
                        return true;
                    }
                } else {
                    if (array[i][field] && array[i][field] == value) {
                        return true;
                    }
                }
            }
            return false;
        } else {
            for (var i = 0; array[i]; i++) {
                if (field && value.field) {
                    if (array[i] == value[field]) {
                        return true;
                    }
                } else {
                    if (array[i] == value) {
                        return true;
                    }
                }
            }
            return false;
        }
    },
    /**
     * 通过类的名字获得类实例
     * @param {String} className 类的名称
     * @param {Object} config 构造函数的参数
     */
    getClassInstance: function (className, config) {
        var classObject = eval(className);
        try {
            return new classObject(config);
        } catch (e) {
            alert("com.doubeye.Utils.getClassInstance()创建对象出错 , className = " + com.doubeye.Ext.encode(className));
        }
    },
    /**
     * 获得url中指定key的值
     * @param url {String} url url
     * @param {Object} skey
     * @return {TypeName}
     */
    getQueryString: function (skey) {
        var url = document.URL;
        var re = new RegExp('(\\?|&)?' + skey + '=([^&]*)');
        var value = "";
        try {
            value = re.exec(url)[2];
        } catch (e) {

        }
        return value;
    },
    /**
     * 将URL转换为全路径
     *
     * @function
     * @param {String}
     *            action 需要转换为全路径的url
     * @returns {String} 返回全路径的url
     *          @example
     *          var logoutAction=Bonc.toFullPath('/logout');
     */
    toFullPath: function (action) {
        if (!action || 'string' != typeof (action))
            return action || '';
        if (action.indexOf('/') === 0) {
            action = this.WEB_APP_CONTEXT_PATH + action;
        }
        return action;
    },
    getContextPath: function (jsUrl) {
        if (!jsUrl) {
            throw 'param JSURL must not null';
        }
        var script = document.getElementsByTagName('script');
        jsUrl = jsUrl.indexOf('/') == 0 ? jsUrl.substring(1) : jsUrl;
        for (var q = 0; q < script.length; q++) {
            var h = !!document.querySelector ? script[q].src : script[q].getAttribute("src", 4), i;
            if (h && (i = h.indexOf(jsUrl)) >= 0) {
                var j = h.indexOf('://');
                return j < 0 ? h.substring(0, i - 1) : h.substring(h.indexOf('/', j + 3), i - 1);
            }
        }
        return '/';
    },
    /**
     * 字符串工具
     */
    String: {
        /**
         *
         * @param {String} source
         *            源字符串
         * @param {String} delimiter
         *            指定字符串
         * @return {String} 源字符串(source)中指定字符串(delimiter)以前的所有字符，如果源字符串为空字符串，则返回空字符串
         *         如果源字符串中不包含指定字符串，则返回源字符串
         */
        getStringBefore: function (source, delimiter) {
            if (source === '' || !source) {
                return '';
            } else if (source.indexOf(delimiter) > -1) {
                return source.substring(0, source.indexOf(delimiter));
            } else {
                return source;
            }
        },
        /**
         *
         * @param {String} source
         *            源字符串
         * @param {String} delimiter
         *            指定字符串
         * @return {String} 源字符串(source)中指定字符串(delimiter)之后的所有字符，如果源字符串为空字符串，则返回空字符串
         *         如果源字符串中不包含指定字符串，则返回源字符串
         */
        getStringAfter: function (source, delimiter) {
            if (source === '' || !source) {
                return '';
            } else if (source.indexOf(delimiter) > -1) {
                return source.substring(source.indexOf(delimiter) + delimiter.length);
            } else {
                return '';
            }
        },
        /**
         *
         * @param {String} source
         *            源字符串
         * @param {String} delimiter
         *            指定字符串
         * @return {String} 源字符串(source)中指定字符串(delimiter)最后一次出现的位置之后之后的所有字符，如果源字符串为空字符串，则返回空字符串
         *         如果源字符串中不包含指定字符串，则返回源字符串
         */
        getStringAfterLastDelimiter: function (source, delimiter) {
            if (source === '' || !source) {
                return '';
            } else if (source.lastIndexOf(delimiter) > -1) {
                return source.substring(source.lastIndexOf(delimiter) + delimiter.length);
            } else {
                return source;
            }
        },
        /**
         * 返回源字符串(source)中指定字符串(leftDelimiter,rightDelimiter)中间的所有字符
         * 如果source中不包含两个分隔符，则返回空字符串
         *
         * @param (String) source
         *            源字符串
         * @param (String) leftDelimiter
         *            左分割字符串
         * @param (String) rightDelimiter
         *            右分割字符串
         * @return 源字符串(source)中指定字符串(leftDelimiter,rightDelimiter)中间的所有字符，如果源字符串为空字符串，则返回空字符串
         *         如果源字符串中不包含指定字符串，则返回源字符串
         */
        getStringBetween: function (source, leftDelimiter, rightDelimiter) {
            if (source == '') {
                return '';
            } else {
                if (source.indexOf(leftDelimiter) < 0 && source.indexOf(rightDelimiter, leftPosition) < 0) {
                    return '';
                }
                var leftPosition = source.indexOf(leftDelimiter);
                if (leftPosition == -1) {
                    leftPosition = 0;
                } else {
                    leftPosition += leftDelimiter.length;
                }
                var rightPosition = source.indexOf(rightDelimiter, leftPosition);
                if (rightPosition == -1) {
                    rightPosition = source.length;
                }
                return source.substring(leftPosition, rightPosition);
            }
        },
        /**
         * 获得指定字符串中指定字符串之前最近的另一字符串之后的字符
         * @param {String} source 源字符串
         * @param {String} secondDelimeter 要找的字符串在源字符串中以此参数为结尾（不包含secondDelimeter）
         * @param {String} firstDelimeter 要找的字符串在secondDelimeter之前最近的指定分隔符
         */
        getStringJustBeforeAndBetween: function (source, secondDelimeter, firstDelimeter) {
            var secondDelimeterIndex = source.indexOf(secondDelimeter), firstDelimeterIndex = 0, bugIndex = 0;
            while (!(source.indexOf(firstDelimeter, firstDelimeterIndex + 1) > secondDelimeterIndex)) {
                bugIndex++;
                firstDelimeterIndex = source.indexOf(firstDelimeter, firstDelimeterIndex + 1);
                if (source.indexOf(firstDelimeter, firstDelimeterIndex + 1) == -1) {
                    break;
                }
                if (bugIndex == 100) {
                    alert('您可能遇到程序bug了，该bug可能出现在com.doubeye.Utils.String.getStringJustBeforeAndBetween()，参数为：(' + source + ',' + secondDelimeter + ',' + firstDelimeter + ')');
                    break;
                }
            }
            return source.substring(firstDelimeterIndex, secondDelimeterIndex).trim();
        }
    },
    /**
     * 事件相关的方法
     */
    event: {
        /**
         * 获得事件发生时鼠标位置相对于出发事件的element的位置
         * @param source {Element} 触发事件的元素
         * @param event {Object} 事件对象
         */
        getEventOffset: function (source, event) {
            if (jQuery.browser.msie) {
                return {
                    top: event.offsetY,
                    left: event.offsetX
                }
            } else if (jQuery.browser.mozilla) {
                var obj = jQuery(source);
                return {
                    top: event.clientY - obj.offset().top,
                    left: event.clientX - obj.offset().left
                }
            }
        }
    },
    /**
     * 组件进入编辑模式
     * @param {com.doubeye.Component} component 组件
     * @param {boolean} enableDrag 是否允许拖动
     */
    enterEdit: function (component, enableDrag) {
        var dragHandler;
        if (enableDrag === true) {
            dragHander = jQuery('<div>', {
                'class': 'ui-widget-header com-doubeye-dragElement',
                html: 'dragHere'
            }).insertBefore(component.rootComponent.children().first());
        }
        component.rootComponent.resizable({
            handles: "all", //定义可变化大小的方向,可选值"n, e, s, w, ne, se, sw, nw","all"代表全部
            helper: "proxy",
            //aspectRatio: true,        //按照原有的比例缩放 默认是false
            autoHide: true, //是否自动隐藏变化控制器
            transparent: false,
            //grid: [10, 10],            //定义x,y轴两个方向的变化步进,单位px
            animate: true, //定义延迟是否变化大小
            animateDuration: "slow", //变化速度
            animateEasing: "swing",
            ghost: true, //是否显示变化
            resize: function (e, ui) {
            } //定义在变化大小时执行的函数
        }).addClass('com-doubeye-default-highlight').css('position', 'absolute');
        if (enableDrag) {
            component.rootComponent.draggable({
                handle: dragHander
            });
        }
    },
    /**
     *
     * @param {com.doubeye.Component} component 组件名
     * @param {function} 结束编辑后的回调函数
     */
    stopEdit: function (component, afterStopEditing) {
        component.rootComponent.resizable('destroy').draggable('destroy').removeClass('com-doubeye-default-highlight');
        var firstEl = component.rootComponent.children().first();
        if (firstEl.hasClass('com-doubeye-dragElement')) {
            firstEl.detach();
        }
        if (com.doubeye.Ext.isFunction(afterStopEditing)) {
            afterStopEditing();
        }
    },
    /**
     * 获得字符串的像素数
     */
    getStringWidthPixel: function (element, string) {
        var obj = jQuery(element);
        var s = jQuery('<span>', {
            html: string,
            style: 'display : none;'
        }).appendTo('body').css('font', obj.css('font'));
        var width = s.width();
        s.detach();
        return width;
    },
    /**
     * 根据viewId获得数据表列定义
     * @param viewerId 视图的Id,对应到
     */
    viewer: {
        getColumnDefineByViewerId: function (viewerId) {
            var columnDefines = [];
            var thiz = this;
            new com.doubeye.Ajax({
                url: '/Entity?action=getViewerColumnDefines',
                showNoSuccessMessage: true,
                noWaitingDialog: true,

                params: {
                    viewId: viewerId
                },
                processResult: function (datas) {
                    if (com.doubeye.Ext.isArray(datas)) {
                        for (var i = 0; i < datas.length; i++) {
                            var data = datas[i];
                            var define = com.doubeye.Ext.decode(data.componentConfig);
                            define.label = data.label;
                            define.dataId = data.dataId;
                            define.key = data.key;
                            if (!define.name) {
                                define.name = data.dataId;
                            }
                            columnDefines.push(define);
                        }
                    }
                }
            }).sendRequest();
            return columnDefines;
        }
    },
    php: {
        fixPhpFuckedObjectToArray: function (object) {
            console.warn('deprecated, user com.doubeye.Utils.objectRefactor.objectToArray instead, this is only for compatibility only');
            var fields = com.doubeye.Utils.objectRefactor.getFields(object);
            var result = [];
            fields.forEach(function (fieldName) {
                result.push(object[fieldName]);
            });
            return result;
        }
    }
};

com.doubeye.Utils.compatibility = {
    audioWithoutDownload: (navigator.userAgent.indexOf("Firefox") > 0) || (navigator.userAgent.indexOf("Chrome") && parseInt(com.doubeye.Utils.String.getStringBetween(navigator.userAgent, 'Chrome/', '.'), 10) < 61)
};
