/**
 * Ajax工具 
 * Server-Client 数据交换规范
 * 改工具内与服务端的交换采用JSON，返回信息如下
 * 1. 如果服务端正常完成数据，则SUCCESS属性为true，同时MESSAGE属性记录完成操作的信息，如MESSAGE无内容，则显示默认信息
 * 	a) RESULTS属性内会返回所有数据，这些数据需由调用者进行解析
 * 2. 如果发生错误，ERROR属性为true，同时MESSAGE属性记录完成操作的信息，如MESSAGE无内容，则显示默认信息，同时可能会返回extendedErrorMessage对象，该对象为扩展错误信息
 * 3. REDIRECT 如果后台操作非常耗时，则采用重定向的方式进行异步处理，此时客户端可进行别的操作，在调用时进行恢复，此时客户端会启动定时更新的机制，参见com.doubeye.RedirectedAjax
 * 4. 后台没有捕捉到异常情况，此时会在Ajax的failure函数
 * 5. FILE 后台需要发送文件形式给前台，此时属性中的URL为文件下载地址
 * 6. LOGIN 后台无认证信息，需重新登录，优先级最高
 * 
 * 
 * config
 * url : 请求的url
 * params ：url的参数
 * processCaller : {Object} processResult函数的作用对象
 * processResult : {function(data)} 处理结果集的回调函数
 * successDialog : {} 处理成功信息的对话框类，如果不指定，则不调用这个结果，比较适用于取得查询结果的调用或多步调用
 * errorDialog : {} 处理错误信息的类
 * successMessage : {String} 成功信息，如果不指定，则显示com.doubeye.message.success
 * errorMessage : {String} 失败信息 ，如果不指定，则显示com.doubeye.message.error
 * async : {Boolean} 是否为异步请求，默认为false，即为同步请求
 * cache : {Boolean} 是否使用缓存，默认为false
 * waitingDialogName : {String} 请求完成之前使用的等待对话框类，默认为'com.doubeye.WaitingDialog',
 * noWaitingDialog : {Boolean} 不显示等待对话框，默认为false，即默认显示等待对话框
 * showNoSuccessMessage : {Boolean} 当请求成功返回时，不显示成功信息，用户后台获取数据时使用，默认为false
 */
com.doubeye.Ajax = function(config) {
	com.doubeye.Ext.apply(this, config);
	this.url = com.doubeye.Utils.toFullPath(this.url);
	this.manager = config.manager;
};
com.doubeye.Ajax.prototype = {
	__waitingDialog : null,
	__successDialog : null,
	__errorDialog : null,
	/**
	 * TODO 编写AjaxManager
	 * 获得ajax对象，配合com.doubeye.AjaxManager使用 
	 */
	__ajaxObj : this,
	/**
	 * 用来进行重定向时使用 
	 */
	//__redirectedAjax : null,
	async: false,
  	cache : false,
  	showNoSuccessMessage : false,
  	waitingDialogName : 'com.doubeye.WaitingDialog',
  	noWaitingDialog : false,
  	successMessage : com.doubeye.message.successMessage,
  	errorMessage : com.doubeye.message.errorMessage,
	sendRequest : function(){
		var thiz = this;
		var config = this.__getAjaxConfig();
		if (!this.noWaitingDialog) {
			try{
				this.__waitingDialog = com.doubeye.Utils.getClassInstance(this.waitingDialogName, {}).init().render();
			} catch (e) {
				this.__waitingDialog = new com.doubeye.WaitingDialog({}).init().render();
			} 
		}
		var response = jQuery.ajax(config).responseText;
	},
	getSuccessDialog : function() {
		if (!this.__successDialog) {
			this.__successDialog = com.doubeye.Utils.getClassInstance('com.doubeye.BubbleDialog', {}).init();
		}
		return this.__successDialog;
	},
	getErrorDialog : function() {
		if (!this.__errorDialog) {
			this.__errorDialog = com.doubeye.Utils.getClassInstance('com.doubeye.NormalDialog', {}).init();
		}
		return this.__errorDialog;
	},
	processServerSuccess : function(data) {
		//有结果集,此时不显示提示
		if (com.doubeye.Ext.isFunction(this.processResult)) {
			this.processResult.call(this.processCaller, data.RESULTS ? com.doubeye.Ext.decode(data.RESULTS) : data);
		} else if (this.showNoSuccessMessage){ //不显示提示直接返回
			return;
		} else if (data.MESSAGE) { //后台返回提示优先于前天的提示
			this.getSuccessDialog().setMessage(data.MESSAGE).render();
		} else { //前台的提示或者是默认提示
			this.getSuccessDialog().setMessage(this.successMessage).render(); 
		}
	},
	processServerError : function(data) {
		if (data.STACK) {
			var config = {
				message : data.MESSAGE, 
				stack : data.STACK
			};
			var dialog = new com.doubeye.AdvenceErrorDialog(config).init().render();
		} else if (data.MESSAGE) {
			this.getErrorDialog().setMessage(data.MESSAGE).render();
		} else {
			this.getErrorDialog().setMessage(this.errorMessage).render();
		}
	},
	__getAjaxConfig : function(url){
		var thiz = this;
		var config = {
			url : url ? url : this.url,
			data : this.params,
			async : this.async,
			cache : this.cache,
			success : function(data, textStatus){
				var originAjax = thiz.originAjax ? thiz.originAjax : thiz;
				if (thiz.manager) {
					thiz.manager.finish(originAjax);
				}
				//data = com.doubeye.Ext.decode(data);
				//data = com.doubeye.Ext.decode(data);
				if (!thiz.noWaitingDialog) {
					thiz.__waitingDialog.closeDialog(true);
				}
				//正常返回
				if (data.LOGIN) {
					window.location = data.LOGIN;
				} else if(data.SUCCESS) {
					thiz.processServerSuccess(data);
				} else if (data.ERROR) { //错误返回，此时应该为
					thiz.processServerError(data);
				} else if (data.REDIRECT){ //转向两外一个url
					var con = thiz;
					con.interval = 5000;
					con.showNoWaitingDialog = true;
					con.url = data.URL;
					con.originAjax = thiz.getOriginAjax();
					delete thiz.originAjax;
					var newAjax = new com.doubeye.RedirectedAjax(con)
					setTimeout(function(){
						newAjax.sendRequest();
					}, 5000);
				} else if (data.FILE) {
					window.open(data.URL);
				}
			},
			error : function(request, status, e){
				var originAjax = thiz.originAjax ? thiz.originAjax : thiz;
				if (thiz.manager) {
					thiz.manager.finish(originAjax);
				}
				if (!thiz.noWaitingDialog) {
					thiz.__waitingDialog.closeDialog(true);
				}
				var config = {
					message : '状态代码' + status,
					stack : 'request:' + com.doubeye.Ext.encode(request) + ' error:' + e
				};
				var dialog = new com.doubeye.AdvenceErrorDialog(config).init().render();
			}
		};
		return config;
	},
	getOriginAjax : function() {
		var parent = this;
		var i = 0;
		while (parent.originAjax) {
			if (i > 100) {
				throw "bad while";
			}
			parent = parent.originAjax;
			i ++;
		}
		return parent;
	}
};


