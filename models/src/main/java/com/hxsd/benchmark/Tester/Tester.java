package com.hxsd.benchmark.Tester;


/**
 * 测试接口
 * @author doubeye
 * @version 1.0.0
 */
public interface Tester extends StudentIdSetter{
	/** 运行测试
	 * @throws Exception 异常
	 */
	void doTest() throws Exception;
}
