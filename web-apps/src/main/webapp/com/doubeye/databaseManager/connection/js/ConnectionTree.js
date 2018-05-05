/**
 * 数据库连接管理器
 *
 */
com.doubeye.ConnectionTree = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.ConnectionTree',
	treePanelEl : null,
	dsEditorPanelEl : null,
	dsEditor : null,
	treeEl : null,
	tree : null,
	menu : null,
	menuItems : null,
	init : function() {
		com.doubeye.ConnectionTree.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.ConnectionTree.superclass.render.call(this);
		this.rootComponent.css('width', '100%').css('height', '100');
		this.__renderLayout();
		this.__renderZTree();
		this.getAllConnections();
		this.__renderMenu();
		return this;
	},
	__renderLayout : function() {
		var table = jQuery('<table>', {}).appendTo(this.rootComponent);
		var tr = jQuery('<tr>').appendTo(table);
		this.treePanelEl = jQuery('<td>', {
			style : 'vertical-align:top;'
		}).appendTo(tr);
		this.dsEditorPanelEl = jQuery('<td>', {
			style : 'vertical-align:top;'
		}).appendTo(tr);
	},
	/**
	 * 获得所有的连接
	 */
	getAllConnections : function() {
		var thiz = this;
		new com.doubeye.Ajax({
			url : '/DatabaseMaintain?action=getAllRegistratedDataSource',
			processResult : function(result) {
				thiz.renderDataSources(result);
			}
		}).sendRequest();
	},
	renderDataSources : function(dataSources) {
		var ds;
		for (var i = 0; i < dataSources.length; i++) {
			ds = dataSources[i];
			this.tree.addNodes(null, {
				id : ds.id,
				pId : ds.id,
				isParent : true,
				name : ds.name,
				typeId : ds.dsTypeId,
				nodeType : 'datasource',
				description : ds.description,
				config : com.doubeye.Ext.decode(ds.config)
			});
		}
	},
	__renderZTree : function() {
		var thiz = this;
		this.treeEl = jQuery('<ul>', {
			'class' : 'ztree'
		}).appendTo(this.treePanelEl).click(function(){
			if (thiz.menu) {
				thiz.menu.hide();
			}
		});
		this.initTree();
	},
	initTree : function() {
		var thiz = this;
		this.tree = jQuery.fn.zTree.init(this.treeEl, {
			data : {
				keep : {
					parent : true,
					leaf : true
				},
				simpleData : {
					enable : true
				}
			}, callback : {
				beforeExpand : function(treeId, node) {
					thiz.menu.hide();
					if (node.nodeType == 'datasource') {
						thiz.getTables(node);
					} else if (node.nodeType == 'table'){
						thiz.getColumns(node);
					}
				},
				onRightClick : function(event, treeid, node) {
					thiz.tree.selectNode(node, false);
					thiz.showMenu(node, event.clientX, event.clientY);
				},
				// mousedown : function() {
					// thiz.menu.hide();
				// },
				onCollapse : function() {
					thiz.menu.hide();
				}
			}
			/*,
			view : {
				addDiyDom : function(treeId, treeNode){
					var liObj = $("#" + treeNode.tId);
					jQuery('<span>').appendTo(liObj).attr('type', treeNode.nodeType).attr('nodename', treeNode.id);
				}
			}
			*/
		}, null);
	},
	getTables : function(node) {
		var thiz = this;
		new com.doubeye.Ajax({
			url : '/DatabaseMaintain?action=getTablesByDataSource',
			params : {
				id : node.id
			},
			async : true,
			processResult : function(result) {
				thiz.renderTables(node, result);
			}
		}).sendRequest();
	},
	renderTables : function(dsNode, tables) {
		var table;
		this.tree.removeChildNodes(dsNode);
		for (var i = 0; i < tables.length; i++) {
			table = tables[i];
			this.tree.addNodes(dsNode, {
				id : dsNode.id,
				pId : table,
				isParent : true,
				name : table,
				nodeType : 'table'
			});
		}
	},
	getColumns : function(tableNode) {
		var thiz = this;
		new com.doubeye.Ajax({
			url : '/DatabaseMaintain?action=getColumnsByTableName',
			params : {
				id : tableNode.id,
				tableName : tableNode.name
			},
			async : true,
			processResult : function(result) {
				thiz.renderColumns(tableNode, result);
			}
		}).sendRequest();
	},
	renderColumns : function(tableNode, columns) {
		var column;
		this.tree.removeChildNodes(tableNode);
		for (var i = 0; i < columns.length; i++) {
			column = columns[i];
			this.tree.addNodes(tableNode, {
				id : tableNode.id,
				pId : column,
				isParent : false,
				name : column,
				nodeType : 'column'
			});
		}
	},
	showMenu : function(node, x, y) {
		if (!node) {//没有点击到节点上，则只显示新建数据源
			this.menu.showMenuItem('addDatasource');
			this.menu.hideMenuItem('editDatasource');
			this.menu.hideMenuItem('deleteDatasource');
			this.menu.hideMenuItem('addTable');
			this.menu.hideMenuItem('editTable');
			this.menu.hideMenuItem('dropTable');
		} else if (node.nodeType == 'datasource') {
			this.menu.showMenuItem('addDatasource');
			this.menu.showMenuItem('editDatasource');
			this.menu.showMenuItem('deleteDatasource');
			this.menu.showMenuItem('addTable');
			this.menu.hideMenuItem('editTable');
			this.menu.hideMenuItem('dropTable');
		} else if (node.nodeType == 'table'){
			this.menu.hideMenuItem('addDatasource');
			this.menu.hideMenuItem('editDatasource');
			this.menu.hideMenuItem('deleteDatasource');;
			this.menu.hideMenuItem('addTable');
			this.menu.showMenuItem('editTable');
			this.menu.showMenuItem('dropTable');
		} else if (node.nodeType == 'column' || !node) {//当节点类型为数据列，不弹出菜单，并关闭已经打开的菜单
			this.menu.hide();
			return;
		}
		this.menu.show(x, y);
	},
	__renderMenu : function() {
		var thiz = this;
		var meniItems = [{
			name : 'addDatasource',
			text : '增加数据源',
			action : function(){
				thiz.addDatasource();
			}
		}, {
			name : 'editDatasource',
			text : '编辑数据源',
			action : function(){
				thiz.editDataSource();
			}
		}, {
			name : 'deleteDatasource',
			text : '删除数据源',
			action : function() {
				thiz.deleteDataSource();
			}
		}, {
			name : 'refreshTable',
			text : '刷新',
			action : function(){
				var node = thiz.getSelectedNode();
				if (!node) {
					thiz.initTree();
					thiz.getAllConnections();
				} else if (node.nodeType == 'datasource'){
					thiz.getTables(node);
				}
			}			
		}, {
			name : 'addTable',
			text : '新建表',
			action : function(){
				thiz.createTable();
			}
		}, {
			name : 'dropTable',
			text : '删除表',
			action : function(){
				thiz.dropTable();
			}
		}, {
			name : 'editTable',
			text : '编辑表',
			action : function(){
				thiz.editTable();
			}
		}];		
		this.menu = new com.doubeye.PopMenu({
			parent : this.rootComponent,
			menuItems : meniItems,
			width : 80
		}).init().render();
	},
	addDatasource : function() {
		if (!this.dsEditor) {
			this.createDSEditor();
		} else {
			this.dsEditor.clearValue();
			this.dsEditor.show();
		}
	},
	editDataSource : function() {
		var node = this.getSelectedNode();
		if (node) {
			if (!this.dsEditor) {
				this.createDSEditor();
			} else {
				this.dsEditor.show();
			}
			var value = {
				id : node.id,
				name : node.name,
				config : node.config,
				typeId : node.typeId,
				description : node.description
			};
			this.dsEditor.setValue(value);
		}
	},
	deleteDataSource : function() {
		var node = this.getSelectedNode(), thiz = this;
		if (node) {
			var id = node.id;
			new com.doubeye.Ajax({
				url : '/DatabaseMaintain?action=deleteDataSource',
				params : {dsId : id},
				processResult : function() {
					thiz.initTree();
					thiz.getAllConnections();
					thiz.dsEditor.hide();
				}
			}).sendRequest();
		}		
	},
	createDSEditor : function() {
		var thiz = this;
		this.dsEditor = new com.doubeye.ConnectionManager({
			parent : this.dsEditorPanelEl,
			afterSave : function(datasourceConfig, added) {
				var dsId = datasourceConfig.id;
				if (added) {//如果是增加，需要重新刷新树
					thiz.initTree();
					thiz.getAllConnections();	
				}
				var nodes = nodes = thiz.tree.getNodesByParam('id', dsId), node;
				if (nodes && nodes.length > 0) {
						node = nodes[0];
				}
				if (node) {
					if (!added) {//如果是修改则将值写回到node上
						node.name = datasourceConfig.name;
						node.dsTypeId = datasourceConfig.typeId;
						node.description = datasourceConfig.description;
						node.config = com.doubeye.Ext.decode(datasourceConfig.config);
					}
				}
				thiz.tree.selectNode(node, false);
				thiz.tree.updateNode(node);
			}
		}).init().render();		
	},
	createTable : function() {
		var node = this.getSelectedNode();
		var thiz = this;
		var div = jQuery('<div>').dialog({
			width : 1024,
			height : 768,
			resizeStop: function(event, ui) {
				var deltaWidth = ui.size.width - ui.originalSize.width;
				var deltaHeight = ui.size.height - ui.originalSize.height;
				tableManager.resize(deltaWidth, deltaHeight, div);
			}
		});
		var tableManager = new com.doubeye.TableManager({
			parent : div,
			dsId : node.id
		});
		tableManager.init();
		tableManager.render();
	},
	editTable : function() {
		var node = this.getSelectedNode(), thiz = this;
		var div = jQuery('<div>').dialog({
			width : 1024,
			height : 768,
			resizeStop: function(event, ui) {
				var deltaWidth = ui.size.width - ui.originalSize.width;
				var deltaHeight = ui.size.height - ui.originalSize.height;
				tableManager.resize(deltaWidth, deltaHeight, div);
			}
		});
		var tableManager = new com.doubeye.TableManager({
			parent : div,
			dsId : node.id
		});
		tableManager.init();
		tableManager.render();
		
		var node = this.getSelectedNode(), thiz = this;
		var tableName = node.name;
		var dsId = node.getParentNode().id;
		new com.doubeye.Ajax({
				url : '/DatabaseMaintain?action=getTableInfo',
				params : {dsId : dsId, tableName : tableName},
				processResult : function(value) {
					tableManager.setValue(value);
				}
			}).sendRequest();
	},
	dropTable : function() {
		var node = this.getSelectedNode(), thiz = this;
		var tableName = node.name;
		var dsId = node.getParentNode().id;
		new com.doubeye.Ajax({
				url : '/DatabaseMaintain?action=dropTable',
				params : {dsId : dsId, tableName : tableName},
				processResult : function() {
					new com.doubeye.BubbleDialog({message : '删除表' + tableName + '成功'}).init().render();
					thiz.tree.removeNode(node);
				}
			}).sendRequest();
	},
	getSelectedNode : function() {
		var nodes = this.tree.getSelectedNodes(), thiz = this;
		if (nodes.length > 0) {
			var node = nodes[0];
			return node;
		} else {
			return null;
		}
	}
}); 