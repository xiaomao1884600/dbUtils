package com.doubeye.experiments.hadoop.commons;

//import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NcdcStationMetadata {
    private Map<String, String> stationIdToName = new HashMap<>();

    public void initialize(File file) throws IOException {
        NcdcStationMetadataParser parser = new NcdcStationMetadataParser();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (parser.parse(line)) {
                    stationIdToName.put(parser.getStationId(), parser.getStationName());
                }
            }
        }
    }

    public String getStationName(String stationId) {
        String stationName = stationIdToName.get(stationId);
        if (stationName == null || stationName.length() == 0) {
            return stationId;
        }
        return stationName;
    }

    public Map<String, String> getStationIdToNameMap() {
        return Collections.unmodifiableMap(stationIdToName);
    }
}
