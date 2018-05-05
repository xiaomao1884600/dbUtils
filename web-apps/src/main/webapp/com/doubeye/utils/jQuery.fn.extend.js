jQuery.fn.extend({
	/**
	 * 在组件上附加可编辑的状态
	 * @param {function} callback 从编辑状态返回到预览状态的回调函数
	 * @param {Object} inputCfg 输入控件的样式等信息
	 */
	editable : function(callback, inputCfg) {
		inputCfg = inputCfg || {};
		return this.each(function() {
			var obj = jQuery(this), editObj = jQuery('<input>', inputCfg || {}).val(obj.text()).insertAfter(obj).hide();
			obj.bind('dblclick', function() {
				if (obj.click && inputCfg.obj) {
					clearTimeout(inputCfg.obj.timer);
				}
				editObj.css('width', obj.innerWidth()).show().focus();
				obj.hide();
			});
			editObj.bind('blur', function() {
				editObj.hide();
				obj.show();
				if (callback && typeof callback == 'function') {
					if (callback(this.value) === false) {
						this.value = obj.text();
						return;
					}
				}
				obj.text(this.value);
			});
		});
	},
	/**
	 * 是指定的element能够通过点击隐藏或显示其child元素
	 * coinfg {Object}
	 *  childSelector {Selector} 表示需要隐藏的 最顶层元素选择器
	 *  collapsed {boolean} 是否为折叠状态，默认为false
	 */
	collapsable : function(config){
		if (!config) {
			config = {};
		}
		return this.each(function(){
			var obj = jQuery(this);
			var expandButton = jQuery('<span>', {
				'class' : 'collapsable-expandButton'
			});
			var firstChild = obj.first();
			if (firstChild.length == 0) {
				expandButton.appendTo(obj);
			} else {
				firstChild.before(expandButton);
			}
			var expandRangion = null;
			if (config.childSelector) {
				expandRangion = obj.children(config.childSelector);
			} else {
				expandRangion = obj.children();
			}
			if (config.collapsed) {
				expandButton.removeClass('collapsable-expanded');
				expandButton.addClass('collapsable-collapsed');
				expandRangion.fadeOut();
			} else {
				expandButton.removeClass('collapsable-collapsed');
				expandButton.addClass('collapsable-expanded');
			}
			expandButton.unbind('click');
			expandButton.click(function(){
				if (expandButton.hasClass('collapsable-expanded')){
					expandButton.removeClass('collapsable-expanded');
					expandButton.addClass('collapsable-collapsed');
					expandRangion.fadeOut();
				} else {
					expandButton.removeClass('collapsable-collapsed');
					expandButton.addClass('collapsable-expanded');
					expandRangion.fadeIn();
				}
			});
		});
	},
	/**
	 * 使指定元素悬浮
	 * @param {Object} config
	 * top {integer} 悬浮位置的上侧坐标
	 * left {integer} 悬浮的左侧坐标
	 */
	float : function(config) {
		config = config ? config : {};
		var needCalculateWidth = !config.left, needCalculateHeight = !config.top;
		/**
		 * TODO 这种计算方法有为题，回头问问刘世民
		 */
		config.left = config.left ? config.left : window.screen.availWidth / 2;
		config.top = config.top ? config.top : window.screen.availHeight / 2;
		return this.each(function() {
			var obj = jQuery(this);
			var left = config.left, top = config.top;
			var wrapper = jQuery('<div class="com-doubeye-default-float"></div>');
			if (needCalculateWidth) {
				left = left - obj.outerWidth() / 2;
			}
			if (needCalculateHeight) {
				top = top - obj.outerHeight() / 2;
			}
			wrapper.attr('style', 'left:' + left + 'px;' + 'top:' + top + 'px;');
			obj.wrap(wrapper);
		});
	},
	/**
	 * 取消指定元素的悬浮，如不指定该hide和detach，则元素将恢复到原来所在位置
	 * @param {Object} config
	 * hide {boolean} 元素变为不可见
	 * detach {boolean} 元素被销毁
	 */
	unfloat : function(config) {
		config = config ? config : {
			detach : false,
			hide : fasle
		};
		return this.each(function() {
			var obj = jQuery(this);
			if (obj.parent().hasClass('com-doubeye-default-float')) {
				obj.unwrap();
			}
			if (config.detach) {
				obj.detach();
			}
			if (config.hide) {
				obj.hide();
			}
		});
	}
});

