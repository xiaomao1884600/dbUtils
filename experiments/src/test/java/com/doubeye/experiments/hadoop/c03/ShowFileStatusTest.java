package com.doubeye.experiments.hadoop.c03;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.CoreMatchers.is;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class ShowFileStatusTest {
    //private MiniDFSCluster cluster;
    private FileSystem fs;
    @Before
    public void setUp() throws IOException {
        Configuration configuration = new Configuration();
        if (System.getProperty("test.build.data") == null) {
            System.setProperty("test.build.data", "/tmp");
        }
        /*
        cluster = new MiniDFSCluster.Builder(configuration).build();
        fs = cluster.getFileSystem();
        */
        OutputStream out = fs.create(new Path("/dir/file"));
        out.write("content".getBytes("UTF-8"));
        out.close();
    }
    @After
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
    @Test(expected = FileNotFoundException.class)
    public void throwsFileNotFoundForNonExistentFile() throws IOException {
        fs.getFileStatus(new Path("no-such-file"));
    }
    @Test
    public void fileStatusForFile() throws IOException {
        Path file = new Path("/dir/file");
        FileStatus status = fs.getFileStatus(file);

        assertThat(status.getPath().toUri().getPath(), is("/dir/file"));
        assertThat(status.isDirectory(), is(false));
        assertThat(status.getLen(), is(7L));
        assertThat(status.getModificationTime(), is(lessThanOrEqualTo(System.currentTimeMillis())));
        assertThat(status.getReplication(), is((short)3));
        assertThat(status.getBlockSize(), is(128 * 1024 * 1024L));
        assertThat(status.getOwner(), is(System.getProperty("user.name")));
        assertThat(status.getPermission().toString(), is("rw-r--r--"));
    }
    @Test
    public void fileStatusForDirectory() throws IOException {
        Path dir = new Path("/dir");

        FileStatus status = fs.getFileStatus(dir);

        assertThat(status.getPath().toUri().getPath(), is("/dir"));
        assertThat(status.isDirectory(), is(true));
        assertThat(status.getLen(), is(0L));
        assertThat(status.getModificationTime(), is(lessThanOrEqualTo(System.currentTimeMillis())));
        assertThat(status.getReplication(), is((short)0));
        assertThat(status.getBlockSize(), is(0L));
        assertThat(status.getOwner(), is(System.getProperty("user.name")));
        assertThat(status.getGroup(), is("supergroup"));
        assertThat(status.getPermission().toString(), is("rw-r--r--"));
    }
}
