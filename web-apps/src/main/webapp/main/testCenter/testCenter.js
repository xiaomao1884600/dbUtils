jQuery(document).ready(function() {
	$(window).resize(function() {
		$('#container').height($(window).height() - $('#functionList').outerHeight() - 1 - jQuery('#foot').outerHeight() - 1 - jQuery('#buttons').outerHeight() - 1 - parseInt(jQuery('#container').css('padding-top'), 10));
	}).resize();
	jQuery('a').click(function(event) {
		var container = jQuery('#container');
		var className = jQuery(this).attr("className");
		if (!className) {
			return;
		}
		var configAttr = {};
		if (jQuery(this).attr('config')) {
			configAttr = com.doubeye.Ext.decode(jQuery(this).attr('config'));
		}
		var functionComp = com.doubeye.Utils.getClassInstance(className, com.doubeye.Ext.apply(configAttr, {
			parent : 'container'
		}));
		functionComp.init();
		if (functionComp.render) {
			functionComp.render();
		}
	});
	var container = jQuery('#container');
	jQuery('#jQueryLayout').click(function() {
		//jQuery('body').width('100%').height('100%');

		container.addClass('ui-layout-container');
		//container.width('100%').height('200');
		jQuery('<div class="ui-layout-north">north</div>').appendTo(container);
		jQuery('<div class="ui-layout-south">south</div>').appendTo(container);

		//jQuery('<div class="ui-layout-east">east</div>').appendTo(container);
		//jQuery('<div class="ui-layout-west">west</div>').appendTo(container);
		jQuery('<div class="ui-layout-center">center</div>').appendTo(container);

		var outer = container.layout();
		container.size("auto");
		outer.open('north');
		outer.sizePane('north', 'auto');
	});
	jQuery('#colorPicker').click(function() {
		var input = jQuery('<input>').appendTo(container);
		var cpEl = jQuery('<div>', {
			id : 'colorSelector2',
			style : 'background: url(../../lib/jQuery/colorPicker/images/select2.png) center;'
		}).appendTo(container);
		cpEl.ColorPicker({
			color : '#00ffff',
			onShow : function(colpkr) {
				$(colpkr).fadeIn(500);
				return false;
			},
			onHide : function(colpkr) {
				$(colpkr).fadeOut(500);
				return false;
			},
			onChange : function(hsb, hex, rgb) {
				cpEl.css('backgroundColor', '#' + hex);
			}
		}).width(input.height()).height(input.height());
	});
	jQuery('#jsTreeTest').click(function() {
		jQuery('#container').css('overflow', 'scroll').jstree({
			json_data : {
				ajax : {
					url : '/redeemer/ServletUtilsTest?action=getAllDataBaseUser'
				}
			},
			"progressive_render" : true,
			plugins : ["themes", "json_data", "ui"],
			themes : {
				theme : "apple",
				dots : false,
				icons : false
			},
			callback : {
				onopen : function(node, tree_obj) {
					alert('')
					if (tree_obj.children(node).length == 0) {
						$.getJSON('getChildren.do', {
							'id' : node.id
						}, function(data) {
							$.each(data, function(entryIndex, entry) {
								tree_obj.create(entry, node, entryIndex + 1);
							});
						});
					}
					return true;
				}
			}
		}).bind('select_node.jstree', function(e, data) {
			alert(data.inst.get_path().length);
		});
	});
	//tTree
	jQuery('#tTreeTest').click(function() {
		//此样式为必须样式，否则树状样式无法显示
		jQuery("#container").addClass('ztree');
		jQuery.fn.zTree.init(jQuery("#container"), {
			view : {
				selectedMulti : true
			},
			async : {
				enable : true,
				url : "/redeemer/ServletUtilsTest",
				autoParam : ["id"],
				otherParam : {
					'action' : 'getAllDataBaseUserZTree',
					'ddd' : '11'
				},
				dataFilter : function(treeId, parentNode, datas) {
					for (var i = 0; i < datas.length; i++) {
						datas[i].isParent = true;
					}
					return datas;
				}
			}
		});
	});
	
	//imageDropdown
	jQuery('#imageDropdown').click(function() {
		jQuery( "div.panel" ).resizable({
			handles:"all",            //定义可变化大小的方向,可选值"n, e, s, w, ne, se, sw, nw","all"代表全部
			helper:"proxy",
			//aspectRatio: true,        //按照原有的比例缩放 默认是false
			autoHide: true,          //是否自动隐藏变化控制器
			transparent: false,
			//grid: [10, 10],            //定义x,y轴两个方向的变化步进,单位px
			//animate: true,           //定义延迟是否变化大小
			//animateDuration: "slow", //变化速度
			//animateEasing: "swing",
			ghost: true,             //是否显示变化
			start:function(e,ui){$(this).append("Start!");},  //定义开始变化大小时执行的函数
			resize:function(e,ui){},                          //定义在变化大小时执行的函数
			stop:function(e,ui){$(this).append("Stop!");}    //与start相反
		}).draggable({
			handle: "h3"
		});
		return;
		var jsonData = [                    
                {description:'Choos your payment gateway', value:'', text:'Payment Gateway'},                    
                {image:'../images/msdropdown/icons/Amex-56.png', description:'My life. My card...', value:'amex', text:'Amex'},
                {image:'../images/msdropdown/icons/Discover-56.png', description:'It pays to Discover...', value:'Discover', text:'Discover'},
                {image:'../images/msdropdown/icons/Mastercard-56.png', title:'For everything else...', description:'For everything else...', value:'Mastercard', text:'Mastercard'},
                {image:'../images/msdropdown/icons/Cash-56.png', description:'Sorry not available...', value:'cash', text:'Cash on devlivery', disabled:true},
                {image:'../images/msdropdown/icons/Visa-56.png', description:'All you need...', value:'Visa', text:'Visa'},
                {image:'../images/msdropdown/icons/Paypal-56.png', description:'Pay and get paid...', value:'Paypal', text:'Paypal'}
                ];

		var oDropdown = $("#container").msDropDown({byJson:{data:jsonData, name:'payments'}}).data("dd");
	});
}); 