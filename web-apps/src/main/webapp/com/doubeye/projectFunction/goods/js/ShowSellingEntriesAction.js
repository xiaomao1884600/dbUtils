com.doubeye.ShowSellingEntriesAction = com.doubeye.Ext.extend(com.doubeye.Action, {
	init : function() {
		this.goodId = this.row.getValueById("id");
	},
	doAction : function() {
		var dialog = new com.doubeye.ModalDialog({}).init().render();
		new com.doubeye.TableView({
			parent : dialog.rootComponent,
			viewId : 5,
			actions : ['refresh'],
			forceCondition : {
				good_id : this.goodId
			}
		}).init().render();
	}
});