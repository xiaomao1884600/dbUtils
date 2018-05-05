package com.doubeye.commons.utils.net;

import java.io.IOException;

public interface UrlPageContentGetter {
    String get(String url) throws IOException;
    void setCharset(String charset);
}
