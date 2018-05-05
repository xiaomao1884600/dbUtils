package com.doubeye.commons.database.sql;

import com.doubeye.commons.database.connection.ConnectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.*;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @author doubeye
 * @version 1.2.0
 * SQL语句执行器
 */
public class SQLExecutor {
	private static Logger logger = LogManager.getLogger(SQLExecutor.class);
	/**
	 * 执行非返回性语句
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @throws SQLException SQL异常
     */
	public static void execute(Connection conn, String sql) throws SQLException {
		try (Statement st = conn.createStatement()){
			log(sql);
			st.execute(sql);
		} catch (SQLException e) {
            printConnection(System.err, conn);
            throw e;
        }
	}

	/**
	 * 执行PreparedStatement TODO 增加对Map的支持
	 * @param conn 数据库连接对象
	 * @param sqlTemplate SQL模板
	 * @param bean 参数对象
	 * @throws SQLException SQL异常
	 */
	public static void execute(Connection conn, String sqlTemplate, Object bean) throws SQLException {
		List<String> namedParameters = new ArrayList<>();
		String sql = getSQLFromTemplate(sqlTemplate, namedParameters);
		try (PreparedStatement ps = conn.prepareStatement(sql)){
			setParameterInPreparedStatement(ps, namedParameters, bean);
			ps.execute();
		}
	}
	/**
	 * 执行PreparedStatement的查询语句 TODO 增加对Map的支持
	 * @param conn 数据库连接对象
	 * @param sqlTemplate SQL模板
	 * @param bean 参数对象
	 * @throws SQLException SQL异常
	 */
	public static ResultSet executeQuery(Connection conn, String sqlTemplate, Object bean) throws SQLException {
		List<String> namedParameters = new ArrayList<>();
		log(sqlTemplate);
		String sql = getSQLFromTemplate(sqlTemplate, namedParameters);
		PreparedStatement ps = conn.prepareStatement(sql);
		setParameterInPreparedStatement(ps, namedParameters, bean);
		return ps.executeQuery();

	}

	/**
	 * 为PreparedStatement设置参数
	 * @param ps PreparedStatement
	 * @param namedParameters 命名的参数List
	 * @param bean 参数对象
	 * @throws SQLException SQL异常
	 */
	public static void setParameterInPreparedStatement(PreparedStatement ps, List<String> namedParameters, Object bean) throws SQLException {
		Class beanClass = bean.getClass();
		for (int i = 1; i <= namedParameters.size(); i ++) {
			try {
				setNamedParameter(ps, i, namedParameters.get(i - 1), bean, beanClass);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				logger.warn("在给定对象实例中没有找到" + namedParameters.get(i - 1) + "的定义");
				e.printStackTrace();
			}
		}
	}

	/**
	 * TODO 需要测试多个条件引用同一个命名参数
	 * 根据命名的SQL模板和参数列表生成标准的PreparedStatement
	 * @param sqlTemplate 命名的SQL模板
	 * @param parameters 参数List
	 * @return 文号形式的SQL模板
	 */
	public static String getSQLFromTemplate(final String sqlTemplate, List<String> parameters) {
		String result = sqlTemplate;
		int i = 0;
		while (result.indexOf(":") > 0) {
			int colonPosition = result.indexOf(":");
			int spacePosition = result.indexOf(" ", colonPosition);
			if (spacePosition < 0) {
				spacePosition = result.length();
			}
			String parameterName = result.substring(colonPosition + 1, spacePosition).replace(",", "");
			parameters.add(parameterName);
			result = result.replace((":" + parameterName), "?");
			i ++;
			if (i > 1000) {
				throw new RuntimeException("错误的while循环，SQL:" + sqlTemplate);
			}
		}
		return result;
	}

	/**
	 * 设置参数
	 * @param ps PreparedStatement
	 * @param index 参数索引
	 * @param parameterName 参数名称
	 * @param instance 值对象
	 * @param instanceClass 值对象的类
	 * @throws SQLException SQL异常
	 */
	private static void setNamedParameter(PreparedStatement ps, int index, String parameterName, Object instance, Class instanceClass) throws NoSuchFieldException, SQLException, IllegalAccessException {
		//log(parameterName);
		Field field =  instanceClass.getDeclaredField(parameterName);
		field.setAccessible(true);
		//空值判断
		if (field.get(instance) == null) {
			ps.setNull(index, 0);
		} else {
			ps.setObject(index, field.get(instance));
		}
	}

	/**
	 * 执行返回结果集的语句
	 * @param conn 数据库连接
	 * @param sql SQL语句
	 * @return 语句的结果集
	 * @throws SQLException SQL异常
     */

