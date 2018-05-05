package com.doubeye.commons.test.DifferentiationTeller;

import com.doubeye.commons.application.GlobalApplicationContext;
import com.doubeye.commons.database.ResultSet.DiffrentationTeller.ResultSetDifferentiationTellerSingle;
import com.doubeye.commons.database.sql.SQLExecutor;
import com.doubeye.commons.utils.test.ApplicationContextInitiator;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by doubeye(doubeye@sina.com) on 2017/3/17.
 */
public class DifferentiationTellerTest extends TestCase{
    private ResultSetDifferentiationTellerSingle differentiationTeller = new ResultSetDifferentiationTellerSingle();

    private void createTable() throws IOException, SQLException {
        try (Connection conn = getConnection()) {
            try (InputStream sqlInputStream = this.getClass().getClassLoader().getResourceAsStream("DifferentiationTeller/init.sql")) {
                SQLExecutor.executeBunchSQLs(conn, sqlInputStream, "utf-8");
            }
        }
    }


    private void clean() throws IOException, SQLException {
        try (Connection conn = getConnection()) {
            try (InputStream sqlInputStream = this.getClass().getClassLoader().getResourceAsStream("DifferentiationTeller/clean.sql")) {
                SQLExecutor.executeBunchSQLs(conn, sqlInputStream, "utf-8");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void testEqual() throws IOException, SQLException {
        /*
        JSONObject config = JSONUtils.getJsonObjectFromInputStream(this.getClass().getClassLoader().getResourceAsStream("config/init.json"));
        GlobalApplicationContext.init(config);
        */
        /*
        ApplicationContextInitiator.init();
        conn = GlobalApplicationContext.getInstance().getCoreConnection();
        */
        DifferentiationTellerTest test = new DifferentiationTellerTest();
        try (Connection conn = getConnection()) {
            test.createTable();
            loadData("equal");
            doCompare(conn, differentiationTeller);
            assertEquals("equal Data Set both key list size", 11, differentiationTeller.getBothKeys().size());
            assertEquals("equal Data Set identical key list size", 11, differentiationTeller.getIdenticalKeys().size());
            assertEquals("equal Data Set only in Data set 1 list size", 0, differentiationTeller.getKeyOnlyInArray1().size());
            assertEquals("equal Data Set only in Data set 2 list size", 0, differentiationTeller.getKeyOnlyInArray2().size());
            assertEquals("equal Data Set different value key list size", 0, differentiationTeller.getDiffs().size());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            test.clean();
        }
    }

    public void testDataSet1HasMore() throws IOException, SQLException {

        /*
        JSONObject config = JSONUtils.getJsonObjectFromInputStream(this.getClass().getClassLoader().getResourceAsStream("config/init.json"));
        GlobalApplicationContext.init(config);
        */
        /*
        conn = GlobalApplicationContext.getInstance().getCoreConnection();
        */
        DifferentiationTellerTest test = new DifferentiationTellerTest();
        try (Connection conn = getConnection()) {
            test.createTable();
            loadData("dataSet1HasMore");
            doCompare(conn, differentiationTeller);
            assertEquals("equal Data Set both key list size", 9, differentiationTeller.getBothKeys().size());
            assertEquals("equal Data Set identical key list size", 9, differentiationTeller.getIdenticalKeys().size());
            assertEquals("equal Data Set only in Data set 1 list size", 2, differentiationTeller.getKeyOnlyInArray1().size());
            assertEquals("equal Data Set only in Data set 2 list size", 0, differentiationTeller.getKeyOnlyInArray2().size());
            assertEquals("equal Data Set different value key list size", 0, differentiationTeller.getDiffs().size());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            test.clean();
        }
    }

    public void testDataSet2HasMore() throws IOException, SQLException {
        /*
        JSONObject config = JSONUtils.getJsonObjectFromInputStream(this.getClass().getClassLoader().getResourceAsStream("config/init.json"));
        GlobalApplicationContext.init(config);
        conn = GlobalApplicationContext.getInstance().getCoreConnection();
        */
        DifferentiationTellerTest test = new DifferentiationTellerTest();
        try (Connection conn = getConnection()) {
            test.createTable();
            loadData("dataSet2HasMore");
            doCompare(conn, differentiationTeller);
            assertEquals("equal Data Set both key list size", 9, differentiationTeller.getBothKeys().size());
            assertEquals("equal Data Set identical key list size", 9, differentiationTeller.getIdenticalKeys().size());
            assertEquals("equal Data Set only in Data set 1 list size", 0, differentiationTeller.getKeyOnlyInArray1().size());
            assertEquals("equal Data Set only in Data set 2 list size", 2, differentiationTeller.getKeyOnlyInArray2().size());
            assertEquals("equal Data Set different value key list size", 0, differentiationTeller.getDiffs().size());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            test.clean();
        }
    }

    public void test2KeyNotEqual() throws IOException, SQLException {

        /*
        JSONObject config = JSONUtils.getJsonObjectFromInputStream(this.getClass().getClassLoader().getResourceAsStream("config/init.json"));
        GlobalApplicationContext.init(config);
        conn = GlobalApplicationContext.getInstance().getCoreConnection();
        */
        DifferentiationTellerTest test = new DifferentiationTellerTest();
        try (Connection conn = getConnection()){
            test.createTable();
            loadData("2KeyNotEqual");
            doCompare(conn, differentiationTeller);
            assertEquals("equal Data Set both key list size", 11, differentiationTeller.getBothKeys().size());
            assertEquals("equal Data Set identical key list size", 9, differentiationTeller.getIdenticalKeys().size());
            assertEquals("equal Data Set only in Data set 1 list size", 0, differentiationTeller.getKeyOnlyInArray1().size());
            assertEquals("equal Data Set only in Data set 2 list size", 0, differentiationTeller.getKeyOnlyInArray2().size());
            assertEquals("equal Data Set different value key list size", 2, differentiationTeller.getDiffs().size());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            test.clean();
        }
    }

    public void testMixedCondition() throws IOException, SQLException {
        /*
        JSONObject config = JSONUtils.getJsonObjectFromInputStream(this.getClass().getClassLoader().getResourceAsStream("config/init.json"));
        GlobalApplicationContext.init(config);
        conn = GlobalApplicationContext.getInstance().getCoreConnection();
        */
        DifferentiationTellerTest test = new DifferentiationTellerTest();
        try (Connection conn = getConnection()){
            test.createTable();
            loadData("mixedCondition");
            doCompare(conn, differentiationTeller);
            assertEquals("equal Data Set both key list size", 9, differentiationTeller.getBothKeys().size());
            assertEquals("equal Data Set identical key list size", 7, differentiationTeller.getIdenticalKeys().size());
            assertEquals("equal Data Set only in Data set 1 list size", 1, differentiationTeller.getKeyOnlyInArray1().size());
            assertEquals("equal Data Set only in Data set 2 list size", 1, differentiationTeller.getKeyOnlyInArray2().size());
            assertEquals("equal Data Set different value key list size", 2, differentiationTeller.getDiffs().size());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            test.clean();
        }
    }


    private static void doCompare(Connection conn, ResultSetDifferentiationTellerSingle teller) throws SQLException {
        ResultSet firstRs = SQLExecutor.executeQuery(conn, String.format(SQL_SELECT, FIRST_TABLE_NAME));
        ResultSet secondRs = SQLExecutor.executeQuery(conn, String.format(SQL_SELECT, SECOND_TABLE_NAME));
        teller.setResultSet1(firstRs);
        teller.setResultSet2(secondRs);
        teller.setObjectKeyPropertyName("id");
        teller.compare();
    }

    private static final String SQL_SELECT = "SELECT * from %s";
    private static final String FIRST_TABLE_NAME = "test_differentiationTeller_result_table_1";
    private static final String SECOND_TABLE_NAME = "test_differentiationTeller_result_table_2";

    private void loadData(String rootDirectory) throws IOException, SQLException {


        ClassLoader classLoader = this.getClass().getClassLoader();
        try (Connection conn = getConnection()){

            InputStream firstDataSet = classLoader.getResourceAsStream("DifferentiationTeller/" + rootDirectory + "/test_differentiationTeller_result_table_1.sql");
            InputStream secondDataSet = classLoader.getResourceAsStream("DifferentiationTeller/" + rootDirectory + "/test_differentiationTeller_result_table_2.sql");


            SQLExecutor.executeBunchSQLs(conn, firstDataSet, "utf-8");
            SQLExecutor.executeBunchSQLs(conn, secondDataSet, "utf-8");
        }
    }

    private Connection getConnection() throws IOException {
        /*
        JSONObject config = JSONUtils.getJsonObjectFromInputStream(this.getClass().getClassLoader().getResourceAsStream("config/init.json"));
        GlobalApplicationContext.init(config);
        */
        ApplicationContextInitiator.init();
        return GlobalApplicationContext.getInstance().getCoreConnection();
    }
}
