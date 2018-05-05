package com.hxsd.benchmark.Tester;

		import java.sql.Connection;
		import java.util.Map;

/**
 * 数据库测试基类
 */
@SuppressWarnings("unused")
public abstract class BaseDatabaseTester implements Tester, StudentIdSetter{

	/**
	 * 运行阶段
	 */
	protected int phase;
	/**
	 * 数据库简介对象
	 */
	protected Connection conn;
	/**
	 * 时间
	 */
	private Map<String, Long> timePoint;

	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public void setTimePoint(Map<String, Long> timePoint) {
		this.timePoint = timePoint;
	}
}
