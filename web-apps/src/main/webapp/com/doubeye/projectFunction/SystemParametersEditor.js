/**
 * 系统参数编辑器 
 * @author doubeye
 * @version 1.0.0
 * config
 * defaultFunctionName {String} 仅显示默认的系统功能
 * isFunctionChangable {boolean} 是否能够改变系统功能的过滤想，默认为true
 */
com.doubeye.SystemParametersEditor = com.doubeye.Ext.extend(com.doubeye.Component, {
	paramTable : null,
	init : function() {
		com.doubeye.SystemParametersEditor.superclass.init.call(this);
		return this;		
	},
	render : function() {
		com.doubeye.SystemParametersEditor.superclass.render.call(this);
		this.__renderButtonPanel();
		this.__renderParamTable();
		return this;		
	},
	__renderButtonPanel : function() {
		var thiz = this;
		var buttonPanel = new com.doubeye.ToolBar({
			parent : this.rootComponent
		}).init().render();
		buttonPanel.addItemByConfig({
			editorClassName : 'com.doubeye.Button',
			text : '保存',
			parent : buttonPanel,
			action : function() {
				thiz.save();
			}
		});
	},
	__renderParamTable : function() {
		this.paramTable = com.doubeye.dTableFacory.getTable({
			parent : this.rootComponent,
			dataURL : '/ParameterManager?action=getAllParameters',
			columnDefines : this.__getColumnDefines() 
		}, {
			edit : true
		}).init().render();
	},
	__getColumnDefines : function() {
		var thiz = this;
		var columnDefines = [{
			columnName : 'FUNCTION_ID',
			dataId : 'FUNCTION_ID',
			label : '功能编号',
			keyColumn : true,
			hidden : true
		}, {
			columnName : 'FUNCTION_DESCRIPTION',
			dataId : 'FUNCTION_DESCRIPTION',
			label : '系统功能'
		}, {
			columnName : 'PARAMETER_NAME',
			dataId : 'PARAMETER_NAME',
			label : '功能编号',
			keyColumn : true,
			label : '参数名称'
		}, {
			columnName : 'PARA_DESCRIPTION',
			dataId : 'PARA_DESCRIPTION',
			label : '参数说明'
		}, {
			columnName : 'PARAMETER_VALUE',
			dataId : 'PARAMETER_VALUE',
			label : '参数值',
			componentRender : {
				configDataId : 'CONFIG'
			},
			contentHorizontalAlign : com.doubeye.constant.CSS_ALIGN.HORIZONTAL_ALIGN.CENTER
		}];
		return columnDefines;
	},
	__getDefaultEditor : function(td, value) {
		var cmp = new com.doubeye.TextEdit({
			parent : td,
			value : value
		}).init().render();
	},
	save : function() {
		var thiz = this;
		var modifiedData = this.paramTable.getModified().modified;
		new com.doubeye.Ajax({
			url : '/ParameterManager?action=saveParameterChanges',
			params : {
				modifies : com.doubeye.Ext.encode(modifiedData)
			},
			processServerSuccess : function(){
				new com.doubeye.BubbleDialog({
					message : '保存成功'
				}).init().render();
				thiz.paramTable.confirmChange();
			}
		}).sendRequest();
	}
});
