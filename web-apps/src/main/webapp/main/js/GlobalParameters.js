/**
 * 定义com,com.doubeye全局名称
 */

com = {};
com.doubeye = {};
com.doubeye.Ajax = {};
/**
 * 项目级全局变量，主要用途为保存项目级别全局变量，兼容性变量
 * 此文件需要在html文件中加载，并需要优先于jsLoader和cssLoader加载
 *  
 */
com.doubeye.GlobalParameters = {
	/**
	 * jsLoader.js文件中的url主要相对于/main/index.html文件而言，如果需要从其他地方加载jsLoader中的文件，需要改变loaderPrefix的值
	 */
	loaderPrefix : '',
	/**
	 * 在页面中没有操作时将锁定页面，此参数设置了锁定的时间间隔，单位为秒，如果指定为0或者不指定，则表示无锁定操作
	 */
	noMoveLockTime : 10,
	/**
	 * 浏览器兼容性 
	 */
	explorerCompatibility : {
		/**
		 * IE低小版本 
		 */
		minIEVersion : 6,
		/**
		 * 火狐最低版本 
		 */
		minFireFoxVersion : 3
	}
};