jQuery(function() {
	jQuery.datepicker.regional['zh-CN'] = {
		clearText : '清除',
		clearStatus : '清除已选日期',
		closeText : '关闭',
		closeStatus : '不改变当前选择',
		prevText : '<上月',
		prevStatus : '显示上月',
		prevBigText : '<<',
		prevBigStatus : '显示上一年',
		nextText : '下月>',
		nextStatus : '显示下月',
		nextBigText : '>>',
		nextBigStatus : '显示下一年',
		currentText : '今天',
		currentStatus : '显示本月',
		monthNames : ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
		monthNamesShort : ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
		//monthNamesShort: ['一','二','三','四','五','六', '七','八','九','十','十一','十二'],
		monthStatus : '选择月份',
		yearStatus : '选择年份',
		weekHeader : '周',
		weekStatus : '年内周次',
		dayNames : ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'],
		dayNamesShort : ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
		dayNamesMin : ['日', '一', '二', '三', '四', '五', '六'],
		dayStatus : '设置 DD 为一周起始',
		dateStatus : '选择 m月 d日, DD',
		dateFormat : 'yy-mm-dd',
		firstDay : 1,
		initStatus : '请选择日期',
		isRTL : false
	};
	$.datepicker.setDefaults($.datepicker.regional['zh-CN']);
});
//jquery combobox
(function($) {
	$.widget("custom.combobox", {
		_create : function() {
			this.wrapper = $("<span>").addClass("custom-combobox").insertAfter(this.element);
			this.element.hide();
			this._createAutocomplete();
			this._createShowAllButton();
		},
		_createAutocomplete : function() {
			var selected = this.element.children(":selected"), value = selected.val() ? selected.text() : "";
			this.input = $("<input>").appendTo(this.wrapper).val(value).attr("title", "").addClass("custom-combobox-input ui-widget ui-widget-content ui-state-default ui-corner-left").autocomplete({
				delay : 0,
				minLength : 0,
				source : $.proxy(this, "_source")
			}).tooltip({
				tooltipClass : "ui-state-highlight"
			});
			this._on(this.input, {
				autocompleteselect : function(event, ui) {
					ui.item.option.selected = true;
					this._trigger("select", event, {
						item : ui.item.option
					});
				},
				autocompletechange : "_removeIfInvalid"
			});
		},
		_createShowAllButton : function() {
			var input = this.input, wasOpen = false;
			$("<a>").attr("tabIndex", -1).attr("title", "Show All Items").tooltip().appendTo(this.wrapper).button({
				icons : {
					primary : "ui-icon-triangle-1-s"
				},
				text : false
			}).removeClass("ui-corner-all").addClass("custom-combobox-toggle ui-corner-right").mousedown(function() {
				wasOpen = input.autocomplete("widget").is(":visible");
			}).click(function() {
				input.focus();
				// Close if already visible
				if (wasOpen) {
					return;
				}
				// Pass empty string as value to search for, displaying all results
				input.autocomplete("search", "");
			});
		},
		_source : function(request, response) {
			var matcher = new RegExp($.ui.autocomplete.escapeRegex(request.term), "i");
			response(this.element.children("option").map(function() {
				var text = $(this).text();
				if (this.value && (!request.term || matcher.test(text) ))
					return {
						label : text,
						value : text,
						option : this
					};
			}));
		},
		_removeIfInvalid : function(event, ui) {
			// Selected an item, nothing to do
			if (ui.item) {
				return;
			}
			// Search for a match (case-insensitive)
			var value = this.input.val(), valueLowerCase = value.toLowerCase(), valid = false;
			this.element.children("option").each(function() {
				if ($(this).text().toLowerCase() === valueLowerCase) {
					this.selected = valid = true;
					return false;
				}
			});
			// Found a match, nothing to do
			if (valid) {
				return;
			}
			// Remove invalid value
			this.input.val("").attr("title", value + " didn't match any item").tooltip("open");
			this.element.val("");
			this._delay(function() {
				this.input.tooltip("close").attr("title", "");
			}, 2500);
			this.input.data("ui-autocomplete").term = "";
		},
		_destroy : function() {
			this.wrapper.remove();
			this.element.show();
		}
	});
})(jQuery); 