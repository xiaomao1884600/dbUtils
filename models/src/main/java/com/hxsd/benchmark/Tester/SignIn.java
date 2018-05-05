package com.hxsd.benchmark.Tester;

import com.doubeye.commons.database.sql.SQLExecutor;

import java.sql.SQLException;

/**
 * 登录测试
 */
public class SignIn extends BaseDatabaseTester implements Tester, StudentIdSetter{
	/**
	 * 学生id
	 */
	private int studentId;
	/**
	 * 登录语句
	 */
	private static final String sql = "INSERT INTO attendance(studentid, attendancedate, timerange, signin, signout, dateline, replaced) VALUES(%d, now(), 0, unix_timestamp(now()), unix_timestamp(now()), unix_timestamp(now()), 0)";
	@Override
	public void doTest() throws SQLException {
		 SQLExecutor.execute(conn, String.format(sql, studentId));
	}
	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}
}