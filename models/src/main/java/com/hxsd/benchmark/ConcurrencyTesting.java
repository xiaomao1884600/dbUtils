package com.hxsd.benchmark;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author doubeye
 * @version 1.0.0
 * 并发测试工具
 */
@SuppressWarnings("unused")
public class ConcurrencyTesting {
	/**
	 * 日志记录
	 */
	private Vector<String> logger = new Vector<>();
	/**
	 * 学生编号列表
	 */
	private List<Integer> students = new ArrayList<>();

	/**
	 * 初始化
	 * @throws IOException IO异常
	 */
	public void init() throws IOException {
		loadStudents();
	}

	/**
	 * 运行测试
	 */
	public void runTest() {
		String userName = System.getenv().get("USERNAME");
		for (int i = 0; i < processSize; i ++) {
			TestUnit tu = new TestUnit();
			tu.setLoggers(logger);
			tu.setStudents(students);
			tu.init();
			tu.setInterval(interval);
			tu.setRunTimes(runTimes);
			tu.setUnitName(userName + "_" + i); 
			tu.start();
		}
		for (String log : logger) {
			System.out.println(log);
		}
	}


	/**
	 * 载入学生
	 */
	private void loadStudents() {
		try (BufferedReader reader = new BufferedReader(new FileReader(new File("d:\\allStudent.txt")))){
			String studentId;
			while ((studentId = reader.readLine()) != null) {
				//System.out.println(studentId);
				
				students.add(new Integer(studentId));
				//break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 线程数
	 */
	private int processSize;
	/**
	 * 运行时间
	 */
	private int runTimes;
	/**
	 * 运行间隔
	 */
	private int interval;
	public void setProcessSize(int processSize) {
		this.processSize = processSize;
	}


	public void setRunTimes(int runTimes) {
		this.runTimes = runTimes;
	}


	public void setInterval(int interval) {
		this.interval = interval;
	}

}
