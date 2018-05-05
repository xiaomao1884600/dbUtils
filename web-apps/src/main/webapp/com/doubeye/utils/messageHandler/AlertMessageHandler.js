com.doubeye.Ajax.MessageHandler.AlertMessageHandler = Ext.extend(com.doubeye.Ajax.MessageHandler , {
	/**
	 * 子类重写显示方法
	 */
	show : function(message){
		alert(message);
	}
});