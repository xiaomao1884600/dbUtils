
package com.doubeye.experiments.hadoop.c13;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.avro.AvroReadSupport;
import org.apache.parquet.column.ParquetProperties;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.GroupFactory;
import org.apache.parquet.example.data.simple.SimpleGroupFactory;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.hadoop.example.GroupWriteSupport;

import org.apache.parquet.schema.MessageType;
import org.apache.parquet.schema.MessageTypeParser;


import java.io.IOException;


public class ParquetTest {


    private static void testParquet() throws IOException {

        MessageType schema = MessageTypeParser.parseMessageType("message Pair {\n" +
                " required binary left (UTF8);\n" +
                " required binary right (UTF8);\n" +
            "}");

        GroupFactory groupFactory = new SimpleGroupFactory(schema);
        Group group = groupFactory.newGroup().append("left", "L").append("right", "R");

        Configuration conf = new Configuration();
        Path path = new Path("D:/data.parquet");
        GroupWriteSupport writeSupport = new GroupWriteSupport();
        GroupWriteSupport.setSchema(schema, conf);


        //ParquetWriter<Group> writer = new ParquetWriter(path, writeSupport, CompressionCodecName.UNCOMPRESSED, ParquetWriter.DEFAULT_BLOCK_SIZE, ParquetWriter.DEFAULT_PAGE_SIZE,  true, true);
        ParquetWriter<Group> writer = new ParquetWriter<Group>(path, writeSupport,
                ParquetWriter.DEFAULT_COMPRESSION_CODEC_NAME,
                ParquetWriter.DEFAULT_BLOCK_SIZE,
                ParquetWriter.DEFAULT_PAGE_SIZE,
                ParquetWriter.DEFAULT_PAGE_SIZE, /* dictionary page size */
                ParquetWriter.DEFAULT_IS_DICTIONARY_ENABLED,
                ParquetWriter.DEFAULT_IS_VALIDATING_ENABLED,
                ParquetProperties.WriterVersion.PARQUET_1_0, conf);
        writer.write(group);
        writer.close();

        GroupReadSupport readSupport = new GroupReadSupport();
        ParquetReader<Group> reader = new ParquetReader(path, readSupport);

        Group result = reader.read();
        System.out.println(result.getString("left", 0) + " " + result.getString("right", 0));
    }

    private static void testAvro() throws IOException {

        String schema = "{\n" +
                "\"type\": \"record\",\n" +
                "\"name\": \"StringPair\",\n" +
                "\"doc\": \"A pair of strings.\",\n" +
                "\"fields\": [\n" +
                "{\"name\": \"left\", \"type\": \"string\"},\n" +
                "{\"name\": \"right\", \"type\": \"string\"}\n" +
                "]\n" +
                "}";
        Schema.Parser parser = new Schema.Parser();
        Schema sch = parser.parse(schema);

        GenericRecord datum = new GenericData.Record(sch);
        datum.put("left", "L");
        datum.put("right", "R");

        Path path = new Path("d:/data.parquet");
        AvroParquetWriter<GenericRecord> writer = new AvroParquetWriter(path, sch);
        writer.write(datum);
        writer.close();



        AvroParquetReader<GenericRecord> reader = new AvroParquetReader(path);
        GenericRecord result = reader.read();

        System.out.println(result.get("left") + " " + result.get("right"));

        String projection = "{\n" +
                "\"type\": \"record\",\n" +
                "\"name\": \"StringPair\",\n" +
                "\"doc\": \"The right field of a pair of strings.\",\n" +
                "\"fields\": [\n" +
                "{\"name\": \"right\", \"type\": \"string\"}\n" +
                "]\n" +
                "}";
        Schema.Parser parserProject = new Schema.Parser();
        Schema projectionSchema = parserProject.parse(projection);
        Configuration conf = new Configuration();
        AvroReadSupport.setRequestedProjection(conf, projectionSchema);

        AvroParquetReader<GenericRecord> projectionReader = new AvroParquetReader(conf, path);
        GenericRecord projectionResult = projectionReader.read();
        System.out.println(projectionResult.get("right"));

    }

    public static void main(String[] args) throws IOException {

        //testParquet();
        testAvro();
    }

}