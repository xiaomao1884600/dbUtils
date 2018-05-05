/**
 * plugins {Object} 支持以下属性
 * rowCheckbox {Object} 行选checkbox
 *  * position {Enumeration<com.doubeye.constant.POSITION<'left'|'right'|'both'>>} 指示位置
 * order {Object} 排序插件配置，支持以下属性
 *  * multiColumn {boolean} 是否支持多列排序，默认为false
 *  * rderType {'client'|'server'} 排序类型，client代表在客户端进行排序，server代表发送排序信息并重新获得数据，指定server排序，需服务端支持排序，默认为client
 *  * orderConfigs　{Array<Object>} 其中每个Object对应一个排序规则，有dataId(String)和order(Enumeration<com.doubeye.constant.DB.ORDER_BY>)组成
 * page {boolean|Object} 指示分页器的位置，如果为true，则在表格上下都显示，如果为Object则接受以下配置
 * * position {String<'head'|'foot'|'both'>} 分页器的位置
 * * currentPage {int} 默认显示的页数
 * * recordPerPage {int} 每页的记录数，默认为10
 * tableSearch {boolean} 页面高亮
 * condition 对列增加筛选器
 *  * conditions {Array<Object<dataId{String}, value{Mix}>>} 预设的筛选条件
 * export {Object} 导出插件
 *  * server {boolean} 服务端导出 
 *  * local {boolean}  客户端导出，需IE浏览器
 * edit {boolean} 编辑插件
 */
com.doubeye.dTableFacory = {
	getTable : function(config, plugins) {
		var table = new com.doubeye.dTable(config);
		if (!plugins) {
			plugins = {};
		}
		if (plugins.rowCheckbox) {
			var config = com.doubeye.Ext.apply(plugins.rowCheckbox, {table : table}); 
			table  = new com.doubeye.dTableRowCheckboxDecorator(config);
		}
		if (plugins.order) {
			var config = com.doubeye.Ext.apply(plugins.order, {table : table}); 
			table = new com.doubeye.dTableOrderDecorator(config);
		}
		if (plugins.page) {
			var pageConfig = plugins.page;
			if (plugins.page === true) {
				pageConfig = {
					position : 'both'
				}
			}
			var config = com.doubeye.Ext.apply(pageConfig, {table : table});
			table = new com.doubeye.dTablePageDecorator(config);
		}
		if (plugins.tableSearch) {
			table = new com.doubeye.dTableTableSearchDecorator({table : table});
		}
		if (plugins.condition) {
			var config = {table : table};
			if (com.doubeye.Ext.isArray(plugins.condition.conditions)) {
				config.conditions = plugins.condition.conditions;
			}
			table = new com.doubeye.dTableConditionDecorator(config);
		}
		if (plugins['export']) {
			if (jQuery.browser.msie && plugins['export'].local) {
				var config = com.doubeye.Ext.apply(plugins['export'], {table : table});
				table = new com.doubeye.dTableLocalExportDecorator(config);
			} else {
				var config = com.doubeye.Ext.apply(plugins['export'], {table : table});
				table = new com.doubeye.dTableServerExportDecorator(config);
			}
		}
		if (plugins['edit']) {
			table = new com.doubeye.dTableEditDecorator({
				table : table,
			});
		}
		return table;
	}
};
