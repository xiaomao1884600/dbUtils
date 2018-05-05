package com.doubeye.experiments.hadoop.c18;


import com.doubeye.experiments.hadoop.commons.NcdcRecordParser;
import org.apache.crunch.*;
import org.apache.crunch.fn.Aggregators;
import org.apache.crunch.impl.mr.MRPipeline;
import org.apache.crunch.io.To;

import static org.apache.crunch.types.writable.Writables.ints;
import static org.apache.crunch.types.writable.Writables.strings;
import static org.apache.crunch.types.writable.Writables.tableOf;

public class MaxTemperatureCrunch {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: MaxTemperatureCrunch <input path> <output path>");
            System.exit(-1);
        }
        Pipeline pipeline = new MRPipeline(MaxTemperatureCrunch.class);
        PCollection<String> records = pipeline.readTextFile(args[0]);

        PTable<String, Integer> yearTemperature = records.parallelDo(toYearTempPairsFn(), tableOf(strings(), ints()));

        PTable<String, Integer> maxTemps = yearTemperature.groupByKey().combineValues(Aggregators.MAX_INTS());;

        maxTemps.write(To.textFile(args[1]));
        PipelineResult result = pipeline.done();
        System.exit(result.succeeded() ? 0 : 1);
    }

    static DoFn<String, Pair<String, Integer>> toYearTempPairsFn() {
        return new DoFn<String, Pair<String, Integer>>() {
            NcdcRecordParser parser = new NcdcRecordParser();
            @Override
            public void process(String input, Emitter<Pair<String, Integer>> emitter) {
                parser.parse(input);
                if (parser.isValidTemperature()) {
                    emitter.emit(Pair.of(parser.getYear(), parser.getAirTemperature()));
                }
            }
        };
    }
}
