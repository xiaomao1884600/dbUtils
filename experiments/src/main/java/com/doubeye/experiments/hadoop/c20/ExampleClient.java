package com.doubeye.experiments.hadoop.c20;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

public class ExampleClient {
    public static void main(String[] args) throws IOException {
        Configuration config = HBaseConfiguration.create();
        Connection connection = ConnectionFactory.createConnection(config);
        try (Admin admin = connection.getAdmin()) {
            TableName tableName = TableName.valueOf("test");
            HTableDescriptor htd = new HTableDescriptor(tableName);
            HColumnDescriptor hcd = new HColumnDescriptor("data");
            htd.addFamily(hcd);
            HTableDescriptor[] tables = admin.listTables();
            if (tables.length != 1 && Bytes.equals(tableName.getName(), tables[0].getTableName().getName())) {
                throw new IOException("Failed create of table");
            }

            try (Table table = connection.getTable(tableName)) {

                for (int i = 1; i <= 3; i ++) {
                    byte[] row = Bytes.toBytes("row" + i);
                    Put put = new Put(row);
                    byte[] columnFamily = Bytes.toBytes("data");
                    byte[] qualifier = Bytes.toBytes(String.valueOf(i));
                    byte[] value = Bytes.toBytes("value" + i);
                    put.add(columnFamily, qualifier, value);
                    table.put(put);
                }
                Get get = new Get(Bytes.toBytes("row1"));
                Result result = table.get(get);
                System.out.println("Get : " + result);
                Scan scan = new Scan();
                try (ResultScanner scanner = table.getScanner(scan)) {
                    for (Result scannerResult : scanner) {
                        System.out.println("Scan:" + scannerResult);
                    }
                }
                admin.disableTable(tableName);
                admin.deleteTable(tableName);
            }
        }

    }
}
