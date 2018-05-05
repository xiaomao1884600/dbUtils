package com.doubeye.commons.utils.collection.zhaopin;

import com.doubeye.commons.utils.collection.CollectionUtils;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ZhaopinKeyValueConverter {

    private KeyValueDecoder decoder;

    public JSONArray fromZhaopinKeyValue(String origin) {
        Logger logger = LogManager.getLogger(ZhaopinKeyValueConverter.class);
        JSONArray array = new JSONArray();
        List<String> entries = CollectionUtils.split(origin, "@");
        entries.forEach(entry -> {
            String[] values = entry.split("\\|");
            try {
                if (!StringUtils.isEmpty(entry)) {
                    array.add(decoder.decode(values));
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        });
        return array;
    }

    public void setDecoder(KeyValueDecoder decoder) {
        this.decoder = decoder;
    }
}
