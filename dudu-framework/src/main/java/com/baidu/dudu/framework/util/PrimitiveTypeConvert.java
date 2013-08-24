package com.baidu.dudu.framework.util;


/**
 * @author rzhao
 */
public class PrimitiveTypeConvert implements net.sf.cglib.core.Converter {

    /*
     * (non-Javadoc)
     * @see net.sf.cglib.core.Converter#convert(java.lang.Object, java.lang.Class, java.lang.Object)
     */
    @Override
    public Object convert(Object value, Class target, Object context) {

        if (value instanceof byte[] && target.isAssignableFrom(Byte[].class)) {
            byte[] byteValues = (byte[]) value;
            Byte[] byteObjects = new Byte[byteValues.length];
            for (int i = 0; i < byteObjects.length; i++) {
                byteObjects[i] = Byte.valueOf(byteValues[i]);
            }
            return byteObjects;
        }

        return value;
    }
}
