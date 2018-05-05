package com.doubeye.experiments.hadoop.commons;

import org.apache.hadoop.io.Text;

public class NcdcStationMetadataParser {
    private String stationId;
    private String stationName;

    public String getStationId() {
        return stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public boolean parse(String record) {
        if (record.length() < 43) {
            return false;
        }
        String usaf = record.substring(0, 6);
        String wban = record.substring(7, 12);
        stationId = usaf + "-" + wban;
        stationName = record.substring(13, 42);
        try {
            Integer.parseInt(usaf);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean parse(Text record) {
        return parse(record.toString());
    }
}