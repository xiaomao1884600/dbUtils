package com.doubeye.experiments.hadoop.c17;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.hadoop.io.DoubleWritable;

public class Mean extends UDAF{
    public static class MeanDoubleUDAFEvaluator implements UDAFEvaluator {
        public static class PartialResult {
            double sum;
            long count;
        }
        private PartialResult partialResult;
        @Override
        public void init() {
            partialResult = null;
        }

        public boolean iterate(DoubleWritable value) {
            if (value == null) {
                return true;
            }
            if (partialResult == null) {
                partialResult = new PartialResult();
            }
            partialResult.sum += value.get();
            partialResult.count ++;
            return true;
        }
        public MeanDoubleUDAFEvaluator.PartialResult terminatePartial() {
            return partialResult;
        }

        public boolean merge(PartialResult other) {
            if (other == null) {
                return true;
            }
            if (partialResult == null) {
                partialResult = new PartialResult();
            }
            partialResult.sum += other.sum;
            partialResult.count += other.count;
            return true;
        }

        public DoubleWritable terminate() {
            if (partialResult == null) {
                return null;
            }
            return new DoubleWritable(partialResult.sum / partialResult.count);
        }

    }
}
