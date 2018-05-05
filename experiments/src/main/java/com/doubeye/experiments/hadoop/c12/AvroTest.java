package com.doubeye.experiments.hadoop.c12;


import com.sun.corba.se.spi.orb.StringPair;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;


import java.io.*;

public class AvroTest {

    public void testWriteRead() throws IOException {
        Schema.Parser parser = new Schema.Parser();

        File file = new File("D:\\workcode\\dbUtils\\experiments\\src\\main\\resources/StringPair.avsc");
        File newFile = new File("D:\\workcode\\dbUtils\\experiments\\src\\main\\resources/StringPairNew.avsc");




        Schema schema = parser.parse(new FileInputStream(file));
        Schema.Parser newParser = new Schema.Parser();
        Schema schemaNew = newParser.parse(new FileInputStream(newFile));
        //Schema schema = parser.parse(getClass().getResourceAsStream("StringPair.avsc"));

        GenericRecord datum = new GenericData.Record(schema);
        datum.put("left", "L");
        datum.put("right", "R");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(datum, encoder);
        encoder.flush();
        out.close();

        //DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
        DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema, schemaNew);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null);
        GenericRecord result = reader.read(null, decoder);
        System.out.println(result.get("left") + " " + result.get("right"));

    }

    public void stringPairTest() throws IOException {
        /*
        StringPair datum = new StringPair();
        datum.setLeft("L");
        datum.setRight("R");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<StringPair> writer = new SpecificDatumWriter<>(StringPair.class);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(datum, encoder);
        encoder.flush();
        out.close();

        DatumReader<StringPair> reader = new SpecificDatumReader<StringPair>(StringPair.class);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(), null);
        StringPair result = reader.read(null, decoder);
        */

        /*StringPair datum = new StringPair();
        datum.setLeft("L");
        datum.setRight("R");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DatumWriter<StringPair> writer =
                new SpecificDatumWriter<StringPair>(StringPair.class);
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(datum, encoder);
        encoder.flush();
        out.close();

        DatumReader<StringPair> reader =
                new SpecificDatumReader<StringPair>(StringPair.class);
        Decoder decoder = DecoderFactory.get().binaryDecoder(out.toByteArray(),
                null);
        StringPair result = reader.read(null, decoder);

        */
    }

    public void writeDataFile() throws IOException {
        Schema.Parser parser = new Schema.Parser();

        File file = new File("D:\\workcode\\dbUtils\\experiments\\src\\main\\resources/StringPair.avsc");
        File newFile = new File("D:\\workcode\\dbUtils\\experiments\\src\\main\\resources/StringPairNew.avsc");


        Schema schema = parser.parse(new FileInputStream(file));
        Schema.Parser newParser = new Schema.Parser();
        Schema schemaNew = newParser.parse(new FileInputStream(newFile));
        //Schema schema = parser.parse(getClass().getResourceAsStream("StringPair.avsc"));

        GenericRecord datum = new GenericData.Record(schema);
        datum.put("left", "L");
        datum.put("right", "R");

        File dataFile = new File("d:/dataFile.avro");
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<>(writer);
        dataFileWriter.create(schema, dataFile);
        dataFileWriter.append(datum);
        dataFileWriter.close();

        //DatumReader<GenericRecord> reader = new GenericDatumReader<>();
        DatumReader<GenericRecord> reader = new GenericDatumReader<>(null, schemaNew);
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<GenericRecord>(dataFile, reader);
        System.out.println(dataFileReader.getSchema());
        GenericRecord result = null;
        while (dataFileReader.hasNext()) {
            result = dataFileReader.next(result);
            System.out.println(result.get("left") + " " + result.get("right"));
        }
    }


    public static void main(String[] args) throws IOException {

        AvroTest test = new AvroTest();
        test.testWriteRead();
        //test.stringPairTest();
        test.writeDataFile();

    }
}
