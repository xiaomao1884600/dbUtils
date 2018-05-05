jQuery(document).ready(function() {
	test_extend_collapsable();
});

test_extend_collapsable = function() {
	setUp();
	//collapable_without_selector();
	collapable_with_selector();
};

var setUp = function() {
	
	var htmlContent = '<div id = "version">我是标题' +
				'<p>全文检索功能模块测试版本    <font color="red">版本：alpha0.1</font></p>' +
				'<p>主要测试全文检索引擎(基于Oracle Text)的效率和功能，以及对不同文件索引建立和查询的效果</p>' +
				'<p>主要完成了以下内容：</p>' +
				'<ol class="content">' +
					'<li>对文档内信息的提取（Oracel Text自动完成）</li>' +
					'<li>索引生成</li>' +
					'<li>对关键字检索</li>' +
					'<li>' +
						'根据文档种类使用相应的打开策略：' +
						'<ol class="openPolicy">' +
							'<li>pdf使用PDF阅读器打开</li>' +
							'<li>在IE下，用内嵌的Office打开Word，Excel，txt和csv文档，在火狐下提供这些文档的下载</li>' +
							'<li>其他类型文件提供下载</li>' +
						'</ol>' +
					'</li>	' +
				'</ol>' +
	'</div>';
	jQuery(htmlContent).appendTo('#container');
};

var collapable_without_selector = function() {
	jQuery("#version").collapsable(
		//{collapsed : true}
	);
};

var collapable_with_selector = function() {
	jQuery("#version").collapsable({
		childSelector : ".content"
		,collapsed : true
	});
};
