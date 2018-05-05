jQuery(document).ready(function() {
	test_Menu();
});


test_Menu = function() {
		jQuery('<input>', {
		value : 'test_Menu',
		type : 'button'
	}).click(function(e) {
		var items = [{
			name : 'add',
			text : 'add',
			action : function(){
				alert('add');
			}
		}, {
			name : 'edit',
			text : 'edit',
			action : function(){
				alert('edit');
			}
		}];
		var cmp = new com.doubeye.PopMenu({
			parent : 'container',
			menuItems : items,
			width : 50
		});
		cmp.init().render().show(e.clientX, e.clientY);
		alert('add');
		cmp.addMenuItem({
			name : 'test',
			text : 'test',
			action : function(){
				alert('test');
			}
		});
		alert('hide test');
		cmp.hideMenuItem('test');
		alert('show test');
		cmp.showMenuItem('test');
		alert('disable test');
		cmp.disableMenuItem('test');
		//alert('enable test');
		//cmp.enableMenuItem('test');
	}).appendTo('#container');
};