	public static ResultSet executeQuery(Connection conn, String sql) throws SQLException {
		log(sql);
		Statement st = conn.createStatement();
        try {
            return st.executeQuery(sql);
        } catch (SQLException e) {
            printConnection(System.err, conn);
            throw e;
        }
	}

	/**
	 * 返回执行语句的唯一的int类型值
	 * @param conn 数据库连接对象
	 * @param sql SQL语句
	 * @return 执行结果中的唯一的int值
	 * @throws SQLException SQL异常
	 */
	private static int getOneIntegerFromSQL(Connection conn, String sql) throws SQLException {
		log(sql);
		try (
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(sql) ){
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				throw new RuntimeException(String.format(ERROR_MESSAGE_RETURN_MORE_THAN_ONE_ROW, sql));
			}
		} catch (SQLException e) {
			printConnection(System.err, conn);
			throw e;
		}
	}

	/**
	 * 批量执行语句，语句用;分隔
	 * @param conn 数据库连接对象
	 * @param inputStream 包含语句的流对象
	 * @param encoding 流对象的字符集
	 * @throws IOException IO异常
	 * @throws SQLException SQL异常
	 */
	public static void executeBunchSQLs(Connection conn, InputStream inputStream, String encoding) throws IOException, SQLException {

		try (InputStreamReader reader = new InputStreamReader(inputStream, encoding)) {
			executeBunchSQLs(conn, reader);
		}
	}

	/**
	 * 批量执行语句，语句用;分隔
	 * @param conn 数据库连接对象
	 * @param sqlFileName 包含语句的文件名
	 * @param encoding 流对象的字符集
	 * @throws IOException IO异常
	 * @throws SQLException SQL异常
	 */
	@SuppressWarnings("unused")
	public static void executeBunchSQLs(Connection conn, File sqlFileName, String encoding) throws IOException, SQLException {
		try (InputStreamReader reader = new InputStreamReader(new FileInputStream(sqlFileName), encoding)) {
			executeBunchSQLs(conn, reader);
		}
	}
	/**
	 * 批量执行语句，语句用;分隔
	 * @param conn 数据库连接对象
	 * @param reader reader
	 * @throws IOException IO异常
	 * @throws SQLException SQL异常
	 */
	private static void executeBunchSQLs(Connection conn, InputStreamReader reader) throws IOException, SQLException {
		BufferedReader bufferedReader = new BufferedReader(reader);
		List<String> sqls = new ArrayList<>();
		String line;
		StringBuilder sql = new StringBuilder();
		while ((line = bufferedReader.readLine()) != null) {
			if (line.trim().endsWith(";")) {
				//sql += line.substring(0, line.lastIndexOf(";"));
				sql.append(line.substring(0, line.lastIndexOf(";")));
				sqls.add(sql.toString());
				sql = new StringBuilder();
			} else {
				sql.append(line);
			}
		}
		executeBunchSQLs(conn, sqls);
	}

	/**
	 * 批量执行语句
	 * @param conn 数据库连接对象
	 * @param sqls 包含SQL语句的List
	 * @throws SQLException SQL异常
	 */
	private static void executeBunchSQLs(Connection conn, List<String> sqls) throws SQLException {
		for (String sql : sqls) {
			execute(conn, sql);
		}
	}

	/**
	 * 打印连接信息
	 * @param err PrintStream对象
	 * @param conn 数据库连接对象
	 * @throws SQLException SQL异常
	 */
	private static void printConnection(PrintStream err, Connection conn) throws SQLException {
        err.println("Connection Driver Name = " + conn.getMetaData().getDriverName());
        err.println("URL = " + conn.getMetaData().getURL());
    }

	/**
	 * 获得最后插入的id TODO 此方法为MySQL专用
	 * @param conn 数据库连接对象
	 * @return 最后插入的id
	 * @throws SQLException SQL异常
	 */
	public static int getLastInsertId(Connection conn) throws SQLException {
		return getOneIntegerFromSQL(conn, SQL_SELECT_LAST_INSERT_ID);
	}

	/**
	 * 获得最后插入的编号，TODO 此方法为MySQL专用
	 */
	private static final String SQL_SELECT_LAST_INSERT_ID = "SELECT LAST_INSERT_ID()";
	/**
	 * 单行语句返回对于一条数据的错误信息
	 */
	private static final String ERROR_MESSAGE_RETURN_MORE_THAN_ONE_ROW = "sql语句返回多于一行的数据.[%s]";

	private static void log(String message) {
		//System.out.println(message);
		logger.debug(message);
	}
}
