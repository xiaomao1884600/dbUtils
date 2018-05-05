/**
 * @author doubeye
 * 项目管理相关概念计算工具类
 */
app.factory('projectManageUtils', [function(){
    return {
        /**
         * 根据bac计算完工尚需绩效指数（TCPI）
         * @param goal 目标数（bac）
         * @param achieve 达成数（ac）
         * @param dayLeft 剩余天数
         * @param totalDay 总天数
         * @return {number} tpci
         */
        calculateTcpiByBac : function (goal, achieve, dayLeft, totalDay) {
            if (totalDay > 0 && dayLeft > 0 && (goal > achieve)) {
                return (goal - achieve) / (goal * dayLeft / totalDay);
            } else {
                return 0;
            }
        },
        calculateException : function (achieve, dayLeft, totalDay) {
            if (dayLeft > 0 && (totalDay - dayLeft > 0)) {
                return achieve * (1 + dayLeft / (totalDay - dayLeft))
            } else {
                return 0;
            }
        }
    }
}]);