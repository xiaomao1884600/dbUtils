/**
 * TableView中为每行添加的动作集合，该类为容器类，将为每个动作绘制一个按钮，同事会将该行的record或改行的key传给action的参数
 * config
 * tableView {com.doubeye.TableView} 对tableView的引用，可以用来回调
 * row {com.doubeye.Row} 数据行对象的引用，@see com.doubeye.Row
 * actions {Array<com.doubeye.Action>} 保存所有action的数组，每个元素会指定一个按钮
 *   @see com.doubeye.Action
 */
com.doubeye.ActionCollection = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.ActionCollection',
	keyAsValue : true,
	init : function() {
		com.doubeye.ActionCollection.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.ActionCollection.superclass.render.call(this);
		this.__renderButtons();
		return this;
	},
	__renderButtons : function() {
		if (com.doubeye.Ext.isArray(this.actions)) {
			for (var i = 0; i < this.actions.length; i ++) {
				var actionConfig = this.actions[i];
				var actionClassName = actionConfig.actionClassName;
				actionConfig.actionConfig.row = this.row;
				var actionObj = com.doubeye.Utils.getClassInstance(actionClassName, actionConfig.actionConfig);
				actionObj.init();
				new com.doubeye.Button({
					text : actionConfig.actionConfig.text,
					parent : this.rootComponent,
					actionObject : actionObj
				}).init().render();
			}
		}
	} 
});
