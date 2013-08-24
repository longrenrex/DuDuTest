package com.baidu.dudu.framework.util;


import java.util.ArrayList;
import java.util.List;

/**
 * @author rzhao
 */
public class DuDuByteUtil {

    public static byte[] convert(Byte[] byteValues) {

        if (byteValues == null) {
            return null;
        }

        byte[] bytes = new byte[byteValues.length];

        for (int i = 0; i < byteValues.length; i++) {
            bytes[i] = byteValues[i].byteValue();
        }

        return bytes;
    }

    public static List<Byte[]> union(Byte[]... byteArrays) {
        if (byteArrays == null) {
            return null;
        }

        List<Byte[]> byteArrayList = new ArrayList<Byte[]>();
        for (Byte[] bs : byteArrays) {
            byteArrayList.add(bs);
        }
        return byteArrayList;
    }

    public static byte[] union(byte[]... byteArrays) {

        if (byteArrays == null) {
            return null;
        }

        List<byte[]> byteArrayList = new ArrayList<byte[]>();
        for (byte[] bs : byteArrays) {
            byteArrayList.add(bs);
        }
        byte[] bytes = convert(byteArrayList);

        return bytes;
    }

    public static byte[] convert(List<byte[]> byteList) {
        int size = 0;
        for (byte[] bs : byteList) {
            size += bs.length;
        }
        byte[] ret = new byte[size];
        int pos = 0;
        for (byte[] bs : byteList) {
            System.arraycopy(bs, 0, ret, pos, bs.length);
            pos += bs.length;
        }
        return ret;
    }
}
