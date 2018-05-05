/**
 * 按钮
 * @author doubeye
 * @version 1.0.2
 * config
 * text : {String} 按钮上的文字
 * action : function(data) 按钮的单击事件 
 * actionData : {object} 用来传递给事件的参数
 * actionObject {com.doubeye.Action} 执行点击事件的动作对象
 * 注意：
 * actionObject和actionData只需要指定一个，如果同时指定，则执行actionObject.doAction
 * @history
 *  1.0.2
 *  + 加入actionObject方法，支持com.doubeye.Action指定
 * 
 */
com.doubeye.Button = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.Button',
	rootElementTypeName : '<input>',
	init : function() {
		com.doubeye.TextEdit.superclass.init.call(this);
		return this;
	},
	render : function() {
		this.renderRootComponent({type : 'button'});
		this.rootComponent.val(this.text);
		if (com.doubeye.Ext.isFunction(this.action) || this.actionObject) {
			var thiz = this;
			this.rootComponent.click(function(){
				if (thiz.actionObject) {
					thiz.actionObject.doAction();
				} else {
					thiz.action(this.actionData);
				}
			});
		}
		return this;
	},
	getValue : function() {
		return this.rootComponent.val();
	},
	setValue : function(value) {
		this.rootComponent.val(value);
	}
});
