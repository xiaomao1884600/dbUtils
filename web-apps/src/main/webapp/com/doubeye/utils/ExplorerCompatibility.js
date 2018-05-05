com.doubeye.ExplorerCompotibility = {
	/**
	 *  用来兼容IE的document.all
	 */
	getDocumentAll : function() {
		if (jQuery.browser.msie) {
			return document.all;
		} else if (jQuery.browser.mozilla){
			return document.getElementsByTagName('*');
		}
		return null;
	},
	getAllByTagName : function(tagName) {
		if (jQuery.browser.msie) {
			return document.all.tags(tagName);
		} else if (jQuery.browser.mozilla){
			return document.getElementsByTagName(tagName);
		}
		return null;		
	}
};
