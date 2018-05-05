package com.doubeye.experiments.hadoop.c06.v4;

import org.apache.hadoop.io.Text;

public class NcdcRecordParser {
    private static final int MISSING_TEMPERATURE = 9999;

    private String year;
    private int airTemperature;
    private String quality;
    private boolean airTemperatureMalformed;

    public String getStationId() {
        return stationId;
    }

    private String stationId;

    public void parse(String record) {
        year = record.substring(15, 19);
        airTemperatureMalformed = false;
        String airTemperatureString;
        if (record.charAt(87) == '+') {
            airTemperatureString = record.substring(88, 92);
            airTemperature = Integer.parseInt(airTemperatureString);
        } else if (record.charAt(87) == '-'){
            airTemperatureString = record.substring(87, 92);
            airTemperature = Integer.parseInt(airTemperatureString);
        } else {
            airTemperatureMalformed = true;
        }
        quality = record.substring(92, 93);
        stationId = record.substring(4, 9) + "-" + record.substring(10, 14);
    }

    public void parse(Text record) {
        parse(record.toString());
    }

    public String getYear() {
        return year;
    }

    public int getAirTemperature() {
        return airTemperature;
    }

    public boolean isValidTemperature() {
        return airTemperature != MISSING_TEMPERATURE && quality.matches("[01459]");
    }

    public boolean isAirTemperatureMalformed() {
        return airTemperatureMalformed;
    }
}

