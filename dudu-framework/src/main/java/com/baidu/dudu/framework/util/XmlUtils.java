package com.baidu.dudu.framework.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.dudu.framework.exception.DuDuException;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class XmlUtils {

    private static Logger logger = LoggerFactory.getLogger(XmlUtils.class);

    private XStream xstream;
    private XStream jxstream;

    public XmlUtils() {
        xstream = new XStream();
        jxstream = new XStream(new JsonHierarchicalStreamDriver());
    }

    public void toXML(Object o) {
        toXML(null, o);
    }

    public void toXML(String name, Object o) {
        FileOutputStream fos;
        try {
            String filePath = null;
            if (StringUtils.isEmpty(name)) {
                filePath = ".\\xml_data\\" + o.getClass().getName() + ".xml";
            }
            else {
                filePath = ".\\xml_data\\" + name + ".xml";
            }
            fos = new FileOutputStream(new File(filePath));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos, Charset.forName("utf-8"));
            xstream.toXML(o, outputStreamWriter);
        }
        catch (FileNotFoundException e) {
            logger.error("save object to xml error!", e);
            throw new DuDuException("save object to xml error!", e);
        }
    }

    public void fromXML(Object o) {
        fromXML(null, o);
    }

    public void fromXML(String name, Object o) {
        try {
            String filePath = null;
            if (StringUtils.isEmpty(name)) {
                filePath = ".\\xml_data\\" + o.getClass().getName() + ".xml";
            }
            else {
                filePath = ".\\xml_data\\" + name + ".xml";
            }

            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                xstream.fromXML(fileInputStream, o);
            }
        }
        catch (FileNotFoundException e) {
            logger.error("read object from xml error!", e);
            throw new DuDuException("read object from xml error!", e);
        }
    }

    public void toJson(Object o) {
        toJson(null, o);
    }

    public void toJson(String name, Object o) {
        FileOutputStream fos;
        try {
            String filePath = null;
            if (StringUtils.isEmpty(name)) {
                filePath = ".\\json_data\\" + o.getClass().getName() + ".json";
            }
            else {
                filePath = ".\\json_data\\" + name + ".json";
            }
            fos = new FileOutputStream(new File(filePath));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos, Charset.forName("utf-8"));
            jxstream.toXML(o, outputStreamWriter);
        }
        catch (FileNotFoundException e) {
            logger.error("save object to json error!", e);
            throw new DuDuException("save object to json error!", e);
        }
    }

    public void fromJson(Object o) {
        fromJson(null, o);
    }

    public void fromJson(String name, Object o) {
        try {
            String filePath = null;
            if (StringUtils.isEmpty(name)) {
                filePath = ".\\json_data\\" + o.getClass().getName() + ".json";
            }
            else {
                filePath = ".\\json_data\\" + name + ".json";
            }

            File file = new File(filePath);

            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(filePath);
                jxstream.fromXML(fileInputStream, o);
            }
        }
        catch (FileNotFoundException e) {
            logger.error("read object from json error!", e);
            throw new DuDuException("read object from json error!", e);
        }
    }
}
