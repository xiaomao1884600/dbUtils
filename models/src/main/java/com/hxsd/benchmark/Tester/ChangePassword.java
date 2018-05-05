package com.hxsd.benchmark.Tester;

import com.doubeye.commons.database.sql.SQLExecutor;

import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.0.0
 * 修改密码测试
 */
public class ChangePassword extends BaseDatabaseTester implements Tester, StudentIdSetter{
	/**
	 * 修改密码
	 */
	private int studentId;
	/**
	 * 修改密码的语句
	 */
	private static final String sql = "UPDATE student1 SET password = '1234567878', salt = '3i4S' WHERE studentid = %d";
	@Override
	public void doTest() throws SQLException {
		SQLExecutor.execute(conn, String.format(sql, studentId));
	}
	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}
}
