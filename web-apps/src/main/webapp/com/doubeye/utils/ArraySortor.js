/**
 * 数组排序器
 * @auther doubeye
 * @version 1.0.0
 * 注意：js对字符串的比较比较复杂，因此可能产生意想不到的结果，对于数字的排序更加适合
 */
com.doubeye.ArraySortor = {
	/**
	 * 为对象数组排序
	 * @param {Array<Object>} objArray 要排序的对象数组
	 * @param {Array<Object>} configs 排序规则配置数组，配置如下{dataId : (String), order : (Enumeration<com.doubeye.constant.DB.ORDER_BY>)},其中为排序的属性值
	 * @param {Boolean} isNullSmallest 如果属性为null，或者为undefined，是否作为最小值看待，没人为true，如果值为false，控制则被视为最大值
	 * @param {String} dataPropertyName 如果此值不为空，则objArray中实际比较对象值仅限于每个元素的dataPropertyName属性
	 */
	doSort : function(objArray, configs, isNullSmallest, dataPropertyName) {
		var obj1, obj2, valueObj1, valueObj2;
		for (var i = 0; i < objArray.length; i ++) {
			obj1 = objArray[i];
			for (j = i + 1; j < objArray.length; j ++) {
				obj2 = objArray[j];
				if (dataPropertyName) {
					valueObj1 = obj1[dataPropertyName];
					valueObj2 = obj2[dataPropertyName];
				} else {
					valueObj1 = obj1;
					valueObj2 = obj2;
				}
				if (com.doubeye.ArraySortor.isNeedChange(valueObj1, valueObj2, configs)) {
					com.doubeye.ArraySortor.swap(objArray, i, j);
				}
			}
		} 
	},
	/**
	 * 交换数组中的元素
	 * @param {Object} array 数组
	 * @param {Object} index1
	 * @param {Object} index2
	 */
	swap : function(array, index1, index2) {
		var tmp = array[index1];
		array[index1] = array[index2];
		array[index2] = tmp;
	},
	/**
	 * 判断根据排序规则是否需要交换两个对象
	 * @param {Object} obj1 对象1
	 * @param {Object} obj2 对象2
	 * @param {Array<Object>} configs 排序规则
	 */
	isNeedChange : function(obj1, obj2, configs, isNullSmallest) {
		//首先判断两个对象是否为空的情况
		if (com.doubeye.ArraySortor.isBothUndefined(obj1, obj2)) {
			return false;
		}
		var config, value1, value2;
		for (var i = 0 ; i < configs.length; i ++) {
			config = configs[i];
			value1 = (obj1 === null || obj1 === undefined) ? null : obj1[config.dataId];
			value2 = (obj2 === null || obj2 === undefined) ? null : obj2[config.dataId];
			/**
			 * 如果排序设置为空，则不进行判断 
			 */
			if (config.order == com.doubeye.constant.DB.ORDER_BY.NONE) {
				continue;
			} else if (config.order == com.doubeye.constant.DB.ORDER_BY.ASC) {
				if (com.doubeye.ArraySortor.isGreater(value1, value2, isNullSmallest)) {
					return true;
				}
			} else if (config.order == com.doubeye.constant.DB.ORDER_BY.DESC) {
				if (com.doubeye.ArraySortor.isSmaller(value1, value2, isNullSmallest)) {
					return true;
				}
			}
		}
		return false;
	},
	/**
	 * 判断根据排序规则对象1是否小于对象2
	 * @param {Object} obj1 对象1
	 * @param {Object} obj2 对象2
	 */
	isSmaller : function(value1, value2, isNullSmallest) {
		//同时为空或者undefined
		if (com.doubeye.ArraySortor.isBothUndefined(value1, value2)) {
			return true;
		}
		if ((value1 === null || value1 === undefined)) {
			return isNullSmallest;
		}
		if ((value2 === null || value2 === undefined)) {
			return !isNullSmallest;
		}
		return value1 < value2;
	},
	/**
	 * 判断根据排序规则对象1是否大于对象2
	 * @param {Object} obj1 对象1
	 * @param {Object} obj2 对象2
	 */	
	isGreater: function(value1, value2, isNullSmallest) {
		//同时为空或者undefined
		if (com.doubeye.ArraySortor.isBothUndefined(value1, value2)) {
			return true;
		}
		if ((value1 === null || value1 === undefined)) {
			return !isNullSmallest;
		}
		if ((value2 === null || value2 === undefined)) {
			return isNullSmallest;
		}
		return value1 > value2;
	},
	/**
	 * 判断两个值是否同时为空 
	 * @param {Object} value1
	 * @param {Object} value2
	 */
	isBothUndefined : function(value1, value2) {
		var v1 = (value1 === null || value1 === undefined) ? 0 : 1;
		var v2 = (value2 === null || value2 === undefined) ? 0 : 1;
		return (v1 + v2 == 0);
	}
};