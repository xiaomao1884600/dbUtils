/**
 * TableView的记录编辑器提供者，此类可作为类似功能类的实现接口。其他实现必须实现此类中的公用方法
 * config
 * tableView {com.doubeye.TableView} 对tableView的引用
 * editorViewId {String} 编辑记录时的视图id
 * saveAction {Enumeration<com.doubeye.constant.SAVE_ACTION>} 保存时的操作，支持动作由com.doubeye.constant.SAVE_ACTION提供
 */
com.doubeye.DefaultTableViewEditorProvider = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.DefaultTableViewEditorProvider',
	tableView : null,
	editorViewId : null,
	__fieldEditorDefines : null,
	__dialog : null,
	init : function() {
		this.parent = '@@onlyDialog@@';
		com.doubeye.DefaultTableViewEditorProvider.superclass.init.call(this);
		this.__fieldEditorDefines = com.doubeye.Utils.viewer.getColumnDefineByViewerId(this.editorViewId);
		return this;
	},
	render : function() {
		com.doubeye.DefaultTableViewEditorProvider.superclass.render.call(this);
		var thiz = this;
		this.__dialog = new com.doubeye.ModalPanelDialog({
			buttons : [{
				text : '提交',
				click : function() {
					thiz.__save();
				}
			}]
		}).init().render();
		this.__dialog.addBunchItemsByConfig(this.__fieldEditorDefines);
		this.__dialog.autoAdjustHeight();
		return this;
	},
	open : function() {
		this.__dialog.show();
		this.__dialog.autoAdjustHeight();
	},
	setValue : function(value) {
		this.__dialog.setValue(value);
	},
	__save : function() {
		var errorMessages = this.__dialog.checkValidity();
		if (errorMessages == true) {
			if (this.saveAction == com.doubeye.constant.SAVE_ACTION.ADD) {
				var value = this.__dialog.getValue();
				this.tableView.doInsert(value);
			} else if (this.saveAction == com.doubeye.constant.SAVE_ACTION.MODIFY) {
				var value = this.__dialog.getModifiedValue(); 
				this.tableView.doModify(value);
			}
			this.close();
		} else {
			new com.doubeye.BubbleDialog({message : errorMessages}).init().render();
		}
	},
	clearValue : function() {
		this.__dialog.clearValue();
	},
	close : function() {
		this.__dialog.closeDialog();
	}
});
