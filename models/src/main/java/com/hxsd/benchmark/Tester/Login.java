package com.hxsd.benchmark.Tester;

import com.doubeye.commons.database.sql.SQLExecutor;

import java.sql.SQLException;

public class Login extends BaseDatabaseTester implements Tester, StudentIdSetter{
	/**
	 * ѧ�����
	 */
	private int studentId;
	private static final String sql = "UPDATE student1 SET lastdateline = unix_timestamp(now()) WHERE studentid = %d";
	@Override
	public void doTest() throws SQLException {
		 SQLExecutor.execute(conn, String.format(sql, studentId));
	}
	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}
}
