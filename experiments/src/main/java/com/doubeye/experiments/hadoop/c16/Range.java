package com.doubeye.experiments.hadoop.c16;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Range {
    private final int start;
    private final int end;

    public Range(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getSubstring(String line) {
        return line.substring(start - 1, end);
    }
    @Override
    public int hashCode() {
        return start * 37 + end;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Range)) {
            return false;
        }
        Range other = (Range) object;
        return this.start == other.start && this.end == other.end;
    }

    public static List<Range> parse(String rangeSpec) {
        if (rangeSpec.length() == 0) {
            return Collections.emptyList();
        }
        List<Range> ranges = new ArrayList<>();
        String[] specs = rangeSpec.split(",");
        for (String spec : specs) {
            String[] split = spec.split("-");
            try {
                ranges.add(new Range(Integer.parseInt(split[0]), Integer.parseInt(split[1])));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        return ranges;
    }
}
