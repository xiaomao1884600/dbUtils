/**
 * 表格维护组件 
 * config
 * dsId {int} 表格所在数据源的id
 * value {Object} 数据表的信息 具体内容如下：
 *  table {Object} 表本身的信息，包括：
 * 	 tableName {String} 表名
 *   tableStatus {Enumeration<com.doubeye.constant.DB.TABLE_STATUS>} 数据表的状态
 *  columns {Array<com.doubeye.Column>} 数据表的列数组
 */
com.doubeye.TableManager = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.TableManager',
	/**
	 * 数据源编号 
	 */
	dataSourceId : null,
	/**
	 * 表名 
	 */
	tableName : null,
	/**
	 * 表名组件 
	 */
	tableNameEl : null,
	/**
	 * 表状态组件 
	 */
	tableStatusEl : null,
	/**
	 * 内容页签面板 
	 */
	contentTabEl : null,
	__columnManager : null,
	tableStatus : null,
	status : '',
	init : function(){
		com.doubeye.TableManager.superclass.init.call(this);
		return this;
	},
	render : function(){
		com.doubeye.TableManager.superclass.render.call(this);
		this.__renderTableNameEl();
		this.__renderConfirmEl();
		return this;
	},
	__renderTableNameEl : function(){
		var div = jQuery('<div>', {
			'class' : this.classThemePrefix + 'TABLENAME_PANEL'
		}).appendTo(this.rootComponent);
		jQuery('<label>', {
			text : '表名：'
		}).appendTo(div);
		this.tableNameEl = new com.doubeye.TextEdit({
			parent : div,
			data : {
				validateFunction : function(value) {
					if (value == '') {
						return '表名不能为空';
					}
					return '';
				}
			}
		}).init().render();
		this.tableStatusEl = jQuery('<label>').appendTo(div);
		this.__setTableStatus(com.doubeye.constant.DB.TABLE_STATUS.UNCREATED);
		this.__renderContentTabEl();
	},
	__setTableStatus : function(status) {
		this.tableStatus = status;
		this.tableStatusEl.removeClass().addClass(this.classThemePrefix + 'STATUS').text(status);
		if (status == com.doubeye.constant.DB.TABLE_STATUS.UNCREATED) {
			this.tableStatusEl.addClass(this.classThemePrefix + 'STATUS_UNCREATED');
		} else if (status == com.doubeye.constant.DB.TABLE_STATUS.CREATED) {
			this.tableStatusEl.addClass(this.classThemePrefix + 'STATUS_CREATED');
		} else if (status == com.doubeye.constant.DB.TABLE_STATUS.META_MANAGED) {
			this.tableStatusEl.addClass(this.classThemePrefix + 'STATUS_META_MANAGED');
		} else if (status == com.doubeye.constant.DB.TABLE_STATUS.UNKNOWN) {
			this.tableStatusEl.addClass(this.classThemePrefix + 'STATUS_UNKNOWN');
		} else if (status == com.doubeye.constant.DB.TABLE_STATUS.OUT_OF_DATE) {
			this.tableStatusEl.addClass(this.classThemePrefix + 'STATUS_OUT_OF_DATE');
		}  else if (status == com.doubeye.constant.DB.TABLE_STATUS.META_MANAGED_BUT_DROPPED) {
			this.tableStatusEl.addClass(this.classThemePrefix + 'META_MANAGED_BUT_DROPPED');
		} else {
			this.tableStatusEl.addClass(this.classThemePrefix + 'STATUS_INVALID_STATUS');
			this.tableStatusEl.text(com.doubeye.constant.DB.TABLE_STATUS.INVALID_STATUS + ":" + status);
			this.tableStatus = com.doubeye.constant.DB.TABLE_STATUS.INVALID_STATUS;
		}
	},
	__renderContentTabEl : function(){
		this.contentTabEl = jQuery('<div>').appendTo(this.rootComponent);
		var content = 
		'<ul>' +
			'<li><a href="#tabColumn">列</a></li>' +
			'<li><a href="#tabIndex">索引</a></li>' +
			'<li><a href="#tabMeta">元数据</a></li>' +
		'</ul>' +
		'<div id="tabColumn' + this.rootId + '" style="overflow:scroll; padding:2px;"></div>' +
		'<div id="tabIndex' + this.rootId + '"></div>' +
		'<div id="tabMeta' + this.rootId + '"></div>';
		this.contentTabEl.html(content);
		this.contentTabEl.tabs();
		this.__columnManager = new com.doubeye.TableColumnManager({
			parent : '#tabColumn' + this.rootId,
			databaseName : com.doubeye.ColumnTypeManager.database.ORACLE
		}).init().render();
	},
	__renderConfirmEl : function(){
		var thiz = this;
		jQuery('<input>', {
			type : 'button',
			value : '确认',
			style : 'margin-left:20px;'
		}).appendTo(this.tableStatusEl.parent()).click(function(){thiz.confirm()});
	},
	confirm : function(){
		var thiz = this;
		var errorMessages = this.isValid();
		if (errorMessages.length > 0) {
			new com.doubeye.BubbleDialog({message : errorMessages}).init().render(); 
			return;
		}
		var modifiedDatas = this.__columnManager.getModifiedValue();
		var data = {
				table : {
					tableName : this.tableNameEl.getValue(),
					tableStatus : this.tableStatus
				},
				columns : modifiedDatas
		};
		
		new com.doubeye.Ajax({
			url : '/DatabaseMaintain?action=changeTableStructure',
			params : {
				dsId : this.dsId,
				data : com.doubeye.Ext.encode(data)
			},
			processResult : function(result){
				thiz.tableChangeSuccess(result);
			}
		}).sendRequest();
	},
	isValid : function() {
		var errorMessages = [];
		errorMessages.push(this.tableNameEl.isValid());
		errorMessages.push(this.__columnManager.isValid());
		for (var i = errorMessages.length - 1; i >=0 ; i --) {
			if (errorMessages[i] == '') {
				errorMessages.splice(i, 1);
			}
		}
		return errorMessages;
	},
	tableChangeSuccess : function(result){
		new com.doubeye.BubbleDialog({message : '操作成功'}).init().render();
		var tableStatus = result.tableStatus;
		this.__setTableStatus(tableStatus);
		this.__columnManager.confirmChange();
	},
	resize : function(deltaWidth, deltaHeight, div) {
		var obj = jQuery('#tabColumn');
		var oHeight = parseInt(obj.css('height'), 10);
		var height = oHeight + deltaHeight;
		obj.css('height', height);
	},
	setValue : function(value) {
		this.tableNameEl.setValue(value.tableName);
		if (value.tableStatus) {
			this.__setTableStatus(value.tableStatus);
		}
		var columnConfigs = com.doubeye.Ext.decode(value.columns);
		if (com.doubeye.Ext.isArray(columnConfigs)) {
			var column;
			for (var i = 0; i < columnConfigs.length; i ++) {
				column = columnConfigs[i];
				this.__columnManager.addColumn(column);
			}
		}
	}
});