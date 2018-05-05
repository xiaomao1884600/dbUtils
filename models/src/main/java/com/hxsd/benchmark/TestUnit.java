package com.hxsd.benchmark;

import com.hxsd.benchmark.Tester.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * @author doubeye
 * @version 1.0.0
 * 测试单元
 */
public class TestUnit extends Thread{
	/**
	 * 所有要运行的测试，在列表中的测试类将在一次测试中顺序运行
	 */
	private List<BaseDatabaseTester> allTesters = new ArrayList<>();
	/**
	 * 学生列表，列表中保存所有的学生id
	 */
	private List<Integer> students;
	/**
	 * 日志内容
	 */
	private Vector<String> loggers;
	/**
	 * 测试名称
	 */
	private String unitName;
	/**
	 * 运行时间
	 */
	private int runTimes;
	/**
	 * 运行间隔
	 */
	private int interval;
	@SuppressWarnings("WeakerAccess")
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	@SuppressWarnings("WeakerAccess")
	public void setRunTimes(int runTimes) {
		this.runTimes = runTimes;
	}
	
	public void setInterval(int interval) {
		this.interval = interval;
	}

	/**
	 * 初始化
	 */
	public void init() {
		allTesters.add(new StudentInfoGetter());
		allTesters.add(new Login());
		allTesters.add(new ChangePassword());
		allTesters.add(new SignIn());
	}
	public void setStudents(List<Integer> students) {
		this.students = students;
	}
	@SuppressWarnings("WeakerAccess")
	public void setLoggers(Vector<String> loggers) {
		this.loggers = loggers;
	}

	/**
	 * 运行测试
	 * @return 运行结果
	 */
	@SuppressWarnings("WeakerAccess")
	public String runTest(){
		StringBuilder logger = new StringBuilder();
		logger.append(" ").append(unitName).append(" ");

		int studentId = getRandomStudentId();
		for (BaseDatabaseTester tester : allTesters) {
			tester.setStudentId(studentId);
			tester.setConn(connection);
			logger.append(tester.getClass().getSimpleName()).append(" ").append(System.currentTimeMillis());
			try {
				tester.doTest();
				logger.append(" success ").append(" ").append(System.currentTimeMillis());
			} catch (Exception e) {
				e.printStackTrace();
				logger.append(" ").append(e.getMessage()).append(" ").append(System.currentTimeMillis());
			}
		}
		return logger.toString();
	}
	
	@Override
	public void run() {
		for (int i = 0; i < runTimes; i ++) {
			String log = runTest();
			System.out.println(i + "   " + log);
			loggers.add(log);
			try {
				Thread.sleep(new Random().nextInt(interval));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 随机获得一个学生编号
	 * @return 随机的学生编号
	 */
	private int getRandomStudentId() {
		Random random = new Random();
		return students.get(random.nextInt(students.size()));
	}

	/**
	 * 数据库连接对象
	 */
	private Connection connection;
	public void setConnection(Connection conn) {
		this.connection = conn;
	}
}
