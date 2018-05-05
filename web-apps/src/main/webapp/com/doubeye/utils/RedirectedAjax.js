/**
 * config
 * 
 * interval 向后台进行查询的间隔，单位毫秒，默认为3000
 * 
 * Server-Client 数据交换规范--redirect扩展
 * PERCENT : 后台进程完成的百分比
 * FINISHED : 后台进程是否完成
 *  
 */
com.doubeye.RedirectedAjax = com.doubeye.Ext.extend(com.doubeye.Ajax, {
	interval : 3000,
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
				data = com.doubeye.Ext.decode(data);
				if (!thiz.noWaitingDialog) {
					thiz.__waitingDialog.closeDialog(true);
				}
				//正常返回
				if (data.SUCCESS) {
					thiz.processServerSuccess(data);
				} else if (data.ERROR) { //错误返回，此时应该为
					thiz.processServerError(data);
				} else if (data.FINISHED != "true"){ //如果没有结束，则在指定时间后再次发起请求
					var config = thiz.__getAjaxConfig(thiz.url);
					setTimeout(function(){
						jQuery.ajax(config);
					}, thiz.interval);
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
	}
});
