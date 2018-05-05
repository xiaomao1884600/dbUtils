com.doubeye.OpenSellingEntryAction = com.doubeye.Ext.extend(com.doubeye.Action, {
	sellEntryEl : null,
	__dialog : null,
	__fieldEditorDefines : null,
	init : function() {
		this.goodId = this.row.getValueById("id");
		this.amount = this.row.getValueById("amount");
	},
	doAction : function() {
		var thiz = this;
		this.__fieldEditorDefines = com.doubeye.Utils.viewer.getColumnDefineByViewerId(4);
		this.__dialog = new com.doubeye.ModalPanelDialog({
			buttons : [{
				text : '提交',
				click : function() {
					thiz.__saveSellingEntry();
				}
			}]
		}).init().render();
		this.__dialog.addBunchItemsByConfig(this.__fieldEditorDefines);
		this.__dialog.autoAdjustHeight();
		this.__dialog.getComponent("good_id").setValue(this.goodId);
		if (this.amount == 1) {
			var cmp = this.__dialog.getComponent("amount");
			cmp.disable();
			cmp.setValue(1);
		}
	},
	__saveSellingEntry : function() {
		var errorMessages = this.__dialog.checkValidity();
		if (errorMessages == true) {
			var value = this.__dialog.getValue();
				var thiz = this;
				new com.doubeye.Ajax({
					url : '/Entity?action=addEntityByViewId&viewId=4',
					params : {
						data : com.doubeye.Ext.encode(value),
						entityClassName : 'com.doubeye.product.goods.entity.GoodSellingEntry'
					},
					processResult : function() {
						thiz.row.setValueById('amount', thiz.amount - value.amount);
						thiz.__dialog.closeDialog();
					}			
				}).sendRequest();
		} else {
			new com.doubeye.BubbleDialog({message : errorMessages}).init().render();
		}
	}
});
