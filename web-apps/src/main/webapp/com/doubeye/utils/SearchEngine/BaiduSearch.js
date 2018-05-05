/**
 * 嵌入式的百度搜索
 * @author doubeye
 * @version 1.0.0
 * value {String} 搜索的内容
 * showLogo {boolean} 是否显示百度的logo，默认为true
 * inputSize {int} 输入框的长度 ,单位为字符,默认为40
 */
com.doubeye.BaiduSearch = com.doubeye.Ext.extend(com.doubeye.Component, {
	className : 'com.doubeye.BaiduSearch',
	inputSize : 40,
	/**
	 * 百度的form 
	 */
	__formEl : null,
	init : function(){
		com.doubeye.AdvenceErrorDialog.superclass.init.call(this);
		return this;
	},  	
	render : function() {
		com.doubeye.AdvenceErrorDialog.superclass.render.call(this);
		this.formEl = jQuery('<form action="http://www.baidu.com/baidu" target="_blank" style="vertical-align:middle;">' +  
    		'<table bgcolor="#FFFFFF"><tr><td style="vertical-align:middle;"><input name="tn" type="hidden" value="baidu">' + (this.showLogo ? '<a href="http://www.baidu.com/" mce_href="http://www.baidu.com/" style="padding-right:3px;"><img src="http://img.baidu.com/img/logo-80px.gif" mce_src="http://img.baidu.com/img/logo-80px.gif" alt="Baidu" align="bottom" border="0"></a>' : '') +  
    		'<input type="text" name="word" size="' + this.inputSize + '"><input type="submit" value="百度搜索"></td></tr></table></form>').appendTo(this.rootComponent);
    	if (this.value) {
    		jQuery(this.formEl.find('input[name="word"]')).val(this.value);
    	}
		return this;
	}
});
