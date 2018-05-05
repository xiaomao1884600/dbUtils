/**
 * @author doubeye
 * 集合相关工具类
 */
app.factory('collectionUtils', [function(){
    return {
        /**
         * 将数组转化为json对象
         * @param array 要被转化的数组 json对象数组
         * @param idPropertyName 转换时作为对象属性名的属性名
         * @param idPropertyPrefix 转换后对象属性名前缀
         * @param callback 回调函数，每转换一个元素，进行回调设置，回调参数为
         *   resultObject 整个的结果对象，包含每个元素
         *   entryObject 单个元素
         */
        arrayToObject : function (array, idPropertyName, idPropertyPrefix, callback) {
            var result = {};
            if (!idPropertyPrefix) {
                idPropertyPrefix = '';
            }
            if (angular.isArray(array)) {
                array.forEach(function (element) {
                    result[idPropertyPrefix + element[idPropertyName]] = element;
                    if (angular.isFunction(callback)) {
                        callback(result, result[idPropertyPrefix + element[idPropertyName]]);
                    }
                });
            }
            return result;
        }
    }
}]);