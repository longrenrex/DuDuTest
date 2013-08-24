package com.baidu.dudu.framework;


import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Visitor;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.baidu.dudu.framework.core.DuDuTestManager;
import com.baidu.dudu.framework.interaction.TestInteraction;
import com.baidu.dudu.framework.util.XmlUtils;

/**
 * @author rzhao
 */
public abstract class DuDuTest {

    private static final Logger logger = LoggerFactory.getLogger(DuDuTest.class);

    private static ApplicationContext context;
    private static DuDuTestManager testManager;
    private static TestInteraction interaciton;

    private static XmlUtils xmlUtils;

    protected final static Properties prop;

    static {
        prop = new Properties();
        try {
            prop.load(new ClassPathResource("config.properties").getInputStream());
        }
        catch (IOException e) {
            logger.error("load config.properties error!", e);
        }

        context = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml", "applicationContext-framework.xml", "applicationContext-plugin.xml", });
        testManager = (DuDuTestManager) context.getBean("duduTestManager");
        testManager.setContext(context);
        interaciton = testManager.getTestInteraction();
        xmlUtils = new XmlUtils();
    }

    public abstract void setUp(TestInteraction testInteraction);

    public abstract void testStart(TestInteraction testInteraction);

    public abstract void tearDown(TestInteraction testInteraction);

    public void toXML(Object o) {
        xmlUtils.toXML(o);
    }

    public void fromXML(Object o) {
        xmlUtils.fromJson(o);
    }

    public void toJson(Object o) {
        xmlUtils.toJson(o);
    }

    public void fromJson(Object o) {
        xmlUtils.fromJson(o);
    }

    @Before
    public void beforeTest() {
        interaciton.beforeSetUp();
        setUp(interaciton);
        interaciton.afterSetUp();
    }

    @Test
    public void runTest() {
        interaciton.beforeTest(interaciton);
        testStart(interaciton);
        interaciton.afterTest(interaciton);
    }

    @After
    public void afterTest() {
        interaciton.beforeTearDown();
        tearDown(interaciton);
        interaciton.afterTearDown();
    }

    protected StringReader convertVariables(File aFile) {
        SAXReader xmlReader = new SAXReader();
        Document document = null;
        try {
            document = xmlReader.read(aFile);
            Visitor visitor = new VisitorSupport() {

                public void visit(Element element) {
                    element.setText(getVariableValue(element.getText()));

                }
            };
            document.accept(visitor);
        }
        catch (DocumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new StringReader(document.asXML());

    }

    protected static String getVariableValue(String v) {
        int start = v.indexOf("${");
        int end = v.indexOf("}");
        if ((start < 0) || (end < 0) || (start > end))
            return v;

        String key = v.substring(start + 2, end);

        Object objValue = prop.get(key);
        if (objValue == null) {
            logger.debug("not found variable as {}", key);
            return v;
        }

        return objValue.toString();
        //        value = v.substring(0, start) + objValue.toString() + v.substring(end + 1);
        //        return getVariableValue(value);
    }

}
