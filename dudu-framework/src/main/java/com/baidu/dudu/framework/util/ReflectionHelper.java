package com.baidu.dudu.framework.util;


import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionHelper {

    private static Logger log = LoggerFactory.getLogger(ReflectionHelper.class);

    private static final String GET_PREFIX = "get";

    private static final String IS_PREFIX = "is";

    public static class ObjectParameters {

        private final Object target;

        private final Map parameters = new LinkedHashMap();

        protected ObjectParameters(Object target) {
            this.target = target;
        }

        protected void addParameter(String name, Method method) {
            parameters.put(name, method);
        }

        protected void addAll(ObjectParameters parameters) {
            this.parameters.putAll(parameters.parameters);
        }

        public Iterator namesIterator() {
            return parameters.keySet().iterator();
        }

        public int size() {
            return parameters.size();
        }

        public Object valueForName(String name) {
            if (parameters.containsKey(name)) {
                Method method = (Method) parameters.get(name);
                try {
                    method.setAccessible(true);
                    return method.invoke(target, null);
                }
                catch (Exception e) {
                    log.error("Could not invoke " + name + " on object " + target.getClass().getName(), e);
                }
            }
            return null;
        }

        public Method methodForName(String name) {
            return (Method) parameters.get(name);
        }

        public Class classForName(String name) {
            if (parameters.containsKey(name)) {
                return ((Method) parameters.get(name)).getReturnType();
            }
            return null;
        }

        public Iterator orderedNamesIterator() {
            TreeSet names = new TreeSet(parameters.keySet());
            return names.iterator();
        }
    }

    public static ObjectParameters parameters(Object o) {
        Class type = o.getClass();
        ObjectParameters result = new ObjectParameters(o);
        while ((type.isPrimitive() == false) && (type.getPackage().getName().startsWith("java.lang") == false)) {
            result.addAll(parametersForType(o, type));
            type = type.getSuperclass();
        }
        return result;
    }

    public static String shortClassName(Class classObject) {
        if (classObject == null) {
            return "<null>";
        }

        String className = classObject.getName();
        Package packageObject = classObject.getPackage();
        if (packageObject == null) {
            return className;
        }

        String packageName = packageObject.getName();
        if (packageName == null) {
            return className;
        }

        int packageLength = packageName.length();
        if (packageLength <= 0) {
            return className;
        }

        return className.substring(packageLength + 1);
    }

    public static boolean classBelongsToPackage(Class classObject, String packageName) {
        if (classObject == null) {
            return false;
        }

        Package packageObjectOfClass = classObject.getPackage();
        if (packageObjectOfClass == null) {
            return false;
        }

        String packageNameOfClass = packageObjectOfClass.getName();
        if (packageNameOfClass == null) {
            return false;
        }

        return packageName.startsWith(packageNameOfClass);
    }

    public static Object getPropertyValue(Object object, String parameter) {
        try {

            PropertyDescriptor descriptor = new PropertyDescriptor(parameter, object.getClass(), "get" + capitalize(parameter), null);
            Method readMethod = descriptor.getReadMethod();
            if (readMethod != null) {
                return readMethod.invoke(object, null);
            }
        }
        catch (Exception e) {
            // Ignore exception on invoking the getMethod and just result null
            // since we cannot fetch the value
        }
        return null;
    }

    public static Object getPropertyArrayValue(Object object, String parameter, int index) {
        try {
            PropertyDescriptor descriptor = new PropertyDescriptor(parameter, object.getClass(), "get" + capitalize(parameter), null);
            Method readMethod = descriptor.getReadMethod();
            if (readMethod != null) {
                object = readMethod.invoke(object, null);
                if (object == null) {
                    return null;
                }
                return Array.get(object, index);
            }
        }
        catch (Exception e) {
            // Ignore exception on invoking the getMethod and just result null
            // since we cannot fetch the value
        }
        return null;
    }

    static String capitalize(String s) {
        if ((s == null) || (s.length() == 0)) {
            return s;
        }
        char chars[] = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static Object getFieldValue(Object object, String property) {
        return null;
    }

    private static ObjectParameters parametersForType(Object o, Class type) {
        Method[] methods = type.getDeclaredMethods();
        ObjectParameters result = new ObjectParameters(o);
        if (methods != null) {
            for (int i = 0; i < methods.length; i++) {
                if (isGetter(methods[i])) {
                    result.addParameter(methods[i].getName().substring(GET_PREFIX.length()), methods[i]);
                }
                else if (isBoolean(methods[i])) {
                    result.addParameter(methods[i].getName().substring(IS_PREFIX.length()), methods[i]);
                }
            }
        }
        return result;
    }

    private static boolean isGetter(Method method) {
        return method.getName().startsWith(GET_PREFIX) && (method.getParameterTypes().length == 0);
    }

    private static boolean isBoolean(Method method) {
        return method.getName().startsWith(IS_PREFIX) && (method.getParameterTypes().length == 0);
    }

}