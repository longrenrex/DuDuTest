package com.baidu.dudu.framework.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class StringUtil {

    /**
     * get String from InputStream
     * 
     * @param data
     * @return
     * @throws IOException
     */
    public static String getStringContent(InputStream data) throws IOException {
        Reader in = new BufferedReader(new InputStreamReader(data, "UTF-8"));
        StringBuilder buffer = new StringBuilder();
        char[] buf = new char[1000];
        int l = 0;
        while (l >= 0) {
            buffer.append(buf, 0, l);
            l = in.read(buf);
        }
        return buffer.toString();
    }
}
