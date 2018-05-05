/**
 * 为com.doubeye.TableView提供记录操作的支持类
 * config
 * tableView {com.doubeye.TableView} 对tableView的引用
 */
com.doubeye.TableViewActionRedner = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.TableViewActionRedner',
	refreshAfterChange : true,
	tableView : null,
	init : function() {
		com.doubeye.TableViewActionRedner.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.TableViewActionRedner.superclass.render.call(this);
		return this;
	},
	renderAction : function(action) {
		if (action == 'add') {
			this.renderAdd();
		} else if (action == 'modify') {
			this.renderModify();
		} else if (action == 'delete') {
			this.renderDelete();
		} else if (action == 'refresh') {
			this.renderRefresh();
		}
	},
	renderAdd : function() {
		var thiz = this;
		new com.doubeye.Button({
			parent : this.rootComponent,
			text : '新增',
			action : function() {
				thiz.tableView.add();
			}
		}).init().render();
	},
	renderModify : function() {
		var thiz = this;
		new com.doubeye.Button({
			parent : this.rootComponent,
			text : '修改',
			action : function() {
				thiz.tableView.modifyCurrentRecord();
			}
		}).init().render();
	},
	renderDelete : function() {
		var thiz = this;
		new com.doubeye.Button({
			parent : this.rootComponent,
			text : '删除',
			action : function() {
				thiz.tableView.deleteSelectedRows();
			}
		}).init().render();
	},
	renderRefresh : function() {
		var thiz = this;
		new com.doubeye.Button({
			parent : this.rootComponent,
			text : '刷新',
			action : function() {
				thiz.tableView.doRefresh();
			}
		}).init().render();
	}
});
