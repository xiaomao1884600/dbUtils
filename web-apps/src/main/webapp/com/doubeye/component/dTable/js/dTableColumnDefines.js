/**
 * 用来定义dTable使用的标题信息数组（列定义信息）
 * @author doubeye
 * @version 1.0.0
 * config 
 *  columnDefines (Array<com.doubeye.dTableColumnDefine>) 列定义信息
 */
com.doubeye.dTableColumnDefines = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.dTableColumnDefines',
	/**
	 * 列定义的深度，即列一共划分几层 
	 */
	__columnDeep : 1,
	/**
	 * 表格的表头trs 
	 */
	trs : null,
	/**
	 * 保存所有列定义对象的数组 
	 */
	columnDefinesObj : null,
	init : function() {
		this.trs = [];
		this.columnDefinesObj = [];
		this.config = com.doubeye.Ext.apply(this.config, {
			resizePosition : 'both'
		});
		com.doubeye.dTableColumnDefines.superclass.init.call(this);
		this.organizeColumnLevelAndColSpan(this.columnDefines, 1);
		this.organizeColumnRowSpan(this.columnDefines);
		//this.rootElementTypeName = '<thead>';
		return this;
	},
	render : function() {
		if (this.parentEl[0].tagName.toLowerCase() != 'thead') {
			throw 'com.doubeye.dTableColumnDefine 的父组件必须是<table>';
		}
		//com.doubeye.dTableColumnDefines.superclass.render.call(this);
		this.rootComponent = this.parentEl;
		this.renderTrs();
		this.renderColumnDefines(this.columnDefines);
		return this;
	},
	/**
	 * 维护column的层次信息和colSpan属性，此函数为递归函数 
	 * 该函数运行结束后，columnDefile中会添加以下属性：
	 * level 定义该column在第几行
	 * colSpan 定义column的colSpan
	 * rowSpan 定义column的rowSpan
 	 * @param {Array<com.doubeye.dTableColumnDefine>} columnDefines 要维护的columnDefine数组
 	 * @param {integer} level 层次
	 */
	organizeColumnLevelAndColSpan : function(columnDefines, level) {
		for(var i = 0; i < columnDefines.length; i ++) {
			columnDefines[i].level = level;
			//如果该列有子节点，则列深度 +1
			if (columnDefines[i].children) {				
				this.__columnDeep = Math.max(columnDefines[i].level + 1, this.__columnDeep);
				//递归
				this.organizeColumnLevelAndColSpan(columnDefines[i].children, columnDefines[i].level + 1);
				//改行为了防止没有此属性时无法进行累加
				columnDefines[i].colSpan = 0;
				for (var j = 0; j < columnDefines[i].children.length; j ++) {
					columnDefines[i].colSpan += columnDefines[i].children[j].colSpan;
				}
			} else {
				columnDefines[i].colSpan = 1;
			}
		}
	},
	organizeColumnRowSpan : function(columnDefines) {
		for(var i = 0; i < columnDefines.length; i ++) {
			//如果该列有子节点则进行递归，如没有子节点，则设置该列的rowSpan = this.__columnDeep - level + 1;
			if (columnDefines[i].children) {				
				//递归
				this.organizeColumnRowSpan(columnDefines[i].children);
				//改行为了防止没有此属性时无法进行累加
				var maxChildrenRowSpan = 0;
				for (var j = 0; j < columnDefines[i].children.length; j ++) {
					maxChildrenRowSpan = Math.max(maxChildrenRowSpan, columnDefines[i].children[j].rowSpan);
				}
				columnDefines[i].rowSpan = this.__columnDeep - columnDefines[i].level - maxChildrenRowSpan + 1;
			} else {
				columnDefines[i].rowSpan = this.__columnDeep - columnDefines[i].level + 1;
			}
		}
	},
	/**
	 * 绘制表头的trs 
	 */
	renderTrs : function() {
		for (var i = 1; i <= this.__columnDeep; i ++) {
			var tr = jQuery('<tr>').appendTo(this.rootComponent);
			this.trs.push(tr);
		}
	},
	/**
	 * 绘制所有的列 ，此函数为递归函数
 	 * @param {Object} columnDefines 要绘制的列定义
 	 * @param {Object} parentColumn 该列定义的父列
	 */
	renderColumnDefines : function(columnDefines, parentColumn) {
		var columnDefine, column;
		for (var i = 0; i < columnDefines.length; i ++) {
			columnDefine = columnDefines[i];
			columnDefine.parent = this.trs[columnDefine.level - 1];
			var config = com.doubeye.Ext.apply(columnDefine, {resizable : columnDefine.children ? false : true});
			column = new com.doubeye.dTableColumnDefine(config);
			column.init().render();
			//如果存在父节点，则维护列之间的父子关系
			if (parentColumn) {
				//确保存在childrenObj属性
				if (!parentColumn.childrenObj) {
					parentColumn.childrenObj = [];
				}
				parentColumn.childrenObj.push(column);
			} else {
				this.columnDefinesObj.push(column);
			}
			if (columnDefine.children) {
				this.renderColumnDefines(columnDefine.children, column);
			}
		}
	},
	/**
	 * 获得最底层的列定义，由于列定义中所有与数据相关的列都在最底层定义，因此获得最底层列定义及其顺序，是table绘制数据的基础,此函数为递归函数 
	 * @param columnDefines {Array<com.doubeye.dTableColumnDefines>} columnDefines
	 * @result {Array<com.doubeye.dTableColumnDefines>} 最底层的列定义
	 */
	__getDeepestColumnDefines : function(columnDefines, result) {
		for (var i = 0; i < columnDefines.length; i ++) {
			var columnDefine = columnDefines[i];
			if (columnDefine.childrenObj) {
				this.__getDeepestColumnDefines(columnDefine.childrenObj, result);
			} else {
				result.push(columnDefine);
			}
		}
	},
	/**
	 * 获得列定义中的主键字段，如果没有定义主键字段（即在列定义中，没有keyColumn == true的字段），则返回[]
	 * @return {Array[com.doubeye.dTableColumnDefile]} 主键字段数组 
	 */
	getKeyColumns : function() {
		var result = [];
		for (var i = 0; i < this.columnDefines.length; i ++) {
			if (this.columnDefines[i].keyColumn === true) {
				result.push(this.columnDefines[i]);
			}
		}
		return result;
	},
	/**
	 * 获得最底层的列定义，及数据列定义，由于列定义中所有与数据相关的列都在最底层定义，因此获得最底层列定义及其顺序，是table绘制数据的基础 
	 * @result 数据列定义
	 */
	getDataColumnDefines : function() {
		var result = [];
		this.__getDeepestColumnDefines(this.columnDefinesObj, result);
		return result;
	}
});