/**
 * @author doubeye
 * 数学相关工具类
 */
app.factory('mathUtils', [function(){
    return {
        /**
         * 四舍五入
         * @param value 原始值
         * @param scale 保留的小数位数
         * @return {number} 四舍五入后的值
         */
        round : function (value, scale) {
            var factor = Math.pow(10, scale);
            return Math.round(value * factor) / factor;
        }
    }
}]);