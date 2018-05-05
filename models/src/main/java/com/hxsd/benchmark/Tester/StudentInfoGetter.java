package com.hxsd.benchmark.Tester;

import com.doubeye.commons.database.sql.SQLExecutor;

import java.sql.SQLException;

/**
 * @author doubeye
 * @version 1.0.0
 * 获得学生信息测试
 */
public class StudentInfoGetter extends BaseDatabaseTester implements Tester, StudentIdSetter{
	/**
	 * 学生编号
	 */
	private int studentId;
	/**
	 * 获得学生信息的编号
	 */
	private static final String sql = "SELECT * FROM student1 WHERE studentid = %d";
	@Override
	public void doTest() throws SQLException {
		
		 SQLExecutor.execute(conn, String.format(sql, studentId));
	}
	public void setStudentId(int studentId) {
		this.studentId = studentId;
	}
}
