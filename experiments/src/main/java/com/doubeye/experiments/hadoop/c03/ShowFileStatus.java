package com.doubeye.experiments.hadoop.c03;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.hdfs.MiniDFSCluster;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class ShowFileStatus {

    //private MiniDFSCluster cluster;
    private FileSystem fs;
    /**
     * hadoop jar d:\experiments.jar com.doubeye.experiments.hadoop.c03.ShowFileStatus
     */
    public static void main(String[] args) throws IOException {
        ShowFileStatus showFileStatus = new ShowFileStatus();
        try {
            showFileStatus.setUp();
            showFileStatus.fileStatusForFile();
            showFileStatus.fileStatusForDirectory();
            try {
                showFileStatus.throwsFileNotFoundForNonExistentFile();
            } catch (FileNotFoundException e) {
                System.out.println("没有找到文件");
            }
        } finally {
            showFileStatus.tearDown();
        }
    }


    public void setUp() throws IOException {
        /*
        Configuration configuration = new Configuration();

        if (System.getProperty("test.build.data") == null) {
            System.setProperty("test.build.data", "/tmp");
        }

        cluster = new MiniDFSCluster.Builder(configuration).build();
        fs = cluster.getFileSystem();
        OutputStream out = fs.create(new Path("/dir/file"));
        out.write("content".getBytes("UTF-8"));
        out.close();
        */
    }

    public void tearDown() throws IOException {
        if (fs != null) {
            fs.close();
        }
        /*
        if (cluster != null) {
            cluster.close();
        }
        */

    }

    public void throwsFileNotFoundForNonExistentFile() throws IOException {
        fs.getFileStatus(new Path("no-such-file"));
    }

    public void fileStatusForFile() throws IOException {
        //Path file = new Path("/data/1901.txt");
        Path file = new Path("/dir/file");
        FileStatus status = fs.getFileStatus(file);

        System.out.println("path = " + status.getPath().toUri().getPath());
        System.out.println("is directory = " + status.isDirectory());
        System.out.println("size = " + status.getLen());
        System.out.println("modified time = " + status.getModificationTime());
        System.out.println("replication = " + status.getReplication());
        System.out.println("block size = " + status.getBlockSize());
        System.out.println("owner = " + status.getOwner());
        System.out.println("permission = " + status.getPermission().toString());
    }

    public void fileStatusForDirectory() throws IOException {
        //Path dir = new Path("/data");
        Path dir = new Path("/dir");

        FileStatus status = fs.getFileStatus(dir);

        System.out.println("path = " + status.getPath().toUri().getPath());
        System.out.println("is directory = " + status.isDirectory());
        System.out.println("size = " + status.getLen());
        System.out.println("modified time = " + status.getModificationTime());
        System.out.println("replication = " + status.getReplication());
        System.out.println("block size = " + status.getBlockSize());
        System.out.println("owner = " + status.getOwner());
        System.out.println("group = " + status.getGroup());
        System.out.println("permission = " + status.getPermission().toString());
    }

}
