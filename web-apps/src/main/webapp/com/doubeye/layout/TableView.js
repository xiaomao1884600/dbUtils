/**
 * 用来展示数据表
 * config
 * viewId {String} 用来获得数据表列定义的视图编号，对应CORE_TABLE_VIEWER表中的id
 * editorViewId {String} 用来获得数据表编辑记录时使用的列定义的视图编号，对应CORE_TABLE_VIEWER表中的id
 * actionRenderClassName : {ClassName<? extend com.doubeye.TableViewActionRender>} 绘制记录操作的类的名称，必须扩展自 TableViewActionRender
 * editorProviderClassName : {ClassName<? extend com.doubeye.DefaultTableViewEditorProvider>} 单条记录编辑器提供者，扩展自com.doubeye.DefaultTableViewEditorProvider，
 *  默认即为com.doubeye.DefaultTableViewEditorProvider
 * actions {Array<Enumeration<add|delete|modify|refresh>>} 即对结果集的操作，可以选择的值包括add 新增，delete 删除，modify 修改， refresh 刷新
 *  注意：可执行的动作在数组中出现的顺序决定了按钮出现的顺序
 * forceCondition {Object} 条件
 */
com.doubeye.TableView = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.TableView',
	viewId : null,
	editorViewId : null,
	resultTable : null,
	/**
	 * 绘制记录操作的类的名称
	 */
	actionRenderClassName : 'com.doubeye.TableViewActionRedner',
	__actionRender : null,
	/**
	 * 表格中使用的列定义 
	 */
	__columnDefines : null,
	editorProviderClassName : 'com.doubeye.DefaultTableViewEditorProvider',
	__editor : null,
	topBar : null,
	init : function() {
		com.doubeye.TableView.superclass.init.call(this);
		this.__getColumnDefines();
		return this;
	},
	render : function() {
		com.doubeye.TableView.superclass.render.call(this);
		this.__renderActionBar();
		this.__renderTable();
		return this;
	},
	add : function() {
		this.__openEditor();
	},
	modifyCurrentRecord : function() {
		var rows = this.resultTable.getTable().getSelectedRows();
		if (rows.length > 0) {
			if (rows.length != 1) {
				new com.doubeye.BubbleDialog({
					message : '选中了多行记录，仅编辑选中的第一行'
				}).init().render();
			}
			var row = rows[0];
			var value = row.getValue();
			this.__openEditor(value);	
		}
	},
	deleteSelectedRows : function() {
		var rows = this.resultTable.getTable().getSelectedRows();
		if (rows.length == 0) {
				new com.doubeye.BubbleDialog({
					message : '请选择要删除的记录'
				}).init().render();
			return;
		}
		var datas = [];
		for (var i = 0; i < rows.length; i ++) {
			datas.push(rows[i].getKeyFieldValue());
		}
		this.doDelete(datas);
	},
	/**
	 * 获取列定义 
	 */
	__getColumnDefines : function() {
		var thiz = this;
		new com.doubeye.Ajax({
			url : '/Entity?action=getViewerColumnDefines',
			showNoSuccessMessage : true,
			noWaitingDialog : true,
			params : {
				viewId : this.viewId
			},
			processResult : function(datas){
				if (com.doubeye.Ext.isArray(datas)) {
					thiz.__columnDefines = [];
					for (var i = 0; i < datas.length; i ++) {
						var config = datas[i];
						var componentConfig = com.doubeye.Ext.decode(datas[i].componentConfig);
						config.component = componentConfig.editorClassName;
						config.componentConfig = componentConfig;
						thiz.__columnDefines.push(config);
					}
					
				}
			}
		}).sendRequest();
	},
	/**
	 * 创建结果表格
	 */
	__renderTable : function() {
		var dataParams = {viewId : this.viewId};
		if (this.forceCondition) {
			dataParams = com.doubeye.Ext.apply(dataParams, {
				forceCondition : com.doubeye.Ext.encode(this.forceCondition)
			});
		}
		var tableConfig = {
			dataURL : '/Entity?action=getViewerData',
			dataParams : dataParams,
			parent : this.rootComponent,
			columnDefines : this.__columnDefines,
			autoLoad : true,
		};
		var plugInConfig = {
			rowCheckbox : {position : com.doubeye.constant.POSITION.LEFT},
			order : {
				multiColumn : true,
				orderType : 'server'
			},
			page : true
		};
		this.resultTable = com.doubeye.dTableFacory.getTable(tableConfig, plugInConfig);
		this.resultTable.init().render();
	},
	__renderActionBar : function() {
		var action;
		if (com.doubeye.Ext.isArray(this.actions)) {
			this.topBar = jQuery('<div>', {
				'class' : 'ui-widget-header'
			}).appendTo(this.rootComponent);
			var config = {
				parent : this.topBar,
				tableView : this
			};
			this.__actionRender = com.doubeye.Utils.getClassInstance(this.actionRenderClassName, config).init().render();
			for (var i = 0; i < this.actions.length; i ++) {
				action = this.actions[i];
				this.__actionRender.renderAction(action);
			}
		}
	},
	__openEditor : function(value) {
		if (! this.__editor) {
			var config = {
				editorViewId : this.editorViewId ? this.editorViewId : this.viewId,
				tableView : this
			};
			this.__editor = com.doubeye.Utils.getClassInstance(this.editorProviderClassName, config);
			this.__editor.init();
			this.__editor.render();
		}
		this.__editor.clearValue();
		if (value) {
			this.__editor.setValue(value);
			this.__editor.saveAction = com.doubeye.constant.SAVE_ACTION.MODIFY;
		} else {
			this.__editor.saveAction = com.doubeye.constant.SAVE_ACTION.ADD;
		}
		this.__editor.open();
	},
	doInsert : function(value) {
		var thiz = this;
		new com.doubeye.Ajax({
			url : '/Entity?action=addEntityByViewId&viewId=' + this.editorViewId,
			params : {
				data : com.doubeye.Ext.encode(value)
			},
			processResult : function() {
				thiz.doRefresh();
			}			
		}).sendRequest();
	}, 
	doModify : function(value) {
		var thiz = this;
		new com.doubeye.Ajax({
			url : '/Entity?action=modifyEntityByViewId&viewId=' + this.editorViewId,
			params : {
				data : com.doubeye.Ext.encode(value)
			},
			processResult : function() {
				thiz.doRefresh();
			}			
		}).sendRequest();
	},
	doDelete : function(datas) {
		var thiz = this;
		new com.doubeye.Ajax({
			url : '/Entity?action=deleteEntityByViewId&viewId=' + this.editorViewId,
			params : {
				datas : com.doubeye.Ext.encode(datas)
			},
			processResult : function() {
				thiz.doRefresh();
			}			
		}).sendRequest();		
	},
	doRefresh : function() {
		this.resultTable.getTable().refresh();
	}
});
