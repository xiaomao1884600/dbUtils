/**
 * 工具方法集合
 */



com.doubeye.Utils = {
	WEB_APP_CONTEXT_PATH : '/',
	array : {
		/**
		 * 根据数组调整数组下标
		 */
		fixArrayIndex : function(array, index) {
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
		getObjectFromArray : function(conditionObj, array, startIndex, valuePropertyName) {
			if (!com.doubeye.Ext.isArray(array)) {
				return null;
			}
			startIndex = com.doubeye.Utils.array.fixArrayIndex(array, startIndex);
			var fields = com.doubeye.Utils.objectRefactor.getFields(conditionObj), field, obj, finding = true;
			if (fields.length == 0) {
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
		}
	},
    /**
	 * 集合操作类
     */
	collection : {
        /**
		 * 将jsonObject 转化为树形组件所使用的数组格式（abnTree）
         * @param jsonObject json对象
		 * @param array abnTree的结果对象
         * @returns {Array} 结果
         */
		toAbnTreeJSON : function(value, result, parentKey) {
			if (com.doubeye.Ext.isJSONObject(value)) {
				for (var key in value) {
					var node = {label : key};
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
				for (var item in value) {
					var node = {label : '[]'};
                    if (com.doubeye.Ext.isJSON(value[key])) {
                        node.children = [];
                        com.doubeye.Utils.collection.toAbnTreeJSON(value[key], node.children);
                    }
                    result.push(node);
				}
            } else {
                var node = {label : value};
                result.push(node);
			}
		},
        /**
		 * 将jsonTree对象与比较结果进行绑定
         * @param jsonTree json的abn tree对象
         * @param compareResult 比较结果
         * @param isFirst 是否为first，如果值为true，则认为是第一个对象，否则认为是第二个对象，默认为true
         */
		abnTreeJSONCompareResultWrapper : function(jsonTree, compareResult, isFirst) {
			return jsonTree;
		}
	},
	objectRefactor : {
		/**
		 * 获得对象的所有属性
		 * @param obj 要获得属性的对象
		 */
		getFields : function(obj) {
			var result = [];
			for (var p in obj) {
				try {
					if ( typeof (obj[p]) != "function") {
						result.push(p);
					}
				} catch(e) {

				}
			}
			return result;
		},
		/**
		 * 获得对象的所有函数
		 * @param obj 要获得函数的对象
		 */
		getFunctions : function(obj) {
			var result = [];
			for (var p in obj) {
				try {
					if ( typeof (obj[p]) == "function") {
						result.push(p);
					}
				} catch(e) {

				}
			}
			return result;
		},
		clone : function(obj) {
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
		 * 判断两个对象是否相等，判断方法为如果两个对象是数字或字符串，用==，否则比较com.doubeye.Ext.encode()的字符串是否相等
		 */
		equals : function(obj1, obj2) {
			if (obj1 == "true") {
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
			return (com.doubeye.Ext.encode(obj1) == com.doubeye.Ext.encode(obj2)); 
		}
	},
	/**
	 * 判断一个对象数组中的field是否包含value,
	 * 如果array不是数组，返回value == array;
	 * 如果array简单数组，则如果存在	value == array[i]返回true, 否则返回false
	 * 如果array为对象数组，则如果存在 value == array[i].field返回true,否则返回false
	 * @param {Object} value 要查找的值
	 * @param {Object} array 对象数组
	 * @param {Object} field 对象的field
	 */
	isValueInObjectArray : function(value, array, field) {
		if (!array) {
			return false;
		}
		if (array.constructor != Array) {
			return value == array;
		}
		if (array[0] && array[0] instanceof Object) {
			for (var i = 0; array[i]; i++) {
				if (array[i][field] && array[i][field] == value) {
					return true;
				}
			}
			return false;
		} else {
			for (var i = 0; array[i]; i++) {
				if (array[i] == value) {
					return true;
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
	getClassInstance : function(className, config) {
		var classObject = eval(className);
		try {
			return new classObject(config);
		} catch (e) {
			alert("com.doubeye.Utils.getClassInstance()创建对象出错 , className = "+ com.doubeye.Ext.encode(className));
		}
	},
	/**
	 * 获得url中指定key的值
	 * @param url {String} url url
	 * @param {Object} skey
	 * @return {TypeName}
	 */
	getQueryString : function(skey) {
		var url = document.URL;
		var re = new RegExp('(\\?|&)?' + skey + '=([^&]*)');
		var value = "";
		try {
			value = re.exec(url)[2];
		} catch (e){
			
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
	toFullPath : function(action) {
		if (!action || 'string' != typeof (action))
			return action || '';
		if (action.indexOf('/') === 0) {
			action = this.WEB_APP_CONTEXT_PATH + action;
		}
		return action;
	},
	getContextPath : function(jsUrl) {
		if (!jsUrl) {
			throw 'param JSURL must not null';
		}
		var script = document.getElementsByTagName('script');
		jsUrl = jsUrl.indexOf('/') == 0 ? jsUrl.substring(1) : jsUrl;
		for (var q = 0; q < script.length; q++) {
			var h = !!document.querySelector ? script[q].src : script[q].getAttribute("src", 4), i;
			if (h && ( i = h.indexOf(jsUrl)) >= 0) {
				var j = h.indexOf('://');
				return j < 0 ? h.substring(0, i - 1) : h.substring(h.indexOf('/', j + 3), i - 1);
			}
		};
		return '/';
	},
	/**
	 * 字符串工具
	 */
	String : {
		/**
		 *
		 * @param {String} source
		 *            源字符串
		 * @param {String} delimiter
		 *            指定字符串
		 * @return {String} 源字符串(source)中指定字符串(delimiter)以前的所有字符，如果源字符串为空字符串，则返回空字符串
		 *         如果源字符串中不包含指定字符串，则返回源字符串
		 */
		getStringBefore : function(source, delimiter) {
			if (source == '') {
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
		getStringAfter : function(source, delimiter) {
			if (source == '') {
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
		getStringAfterLastDelimiter : function(source, delimiter) {
			if (source == '') {
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
		getStringBetween : function(source, leftDelimiter, rightDelimiter) {
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
		getStringJustBeforeAndBetween : function(source, secondDelimeter, firstDelimeter) {
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
	event : {
		/**
		 * 获得事件发生时鼠标位置相对于出发事件的element的位置
		 * @param source {Element} 触发事件的元素
		 * @param event {Object} 事件对象
		 */
		getEventOffset : function(source, event) {
			if (jQuery.browser.msie) {
				return {
					top : event.offsetY,
					left : event.offsetX
				}
			} else if (jQuery.browser.mozilla) {
				var obj = jQuery(source);
				return {
					top : event.clientY - obj.offset().top,
					left : event.clientX - obj.offset().left
				}
			}
		}
	},
	/**
	 * 组件进入编辑模式
	 * @param {com.doubeye.Component} component 组件
	 * @param {boolean} enableDrag 是否允许拖动
	 */
	enterEdit : function(component, enableDrag) {
		var dragHandler;
		if (enableDrag === true) {
			dragHander = jQuery('<div>', {
				'class' : 'ui-widget-header com-doubeye-dragElement',
				html : 'dragHere'
			}).insertBefore(component.rootComponent.children().first());
		}
		component.rootComponent.resizable({
			handles : "all", //定义可变化大小的方向,可选值"n, e, s, w, ne, se, sw, nw","all"代表全部
			helper : "proxy",
			//aspectRatio: true,        //按照原有的比例缩放 默认是false
			autoHide : true, //是否自动隐藏变化控制器
			transparent : false,
			//grid: [10, 10],            //定义x,y轴两个方向的变化步进,单位px
			animate : true, //定义延迟是否变化大小
			animateDuration : "slow", //变化速度
			animateEasing : "swing",
			ghost : true, //是否显示变化
			resize : function(e, ui) {
			} //定义在变化大小时执行的函数
		}).addClass('com-doubeye-default-highlight').css('position', 'absolute');
		if (enableDrag) {
			component.rootComponent.draggable({
				handle : dragHander
			});
		}
	},
	/**
	 *
	 * @param {com.doubeye.Component} component 组件名
	 * @param {function} 结束编辑后的回调函数
	 */
	stopEdit : function(component, afterStopEditing) {
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
	getStringWidthPixel : function(element, string) {
		var obj = jQuery(element)
		var s = jQuery('<span>', {
			html : string,
			style : 'display : none;'
		}).appendTo('body').css('font', obj.css('font'));
		var width = s.width();
		s.detach();
		return width;
	},
	/**
	 * 未分类方法 
	 */
	miscellaneous : {
		/**
		 * 获得当前登录用户的我的文件夹
		 * @return 在IE浏览器下，返回当前系统登录用户的我的文件夹，非IE浏览器返回""
		 * 注意：仅IE下支持 
		 */
		getSystemUserTempDir : function() {
			if (!jQuery.browser.msie) {
				alert('只有IE浏览器才能获得当前登录用户的"我的文件夹"');
				return '';
			}
			try {
				var wshShell = new ActiveXObject("WScript.Shell");
				var dir = wshShell.SpecialFolders("MyDocuments");
				return dir;
			} catch (e){
				if (e.number == -2146827859){
					//noinspection JSAnnotator
                    document.URL = com.doubeye.Utils.toFullPath('/knowledgeBase/fixIESecurityProblem.htm');
				} else {
					alert(com.doubeye.Ext.Encode(e));
				}
			}
		}
	},
	/**
	 * 根据viewId获得数据表列定义
	 * @param viewerId 视图的Id,对应到 
	 */
	viewer : {
		getColumnDefineByViewerId : function(viewerId) {
			var columnDefines = [];
			new com.doubeye.Ajax({
				url : '/Entity?action=getViewerColumnDefines',
				showNoSuccessMessage : true,
				noWaitingDialog : true,
				params : {
					viewId : viewerId
				},
				processResult : function(datas){
					if (com.doubeye.Ext.isArray(datas)) {
						for (var i = 0; i < datas.length; i ++) {
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
	}
};
