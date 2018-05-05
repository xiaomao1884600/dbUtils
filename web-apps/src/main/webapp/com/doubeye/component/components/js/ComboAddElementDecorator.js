/**
 * 下拉选择框,可以追加元素
 * @author doubeye
 * @version 1.0.1
 * config
 * value : {Mix} 组件需要显示的值
 * selectOptions {Array<Object{Mix value, String text>}} 组成下拉列表的选项
 * optionURL {URL} 获取组成下拉列表选项的url，url应该返回的类型为{RESULTS : selectionOptions}，
 * 其中selectionOptions的类型如上所述
 * optionGetter : function(Object config, com.doubeye.Combo combo) : 获得下拉列表内容的回调函数，完全有使用者定义
 * optionGetterConfig : {Object} 将此参数传递给optionGetter方法
 * 注意：设置下拉列表内容的方法的优先级为  optionGetter > optionURL > selectOptions
 * onChange : function(event, value, selectedOptionObj) : 值改变的事件
 * addDataURL {URL} 保存的url
 * addDataConfig {Object} 用来获得增加新属性的编辑界面
 * @history
 */
com.doubeye.ComboAddElementDecorator = com.doubeye.Ext.extend(com.doubeye.Combo, {
	className : 'com.doubeye.ComboAddElementDecorator',
	valueDialog : null,
	init : function() {
		com.doubeye.ComboAddElementDecorator.superclass.init.call(this);
		return this;
	},
	render : function() {
		var thiz = this;
		com.doubeye.ComboAddElementDecorator.superclass.render.call(this);
		this.__renderAddButton();
		return this;
	},
	__renderAddButton : function(){
		var thiz = this;
		jQuery('<span>', {
			style : 'background-image: url("../../images/ui-icons_228ef1_256x240.png");display: inline-block;height: 16px;width: 16px;background-position: -16px -128px; vertical-align:middle;'
		}).appendTo(this.rootComponent).click(function() {
			thiz.__renderValuePanel();
		});
	},
	__renderValuePanel : function() {
		var thiz = this;
		this.valueDialog = new com.doubeye.ModalPanelDialog({
			title : '新增',
			buttons : [{
				text : '提交',
				click : function() {
					var val = thiz.valueDialog.getValue();
					thiz.__save(val);
				}
			}]
		}).init().render();
		this.valueDialog.addBunchItemsByConfig(this.addDataConfig);
		this.valueDialog.autoAdjustHeight();
	},
	__save : function(value) {
		var thiz = this;
		new com.doubeye.Ajax({
			url : this.addDataURL,
			params : {
				data : com.doubeye.Ext.encode(value)
			},
			processResult : function() {
				thiz.__renderOptions();
				thiz.valueDialog.closeDialog();
			}			
		}).sendRequest();
	}
});