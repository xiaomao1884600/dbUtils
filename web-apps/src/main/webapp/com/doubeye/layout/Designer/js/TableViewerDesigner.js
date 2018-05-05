com.doubeye.TableViewerDesigner = com.doubeye.Ext.extend(com.doubeye.Component, {
	tableId : null,
	viewId : null,
	className : 'com.doubeye.TableViewerDesigner',
	__configEl : null,
	__previewEl : null,
	init : function() {
		com.doubeye.TableViewerDesigner.superclass.init.call(this);
		return this;
	},
	render : function() {
		com.doubeye.TableViewerDesigner.superclass.render.call(this);
		this.__renderLayoutFrame();
		return this;
	},
	__renderLayoutFrame : function() {
		this.rootComponent.height(600);
		this.__configEl = jQuery('<div>', {
			id : this.id + 'west',
			'class' : 'ui-layout-west'
		}).appendTo(this.rootComponent);
		this.__previewEl = jQuery('<div>', {
			id : this.id + 'center',
			'class' : 'ui-layout-center'
		}).appendTo(this.rootComponent);
		this.rootComponent.layout({});
	}
});
