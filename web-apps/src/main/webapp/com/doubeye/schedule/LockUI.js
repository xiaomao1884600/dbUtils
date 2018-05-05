/**
 * 锁定界面
 * 当主页面一定时间不动时，则锁定主界面
 * @author doubeye
 * @version 1.0.0
 * 构造函数参数
 * hiddingEl : 需要隐藏的控件，如果不指定此项，则用ExtJs.Window模态窗口的默认透明度展示
 * 
 */
com.doubeye.LockUI = function(config){
	this.config = config;
	// if (this.config.hiddingEl) {
	// 	if (typeof(this.config.hiddingEl)=="string") {
	// 		this.config.hiddingEl = jQuery('#' + this.config.hiddingEl);
	// 	} else if (this.config.hiddingEl.ctype.indexOf('Ext.') == 0) {
	// 		//this.config.hiddingEl = jQuery(this.config.hiddingEl.body ? this.config.hiddingEl.body.dom : this.config.hiddingEl.container.dom);
	// 	} else if (this.config.hiddingEl.jquery) {
	// 		
	// 	} else {
	// 		delete this.config.hiddingEl;
	// 	}
	// }
};

com.doubeye.LockUI.prototype = {
	/**
	 * 累计没有动作的时间
	 */	
	noMoveLasting : 0,
	/**
	 * 锁定标志
	 */
	locked : false,
	/**
	 * 密码组件
	 */
	passwordEl : null,
	/**
	 * 锁定窗口
	 */
	lockWindow : null,
	setTimer : function(){
		var thiz = this;
		if (this.locked) {
			return;
		}
		setTimeout(function(){
			thiz.noMoveLasting += 5;
			if (thiz.noMoveLasting > com.doubeye.GlobalParameters.noMoveLockTime) {
				thiz.showLockWindow();
				thiz.noMoveLasting = 0;
			}
			thiz.setTimer();
		}, 5000);
	},
	showLockWindow : function(){
		var id = Ext.id();
		var thiz = this;
		this.lockWindow = new Ext.Window({
			layout : 'fit',
			showLabel : true,
			closable : false,
			title : '锁定',
			html : '<div id = "' + id + '"><div>',
			modal : true,
			resizable : false,
			maximized : true
		}).show();
		this.locked = true;
		var el = jQuery('#' + id).attr('style', 'text-align: center; vertical-align: middle; padding-top: 120px;');
		jQuery('<label>', {
			html : '由于长时间没有操作，系统自动锁定，请在密码提示框中输入你的密码，点击解锁接触锁定',
			style : 'font-size:120%;'
		}).appendTo(el);
		var div = jQuery('<div>', {
			style : 'padding-top:20px;'
		}).appendTo(el);
		this.passwordEl = jQuery('<input>', {
			type : 'password'
		}).appendTo(div);
		jQuery('<input>', {
			type : 'button',
			value : '解锁'
		}).click(function(){
			thiz.unlock();
		}).appendTo(div);
	},
	unlock : function(){
		var password = this.passwordEl.val();
		if (password == 'unlock') {
			this.lockWindow.close();
			this.locked = false;
			this.setTimer();
			if (this.config.hiddingEl) {
				this.config.hiddingEl.show();
			}
		}
	}
};
